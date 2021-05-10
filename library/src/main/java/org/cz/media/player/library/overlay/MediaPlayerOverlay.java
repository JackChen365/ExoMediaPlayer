package org.cz.media.player.library.overlay;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.cz.media.player.base.MediaPlayerView;

/**
 * @author Created by cz
 * @date 2020/9/7 11:33 AM
 * @email binigo110@126.com
 */
public abstract class MediaPlayerOverlay {

    public abstract View onCreateOverlay(Context context, ViewGroup parent);

    public void attachToPlayerView(Context context, MediaPlayerView playerView){
        View view = onCreateOverlay(context, playerView);
        if(null!=view.getParent()){
            throw new IllegalArgumentException("The view already attach to a ViewGroup. Make sure your view is clear.");
        }
        FrameLayout overlayFrameLayout = playerView.getOverlayFrameLayout();
        overlayFrameLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //Call this function when the view is attach to player.
        onAttachToPlayer(playerView,view);
    }

    public void onAttachToPlayer(MediaPlayerView playerView, View contentView){
    }
}
