package org.cz.media.player.library.status.transition;

import android.view.View;
import android.view.ViewGroup;

import org.cz.media.player.library.status.PlayerViewWrapper;

/**
 * @author Created by cz
 * @date 2020-05-23 23:19
 * @email binigo110@126.com
 */
public class ContentFrameTransition extends DefaultFrameTransition {

    @Override
    public void appearingAnimator(ViewGroup parent, View child, int frameId) {
        if(PlayerViewWrapper.FRAME_CONTAINER!=frameId){
            super.appearingAnimator(parent, child, frameId);
        }
    }

    @Override
    public void disappearingAnimator(ViewGroup parent, View child, int frameId) {
        if(PlayerViewWrapper.FRAME_CONTAINER!=frameId){
            super.disappearingAnimator(parent, child, frameId);
        }
    }

    @Override
    public void onFrameDisappear(View child, int frameId) {
        if(PlayerViewWrapper.FRAME_CONTAINER!=frameId){
            super.onFrameDisappear(child, frameId);
        }
    }
}
