package org.cz.media.player.base.controller.component;

import android.content.Context;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import org.cz.media.player.base.controller.MediaPlayerViewHolder;

/**
 * @author Created by cz
 * @date 2020/8/13 10:55 AM
 * @email binigo110@126.com
 */
public class PlayerNavigationComponent extends PlayerComponent {

    @Override
    @ComponentName
    public String getComponentName() {
        return COMPONENT_NAVIGATION;
    }

    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
        if (!isVisible() || !isAttachedToWindow()) {
            return;
        }
        @Nullable Player player = getPlayer();
        boolean enableSeeking = false;
        boolean enablePrevious = false;
        boolean enableRewind = false;
        boolean enableFastForward = false;
        boolean enableNext = false;
        PlayerControlConfiguration configuration = getPlayerControlConfiguration();
        int rewindIncrement = configuration.getRewindIncrement();
        int forwardIncrement = configuration.getForwardIncrement();
        if (player != null) {
            Timeline timeline = player.getCurrentTimeline();
            if (!timeline.isEmpty() && !player.isPlayingAd()) {
                Timeline.Window window = getTimelineWindow();
                timeline.getWindow(player.getCurrentWindowIndex(), window);
                boolean isSeekable = window.isSeekable;
                enableSeeking = isSeekable;
                enablePrevious = isSeekable || !window.isDynamic || player.hasPrevious();
                enableRewind = isSeekable && rewindIncrement > 0;
                enableFastForward = isSeekable && forwardIncrement > 0;
                enableNext = window.isDynamic || player.hasNext();
            }
        }
        setButtonEnabled(enablePrevious, viewHolder.previousButton);
        setButtonEnabled(enableRewind, viewHolder.rewindButton);
        setButtonEnabled(enableFastForward, viewHolder.fastForwardButton);
        setButtonEnabled(enableNext, viewHolder.nextButton);
        if (null!=viewHolder.timeBar) {
            viewHolder.timeBar.setEnabled(enableSeeking);
        }
    }

    @Override
    public void execute() {

    }

    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
    }
}
