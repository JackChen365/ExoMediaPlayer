package org.cz.media.player.library.status.trigger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import org.cz.media.player.base.MediaPlayerView;
import org.cz.media.player.library.status.AbsPlayerViewWrapper;
import org.cz.media.player.library.status.PlayerViewWrapper;

/**
 * @author Created by cz
 * @date 2020/8/18 4:48 PM
 * @email binigo110@126.com
 */
public class PlayerEventTrigger extends FrameTrigger<Integer> implements Player.EventListener {
    private static final String TAG="PlayerEventTrigger";
    private static final int MINIMUM_STATUS_CHANGE_TIME=300;
    private final MediaPlayerView playerView;
    private Runnable backPressureAction;

    public PlayerEventTrigger(@NonNull MediaPlayerView playerView) {
        this.playerView = playerView;
        playerView.addOnPlayerChangeListener((newPlayer, oldPlayer) -> {
            if(null!=newPlayer){
                newPlayer.addListener(this);
            }
            if(null!=oldPlayer){
                oldPlayer.removeListener(this);
            }
        });
    }

    @Override
    protected void trigger(AbsPlayerViewWrapper frameWrapper, @Nullable Integer state) {
        ViewGroup hostView = frameWrapper.getHostView();
        if(null!=backPressureAction){
            hostView.removeCallbacks(backPressureAction);
        }
        backPressureAction= () -> frameWrapper.setFrame(state);
        hostView.postDelayed(backPressureAction,MINIMUM_STATUS_CHANGE_TIME);
    }



    @Override
    public void onPlayerStateChanged(boolean playWhenReady, @Player.State int playbackState) {
        Player player = playerView.getPlayer();
        if(null!=player){
            int showBuffering = playerView.getShowBuffering();
            boolean showBufferingSpinner = player != null && player.getPlaybackState() == Player.STATE_BUFFERING
                            && (showBuffering == MediaPlayerView.SHOW_BUFFERING_ALWAYS
                            || (showBuffering == MediaPlayerView.SHOW_BUFFERING_WHEN_PLAYING && player.getPlayWhenReady()));
            Log.i(TAG,"onPlayerStateChanged:"+showBufferingSpinner);
            @Nullable ExoPlaybackException error = player.getPlaybackError();
            if(showBufferingSpinner){
                //When the MediaPlayer starts to prepare it will always be buffering.
                //So if here we hide the control layout. that will make the show_auto invalid.
                if(player.isLoading()){
                    playerView.hideController();
                    playerView.setControllerEnable(false);
                }
                if(null==error){
                    //change the frame.
                    trigger(PlayerViewWrapper.FRAME_BUFFERING);
                }
            } else {
                //Check the player error.
                @Nullable ExoPlaybackException e = player.getPlaybackError();
                if(null != e){
                    handleException(e);
                } else {
                    playerView.setControllerEnable(true);
                    trigger(PlayerViewWrapper.FRAME_CONTAINER);
                }
            }
        }
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
    public void onPositionDiscontinuity(int reason) {
        //Change the window index Dynamically
        if(Player.TIMELINE_CHANGE_REASON_SOURCE_UPDATE==reason||Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED==reason){
            trigger(PlayerViewWrapper.FRAME_BUFFERING);
        }
    }

    @Override
    public void onDetached() {
        super.onDetached();
        if(null!=playerView){
            Player player = playerView.getPlayer();
            if(null!=player){
                player.removeListener(this);
            }
        }
    }
}
