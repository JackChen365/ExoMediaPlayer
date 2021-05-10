package org.cz.media.player.base.controller.component;

import android.content.Context;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import org.cz.media.player.base.controller.MediaPlayerViewHolder;
import org.cz.media.player.base.timebar.TimeBar;

import java.util.Formatter;
import java.util.Locale;

/**
 * @author Created by cz
 * @date 2020/8/13 10:55 AM
 * @email binigo110@126.com
 */
public class PlayerTimeBarComponent extends PlayerComponent implements TimeBarComponent,TimeBar.OnScrubListener {
    private static final String TAG="PlayerTimeBarComponent";
    /**
     * The maximum number of windows that can be shown in a multi-window time bar.
     */
    public static final int MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR = 100;

    private final StringBuilder formatBuilder=new StringBuilder();
    private final Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

    private long currentWindowOffset;
    private boolean showMultiWindowTimeBar;
    private boolean multiWindowTimeBar;
    private boolean scrubbing;

    @Override
    @ComponentName
    public String getComponentName() {
        return COMPONENT_TIME_BAR;
    }

    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
        Player player = getPlayer();
        if (player == null) {
            return;
        }
        Timeline.Period period = getTimelinePeriod();
        Timeline.Window window = getTimelineWindow();
        multiWindowTimeBar = showMultiWindowTimeBar && canShowMultiWindowTimeBar(player.getCurrentTimeline(), window);
        currentWindowOffset = 0;
        long durationUs = 0;
        Timeline timeline = player.getCurrentTimeline();
        if (!timeline.isEmpty()) {
            int currentWindowIndex = player.getCurrentWindowIndex();
            int firstWindowIndex = multiWindowTimeBar ? 0 : currentWindowIndex;
            int lastWindowIndex = multiWindowTimeBar ? timeline.getWindowCount() - 1 : currentWindowIndex;
            for (int i = firstWindowIndex; i <= lastWindowIndex; i++) {
                if (i == currentWindowIndex) {
                    currentWindowOffset = C.usToMs(durationUs);
                }
                timeline.getWindow(i, window);
                if (window.durationUs == C.TIME_UNSET) {
                    Assertions.checkState(!multiWindowTimeBar);
                    break;
                }
                for (int j = window.firstPeriodIndex; j <= window.lastPeriodIndex; j++) {
                    timeline.getPeriod(j, period);
                }
                durationUs += window.durationUs;
            }
        }
        long durationMs = C.usToMs(durationUs);
        if (viewHolder.durationView != null) {
            viewHolder.durationView.setText(Util.getStringForTime(formatBuilder, formatter, durationMs));
        }
        if (null != viewHolder.timeBar) {
            viewHolder.timeBar.setDuration(durationMs);
        }
        updateProgress();
    }

    @Override
    public void execute() {
    }

    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
        if(null!=viewHolder.timeBar){
            viewHolder.timeBar.addListener(this);
        }
    }

    @Override
    public void onScrubStart(TimeBar timeBar, long position) {
        scrubbing = true;
        MediaPlayerViewHolder viewHolder = getViewHolder();
        if (viewHolder.positionView != null) {
            viewHolder.positionView.setText(Util.getStringForTime(formatBuilder, formatter, position));
        }
    }

    @Override
    public void onScrubMove(TimeBar timeBar, long position) {
        MediaPlayerViewHolder viewHolder = getViewHolder();
        if (viewHolder.positionView != null) {
            viewHolder.positionView.setText(Util.getStringForTime(formatBuilder, formatter, position));
        }
    }

    @Override
    public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
        scrubbing = false;
        Player player = getPlayer();
        if (!canceled && player != null) {
            seekToTimeBarPosition(player, position);
        }
    }

    private void seekToTimeBarPosition(Player player, long positionMs) {
        int windowIndex;
        Timeline timeline = player.getCurrentTimeline();
        if (multiWindowTimeBar && !timeline.isEmpty()) {
            int windowCount = timeline.getWindowCount();
            windowIndex = 0;
            while (true) {
                Timeline.Window window = getTimelineWindow();
                long windowDurationMs = timeline.getWindow(windowIndex, window).getDurationMs();
                if (positionMs < windowDurationMs) {
                    break;
                } else if (windowIndex == windowCount - 1) {
                    // Seeking past the end of the last window should seek to the end of the timeline.
                    positionMs = windowDurationMs;
                    break;
                }
                positionMs -= windowDurationMs;
                windowIndex++;
            }
        } else {
            windowIndex = player.getCurrentWindowIndex();
        }
        boolean dispatched = dispatchSeekTo(windowIndex, positionMs);
        if (!dispatched) {
            // The seek wasn't dispatched then the progress bar scrubber will be in the wrong position.
            // Trigger a progress update to snap it back.
            updateProgress();
        }
    }

    public void updateProgress() {
        MediaPlayerViewHolder viewHolder = getViewHolder();
        if (!isVisible() || !isAttachedToWindow()) {
            return;
        }
        @Nullable Player player = getPlayer();
        long position = 0;
        long bufferedPosition = 0;
        if (player != null) {
            position = currentWindowOffset + player.getContentPosition();
            bufferedPosition = currentWindowOffset + player.getContentBufferedPosition();
        }
        long duration = viewHolder.timeBar.getDuration();
        if(position > duration){
            position = duration;
        }
        if (viewHolder.positionView != null && !scrubbing) {
            viewHolder.positionView.setText(Util.getStringForTime(formatBuilder, formatter, position));
        }
        if (viewHolder.timeBar != null) {
            viewHolder.timeBar.setPosition(position);
            viewHolder.timeBar.setBufferedPosition(bufferedPosition);
        }
    }

    public long getPosition(){
        long position = 0;
        Player player = getPlayer();
        if(null!=player){
            position=currentWindowOffset + player.getContentBufferedPosition();
        }
        return position;
    }

    public long getBufferedPosition(){
        long bufferedPosition = 0;
        Player player = getPlayer();
        if(null!=player){
            bufferedPosition=currentWindowOffset + player.getContentBufferedPosition();
        }
        return bufferedPosition;
    }

    /**
     * Returns whether the specified {@code timeline} can be shown on a multi-window time bar.
     *
     * @param timeline The {@link Timeline} to check.
     * @param window A scratch {@link Timeline.Window} instance.
     * @return Whether the specified timeline can be shown on a multi-window time bar.
     */
    private static boolean canShowMultiWindowTimeBar(Timeline timeline, Timeline.Window window) {
        if (timeline.getWindowCount() > MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR) {
            return false;
        }
        int windowCount = timeline.getWindowCount();
        for (int i = 0; i < windowCount; i++) {
            if (timeline.getWindow(i, window).durationUs == C.TIME_UNSET) {
                return false;
            }
        }
        return true;
    }
}
