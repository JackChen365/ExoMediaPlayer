package org.cz.media.player.base.controller.component;

import android.content.Context;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import org.cz.media.player.base.controller.MediaPlayerViewHolder;

/**
 * @author Created by cz
 * @date 2020/8/13 10:55 AM
 * @email binigo110@126.com
 */
public class PlayerNextComponent extends PlayerComponent {

    @Override
    @ComponentName
    public String getComponentName() {
        return COMPONENT_NEXT;
    }

    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
    }

    @Override
    public void execute() {
        Player player = getPlayer();
        if(null==player) return;
        Timeline timeline = player.getCurrentTimeline();
        if (timeline.isEmpty() || player.isPlayingAd()) {
            return;
        }
        Timeline.Window window = getTimelineWindow();
        int windowIndex = player.getCurrentWindowIndex();
        int nextWindowIndex = player.getNextWindowIndex();
        if (nextWindowIndex != C.INDEX_UNSET) {
            dispatchSeekTo(nextWindowIndex, C.TIME_UNSET);
        } else if (timeline.getWindow(windowIndex, window).isDynamic) {
            dispatchSeekTo(windowIndex, C.TIME_UNSET);
        }
    }

    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
        if(null!=viewHolder.nextButton){
            viewHolder.nextButton.setOnClickListener(this);
        }
    }

}
