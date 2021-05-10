package org.cz.media.player.base.controller.component;

import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringDef;

import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import org.cz.media.player.base.controller.MediaPlayerControlLayout;
import org.cz.media.player.base.controller.MediaPlayerViewHolder;

/**
 * @author Created by cz
 * @date 2020/8/13 10:52 AM
 * @email binigo110@126.com
 * The abstraction of MediaPlayer's component.
 *
 * The reason we have this abstraction is we want to separate all the view inside the MediaPlayerControlLayout.
 * But it can be really hard. We have to take care of all the changes that the player has, and dispatch the change to each component.
 *
 * Be careful about the {@link #getComponentName()} it should be unique. We use a unique component name to distinguish different component.
 * The function {@link #execute()} could binding to the view or not. But it must be existed. However we may want to execute the component's function.
 *
 * @see MediaPlayerControlLayout
 *
 * Also. check those methods out.
 * @see org.cz.media.player.base.MediaPlayerView#registerPlayerComponent(PlayerComponent) Register a new component.
 * @see org.cz.media.player.base.MediaPlayerView#getPlayerComponent(String) Take the component by its name.
 *
 */
public abstract class PlayerComponent implements View.OnClickListener{
    public static final String COMPONENT_FAST_FORWARD ="forward";
    public static final String COMPONENT_NAVIGATION="navigation";
    public static final String COMPONENT_NEXT="next";
    public static final String COMPONENT_PLAY_PAUSE="play_pause";
    public static final String COMPONENT_PREVIOUS="previous";
    public static final String COMPONENT_REPEAT="repeat";
    public static final String COMPONENT_REWIND="rewind";
    public static final String COMPONENT_SHUFFLE="shuffle";
    public static final String COMPONENT_TIME_BAR="timeBar";

    @StringDef({COMPONENT_FAST_FORWARD,
            COMPONENT_NAVIGATION,
            COMPONENT_NEXT,
            COMPONENT_PLAY_PAUSE,
            COMPONENT_PREVIOUS,
            COMPONENT_REPEAT,
            COMPONENT_REWIND,
            COMPONENT_SHUFFLE,
            COMPONENT_TIME_BAR
    })
    public @interface ComponentName {
    }
    private MediaPlayerControlLayout controlLayout;

    public PlayerComponent() {
    }

    /**
     * Return the name of the component. It should be a unique name.
     * @return The component name.
     */
    public abstract String getComponentName();

    /**
     * When the player receives some events. Some components need to change its appearance.
     * Like the the Player's play status changed from start to pause. The PlayPauseComponent should update.
     * @param viewHolder The component view combination.
     */
    public abstract void update(MediaPlayerViewHolder viewHolder);

    /**
     * If the component changed whatever the user clicks the button or You call it programminglly.
     * It will trigger this function, Such as you click the play button. It should play the playback.
     */
    public abstract void execute();

    /**
     * On the component created. What kind of view you are interested in.
     * Here you could use the {@link MediaPlayerViewHolder} to initialize your component function and tie it to your view.
     * If the view not defined in the holder. You could always use the {@link MediaPlayerViewHolder#itemView} to find your view if it existed.
     * @param context
     * @param viewHolder
     */
    public abstract void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder);

    @Override
    public void onClick(View v) {
        execute();
    }

    /**
     * Determine if the control layout is visible.
     * @return
     */
    public boolean isVisible(){
        return controlLayout.isVisible();
    }

    /**
     * Attach the component to the {@link MediaPlayerControlLayout}
     * It's the change you should initialize something with the control layout.
     * @see #onAttachToPlayerControlLayout(MediaPlayerControlLayout)
     * @param controlLayout
     */
    public void attachToPlayerControlLayout(MediaPlayerControlLayout controlLayout){
        this.controlLayout=controlLayout;
        onAttachToPlayerControlLayout(controlLayout);
    }

    /**
     * When the component binding to the view.
     * @param controlLayout
     */
    public void onAttachToPlayerControlLayout(MediaPlayerControlLayout controlLayout){
    }

    /**
     * When the component detach from the view.
     * Here you should release all the resources.
     * @param controlLayout
     */
    public void detachFromPlayerControlLayout(MediaPlayerControlLayout controlLayout){
//        this.controlLayout=null;
        onDetachFromPlayerControlLayout(controlLayout);
    }

    public void onDetachFromPlayerControlLayout(MediaPlayerControlLayout controlLayout){
    }

    public boolean isAttachedToWindow(){
        return controlLayout.isAttachedToWindow();
    }

    public MediaPlayerViewHolder getViewHolder() {
        return controlLayout.getViewHolder();
    }

    public Player getPlayer() {
        return controlLayout.getPlayer();
    }

    public PlaybackPreparer getPlaybackPreparer(){
        return controlLayout.getPlaybackPreparer();
    }

    public Timeline.Period getTimelinePeriod() {
        return controlLayout.getTimelinePeriod();
    }

    public Timeline.Window getTimelineWindow() {
        return controlLayout.getTimelineWindow();
    }

    public void dispatchSetPlayWhenReady(boolean playWhenReady) {
        controlLayout.dispatchSetPlayWhenReady(playWhenReady);
    }

    public void dispatchSetRepeatMode(int repeatMode) {
        controlLayout.dispatchSetRepeatMode(repeatMode);
    }

    public void dispatchSetShuffleModeEnabled(boolean shuffleModeEnabled) {
        controlLayout.dispatchSetShuffleModeEnabled(shuffleModeEnabled);
    }

    public boolean dispatchSeekTo(int windowIndex, long positionMs) {
        return controlLayout.dispatchSeekTo(windowIndex,positionMs);
    }

    public void seekToOffset(long offsetMs) {
        controlLayout.seekToOffset(offsetMs);
    }

    public void dispatchStop(boolean reset) {
        controlLayout.dispatchStop(reset);
    }

    public PlayerControlConfiguration getPlayerControlConfiguration(){
        return controlLayout;
    }

    public void setButtonEnabled(boolean enabled, @Nullable View view) {
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
        PlayerControlConfiguration playerControlConfiguration = getPlayerControlConfiguration();
        float buttonEnabledAlpha = playerControlConfiguration.getButtonEnabledAlpha();
        float buttonDisabledAlpha = playerControlConfiguration.getButtonDisabledAlpha();
        view.setAlpha(enabled ? buttonEnabledAlpha : buttonDisabledAlpha);
        view.setVisibility(View.VISIBLE);
    }
}
