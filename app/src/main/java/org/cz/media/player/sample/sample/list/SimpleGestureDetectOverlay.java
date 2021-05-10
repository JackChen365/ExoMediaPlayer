package org.cz.media.player.sample.sample.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cz.media.player.base.MediaPlayerView;
import org.cz.media.player.base.controller.component.PlayerComponent;
import org.cz.media.player.library.extension.PlayerGestureLayout;
import org.cz.media.player.library.extension.detect.DoubleTapGestureDetector;
import org.cz.media.player.library.extension.detect.SingleTapGestureDetector;
import org.cz.media.player.library.overlay.MediaPlayerOverlay;
import org.cz.media.player.sample.R;

/**
 * @author Created by cz
 * @date 2020/9/7 11:39 AM
 * @email binigo110@126.com
 */
public class SimpleGestureDetectOverlay extends MediaPlayerOverlay {
    public SimpleGestureDetectOverlay() {
    }

    @Override
    public View onCreateOverlay(Context context, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.media_player_simple_gesture_overlay_layout,parent,false);
    }

    @Override
    public void onAttachToPlayer(MediaPlayerView playerView,View contentView) {
        super.onAttachToPlayer(playerView,contentView);
        //Initialize all the gesture detectors.
        PlayerGestureLayout playerGestureLayout=contentView.findViewById(R.id.simplePlayerGestureLayout);
        SingleTapGestureDetector singleTapGestureDetector = new SingleTapGestureDetector(playerGestureLayout);
        singleTapGestureDetector.setOnSingleTapListener(v -> playerView.performClick());
        playerGestureLayout.addGestureDetector(singleTapGestureDetector);

        DoubleTapGestureDetector doubleTapGestureDetector = new DoubleTapGestureDetector(playerGestureLayout);
        doubleTapGestureDetector.setOnTapListener(v -> {
            PlayerComponent playerComponent = playerView.getPlayerComponent(PlayerComponent.COMPONENT_PLAY_PAUSE);
            if(null!=playerComponent){
                playerComponent.execute();
            }
        });
        playerGestureLayout.addGestureDetector(doubleTapGestureDetector);
    }
}
