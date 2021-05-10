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
public class PlayerPreviousComponent extends PlayerComponent {
    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;

    @Override
    @ComponentName
    public String getComponentName() {
        return COMPONENT_PREVIOUS;
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
        timeline.getWindow(windowIndex, window);
        int previousWindowIndex = player.getPreviousWindowIndex();
        if (previousWindowIndex != C.INDEX_UNSET
                && (player.getCurrentPosition() <= MAX_POSITION_FOR_SEEK_TO_PREVIOUS
                || (window.isDynamic && !window.isSeekable))) {
            dispatchSeekTo(previousWindowIndex, C.TIME_UNSET);
        } else {
            dispatchSeekTo(windowIndex, /* positionMs= */ 0);
        }
    }

    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
        if(null!=viewHolder.previousButton){
            viewHolder.previousButton.setOnClickListener(this);
        }
    }

}
