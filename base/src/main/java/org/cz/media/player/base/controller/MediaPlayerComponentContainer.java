package org.cz.media.player.base.controller;

import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import org.cz.media.player.base.controller.component.PlayerComponent;

/**
 * @author Created by cz
 * @date 2020/8/13 11:02 AM
 * @email binigo110@126.com
 *
 * The component container. It supports all the functionality that the Component needed.
 * The implementation of this interface was {@link MediaPlayerControlLayout}
 * and It's an abstraction for {@link PlayerComponent}
 *
 */
public interface MediaPlayerComponentContainer {

    /**
     * Return the view holder.
     * @return
     */
    MediaPlayerViewHolder getViewHolder();

    /**
     * Return the player that the ExoPlayer support.
     * @return
     */
    Player getPlayer();

    /**
     * Get the PlaybackPreparer.
     * @return
     */
    PlaybackPreparer getPlaybackPreparer();

    /**
     * Get the TimelinePeriod.
     * @return
     */
    Timeline.Period getTimelinePeriod();

    /**
     * Get the TimelineWindow.
     * @return
     */
    Timeline.Window getTimelineWindow();

    /**
     * Register a new component. If the component has the same name inside the registered list.
     * It will replace that component.
     * @param component
     */
    void registerPlayerComponent(PlayerComponent component);

    /**
     * Unregister a component if it existed.
     * @param component
     */
    void unregisterPlayerComponent(PlayerComponent component);

    /**
     * The container is visible.
     * @return
     */
    boolean isVisible();

    /**
     * The container is attach to the window.
     * @return
     */
    boolean isAttachedToWindow();

    /**
     * Seek to a specific position.
     * @param offsetMs
     */
    void seekToOffset(long offsetMs);

    /**
     * Change the playWhenReady status for ExoPlayer
     * The playWhenReady is simply mean: When it ready if it should play the playback automatically.
     * @param playWhenReady
     */
    void dispatchSetPlayWhenReady(boolean playWhenReady);

    /**
     * Change the repeat mode.
     * @see Player#REPEAT_MODE_OFF
     * @see Player#REPEAT_MODE_ONE
     * @see Player#REPEAT_MODE_ALL
     *
     * @param repeatMode
     */
    void dispatchSetRepeatMode(@Player.RepeatMode int repeatMode);

    /**
     * Change the shuffle mode.
     * @param shuffleModeEnabled
     */
    void dispatchSetShuffleModeEnabled(boolean shuffleModeEnabled);

    /**
     * Change the current playback in the list. and where it wants to move.
     * @param windowIndex
     * @param positionMs
     * @return
     */
    boolean dispatchSeekTo(int windowIndex, long positionMs);

    /**
     * Stop the player.
     * @param reset
     */
    void dispatchStop(boolean reset);

}
