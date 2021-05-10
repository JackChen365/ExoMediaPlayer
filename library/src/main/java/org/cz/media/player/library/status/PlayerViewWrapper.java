package org.cz.media.player.library.status;

import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import org.cz.media.player.library.R;
import org.cz.media.player.base.MediaPlayerView;
import org.cz.media.player.base.overlay.FrameOverlayLayout;

/**
 * @author Created by cz
 * @date 2020-05-22 17:09
 * @email binigo110@126.com
 */
public class PlayerViewWrapper extends AbsPlayerViewWrapper {
    public static final int FRAME_CONTAINER=0;
    public static final int FRAME_BUFFERING = R.id.playerBufferingLayout;
    public static final int FRAME_LOAD_ERROR =R.id.playerLoadErrorLayout;
    public static final int FRAME_NETWORK_ERROR =R.id.playerNetworkErrorLayout;

    static{
        registerFrame(R.id.playerBufferingLayout, R.layout.media_view_frame_buffering);
        registerFrame(R.id.playerNetworkErrorLayout, R.layout.media_view_frame_network_error);
        registerFrame(R.id.playerLoadErrorLayout, R.layout.media_view_frame_load_error);
    }

    private MediaPlayerView playerView;

    public PlayerViewWrapper(MediaPlayerView playerView, View hostView) {
        super(hostView, 0);
        this.playerView=playerView;
    }

    public PlayerViewWrapper(MediaPlayerView playerView, ViewGroup hostView, int style) {
        super(hostView, style);
        this.playerView=playerView;
    }

    @Override
    public void onInflateFrameView(int frameId, View view) {
        super.onInflateFrameView(frameId,view);
        if(FRAME_NETWORK_ERROR ==frameId){
            View errorButton=view.findViewById(R.id.errorButton);
            if(null!=errorButton){
                errorButton.setOnClickListener(v -> {
                    Player player = playerView.getPlayer();
                    if(null!=player&& player instanceof SimpleExoPlayer){
                        setFrame(FRAME_BUFFERING);
                        SimpleExoPlayer simpleExoPlayer= (SimpleExoPlayer)player;
                        simpleExoPlayer.retry();
                    }
                });
            }
        } else if(FRAME_LOAD_ERROR == frameId){
            View errorButton=view.findViewById(R.id.errorButton);
            if(null!=errorButton){
                errorButton.setOnClickListener(v -> {
                    Player player = playerView.getPlayer();
                    if(null!=player&& player instanceof SimpleExoPlayer){
                        setFrame(FRAME_BUFFERING);
                        SimpleExoPlayer simpleExoPlayer= (SimpleExoPlayer)player;
                        simpleExoPlayer.retry();
                    }
                });
            }
        }
    }

    @Override
    protected void onFrameShown(int id, View view) {
        super.onFrameShown(id, view);
        FrameOverlayLayout overlayFrameLayout = playerView.getOverlayFrameLayout();
        if(FRAME_CONTAINER == id){
            overlayFrameLayout.setDispatchTouchEvent(true);
        } else {
            overlayFrameLayout.setDispatchTouchEvent(false);
        }
    }
}
