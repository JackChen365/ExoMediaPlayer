package org.cz.media.player.sample.sample.list.component;

import android.content.Context;
import androidx.annotation.Nullable;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import org.cz.media.player.base.controller.MediaPlayerViewHolder;
import org.cz.media.player.base.controller.component.PlayerComponent;
import org.cz.media.player.base.controller.component.TimeBarComponent;
import org.cz.media.player.base.timebar.DefaultTimeBar;

import java.util.Formatter;
import java.util.Locale;

/**
 * @author Created by cz
 * @date 12/28/20 5:20 PM
 * @email bingo110@126.com
 */
public class MiniTimeBarComponent extends PlayerComponent implements TimeBarComponent {
    /**
     * The maximum number of windows that can be shown in a multi-window time bar.
     */
    public static final int MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR = 100;
    private final StringBuilder formatBuilder=new StringBuilder();
    private final Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
    public DefaultTimeBar playerTimeBar;

    private long currentWindowOffset;
    private boolean multiWindowTimeBar;

    public MiniTimeBarComponent(@NonNull DefaultTimeBar playerTimeBar) {
        this.playerTimeBar = playerTimeBar;
    }

    @Override
    public String getComponentName() {
        return "mini-timeBar";
    }

    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
        Player player = getPlayer();
        if (player == null) {
            return;
        }
        Timeline.Period period = getTimelinePeriod();
        Timeline.Window window = getTimelineWindow();
        multiWindowTimeBar = canShowMultiWindowTimeBar(player.getCurrentTimeline(), window);
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
        if (null != playerTimeBar) {
            playerTimeBar.setDuration(durationMs);
        }
        updateProgress();
    }

    @Override
    public void execute() {
    }

    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
    }

    public void updateProgress() {
        if (!isAttachedToWindow()) {
            return;
        }
        @Nullable Player player = getPlayer();
        long position = 0;
        long bufferedPosition = 0;
        if (player != null) {
            position = currentWindowOffset + player.getContentPosition();
            bufferedPosition = currentWindowOffset + player.getContentBufferedPosition();
        }
        if(null!=playerTimeBar){
            long duration = playerTimeBar.getDuration();
            if(position > duration){
                position = duration;
            }
            playerTimeBar.setPosition(position);
            playerTimeBar.setBufferedPosition(bufferedPosition);
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
