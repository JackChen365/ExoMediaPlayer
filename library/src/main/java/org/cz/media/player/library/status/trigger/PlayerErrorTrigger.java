package org.cz.media.player.library.status.trigger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import org.cz.media.player.base.MediaPlayerView;
import org.cz.media.player.library.status.AbsPlayerViewWrapper;
import org.cz.media.player.library.status.PlayerViewWrapper;

/**
 * @author Created by cz
 * @date 2020/9/11 11:44 AM
 * @email bingo110@126.com
 */
public class PlayerErrorTrigger extends FrameTrigger<Integer>{
    private static final String MOBILE_INFO = "MOBILE";
    private static final String WIFI_INFO = "WIFI";
    private Context context;
    private MediaPlayerView playerView;
    private String currentActionNetworkName;
    private NetworkStatusReceiver netWorkReceiver= new NetworkStatusReceiver();

    public PlayerErrorTrigger(Context context, MediaPlayerView mediaPlayerView) {
        this.context = context;
        this.playerView =mediaPlayerView;
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(null!=activeNetworkInfo){
            currentActionNetworkName=activeNetworkInfo.getTypeName();
        }
        context.registerReceiver(netWorkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mediaPlayerView.addOnPlayerChangeListener((newPlayer, oldPlayer) -> {
            if(null!=newPlayer){
                newPlayer.addListener(new Player.EventListener() {
                    @Override
                    public void onPlayerError(ExoPlaybackException e) {
                        mediaPlayerView.hideController();
                        mediaPlayerView.setControllerEnable(false);
                        handleException(e);
                    }
                });
            }
        });
    }

    private void handleException(ExoPlaybackException e) {
        if(ExoPlaybackException.TYPE_SOURCE == e.type){
            Exception sourceException = e.getSourceException();
            if(sourceException instanceof HttpDataSource.HttpDataSourceException||
                    sourceException instanceof HttpDataSource.InvalidResponseCodeException){
                trigger(PlayerViewWrapper.FRAME_NETWORK_ERROR);
            } else {
                // com.google.android.exoplayer2.source.UnrecognizedInputFormatException: None of the available extractors (FlvExtractor, FlacExtractor, WavExtractor, FragmentedMp4Extractor, Mp4Extractor, AmrExtractor, PsExtractor, OggExtractor, TsExtractor, MatroskaExtractor, AdtsExtractor, Ac3Extractor, Ac4Extractor, Mp3Extractor) could read the stream.
                trigger(PlayerViewWrapper.FRAME_LOAD_ERROR);
            }
        } else {
            trigger(PlayerViewWrapper.FRAME_LOAD_ERROR);
        }
    }

    @Override
    protected void trigger(AbsPlayerViewWrapper frameWrapper, @Nullable Integer frameId) {
        frameWrapper.setFrame(frameId);
    }

    @Override
    public boolean isActive() {
        return super.isActive();
    }

    @Override
    public void onAttached() {
        context.registerReceiver(netWorkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onAttached();
    }

    @Override
    public void onDetached() {
        this.context.unregisterReceiver(netWorkReceiver);
        super.onDetached();
    }

    public class NetworkStatusReceiver extends BroadcastReceiver {

        public NetworkStatusReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(null==networkInfo){
                    currentActionNetworkName = null;
                }
                if(null!=networkInfo&&networkInfo.isAvailable()){
                    if ((null==currentActionNetworkName||!currentActionNetworkName.equalsIgnoreCase(networkInfo.getTypeName())&&
                            (MOBILE_INFO.equalsIgnoreCase(networkInfo.getTypeName())|| WIFI_INFO.equalsIgnoreCase(networkInfo.getTypeName())))) {
                        Player player = playerView.getPlayer();
                        if(null!=player&& player instanceof SimpleExoPlayer){
                            SimpleExoPlayer simpleExoPlayer= (SimpleExoPlayer)player;
                            simpleExoPlayer.retry();
                        }
                        playerView.setControllerEnable(true);
                        trigger(PlayerViewWrapper.FRAME_CONTAINER);
                    }
                    currentActionNetworkName = networkInfo.getTypeName();
                }
            }
        }
    }

}
