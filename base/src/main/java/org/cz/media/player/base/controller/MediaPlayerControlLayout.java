package org.cz.media.player.base.controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.RepeatModeUtil;
import com.google.android.exoplayer2.util.Util;
import org.cz.media.player.base.R;
import org.cz.media.player.base.controller.component.PlayerComponent;
import org.cz.media.player.base.controller.component.PlayerControlConfiguration;
import org.cz.media.player.base.controller.component.PlayerFastForwardComponent;
import org.cz.media.player.base.controller.component.PlayerNavigationComponent;
import org.cz.media.player.base.controller.component.PlayerNextComponent;
import org.cz.media.player.base.controller.component.PlayerPlayPauseComponent;
import org.cz.media.player.base.controller.component.PlayerPreviousComponent;
import org.cz.media.player.base.controller.component.PlayerRepeatComponent;
import org.cz.media.player.base.controller.component.PlayerRewindComponent;
import org.cz.media.player.base.controller.component.PlayerShuffleComponent;
import org.cz.media.player.base.controller.component.PlayerTimeBarComponent;
import org.cz.media.player.base.controller.component.TimeBarComponent;
import org.cz.media.player.base.layout.AnimationControlLayout;

import java.util.Collection;

/**
 * @author Created by cz
 * @date 2020/8/5 11:26 AM
 * @email bingo110@126.com
 *
 * The controller layout. By extend from AnimationControlLayout our child view support animation.
 * This layout manager all the components we predefine inside the MediaPlayer.
 *
 * @see PlayerComponent The abstraction of the component inside the MediaPlayer
 * @see MediaPlayerComponentManager manager all the components inside the control layout.
 *
 * @see MediaPlayerComponentContainer The component container.
 * @see PlayerControlConfiguration The control configuration. The configuration that all the components needed.
 *
 * @see org.cz.media.player.base.MediaPlayerView Cooperate with this view.
 *
 * Also take a look at this attribute.
 * @see R.attr#control_layout_id
 */
public class MediaPlayerControlLayout extends AnimationControlLayout implements Player.EventListener, MediaPlayerComponentContainer, PlayerControlConfiguration {
  /**
   * The maximum interval between time bar position updates.
   */
  private static final int MAX_UPDATE_INTERVAL_MS = 1000;
  private final MediaPlayerComponentManager playerComponentManager=new MediaPlayerComponentManager();
  @Nullable
  private MediaPlayerControlLayout.ProgressUpdateListener progressUpdateListener;
  private final Runnable updateProgressAction=this::updateProgress;
  private final Timeline.Period period=new Timeline.Period();
  private final Timeline.Window window=new Timeline.Window();
  private ControlDispatcher controlDispatcher = new DefaultControlDispatcher();
  private MediaPlayerViewHolder viewHolder;
  private PlaybackPreparer playbackPreparer;
  private Player player;

  private int rewindIncrement;
  private int forwardIncrement;
  private int timeBarMinUpdateInterval;
  @RepeatModeUtil.RepeatToggleModes
  private int repeatToggleMode;
  private boolean showShuffleButton;
  private float buttonEnabledAlpha;
  private float buttonDisabledAlpha;

  public MediaPlayerControlLayout(Context context) {
    this(context, /* attrs= */ null);
  }

  public MediaPlayerControlLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, /* defStyleAttr= */ R.attr.playerControlLayout);
  }

  public MediaPlayerControlLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs);
    initializeAttributes(context, attrs, defStyleAttr);
  }

  @Override
  protected void onInitializeControlLayout() {
    super.onInitializeControlLayout();
    Context context = getContext();
    viewHolder =new MediaPlayerViewHolder(this);
    initializeComponents(context,viewHolder);
    setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
  }

  private void initializeComponents(Context context, MediaPlayerViewHolder viewHolder) {
    //Register all the default player component.
    playerComponentManager.registerPlayerComponent(new PlayerPlayPauseComponent());
    playerComponentManager.registerPlayerComponent(new PlayerNavigationComponent());
    playerComponentManager.registerPlayerComponent(new PlayerPreviousComponent());
    playerComponentManager.registerPlayerComponent(new PlayerNextComponent());
    playerComponentManager.registerPlayerComponent(new PlayerFastForwardComponent());
    playerComponentManager.registerPlayerComponent(new PlayerRepeatComponent());
    playerComponentManager.registerPlayerComponent(new PlayerRewindComponent());
    playerComponentManager.registerPlayerComponent(new PlayerShuffleComponent());
    playerComponentManager.registerPlayerComponent(new PlayerTimeBarComponent());

    //Bind view to the player component.
    Collection<PlayerComponent> componentList = playerComponentManager.getPlayerComponentList();
    for(PlayerComponent playerComponent:componentList){
      playerComponent.attachToPlayerControlLayout(this);
      playerComponent.onComponentCreated(context,viewHolder);
    }
  }

  private void initializeAttributes(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MediaPlayerControlLayout, defStyleAttr, R.style.MediaPlayerControlLayout);
      try {
        rewindIncrement = a.getInt(R.styleable.MediaPlayerControlLayout_player_rewind_increment, 0);
        forwardIncrement = a.getInt(R.styleable.MediaPlayerControlLayout_player_forward_increment, 0);
        repeatToggleMode = a.getInt(R.styleable.MediaPlayerControlLayout_player_repeat_toggle_modes, repeatToggleMode);
        showShuffleButton = a.getBoolean(R.styleable.MediaPlayerControlLayout_player_show_shuffle_button, false);
        buttonEnabledAlpha = a.getFloat(R.styleable.MediaPlayerControlLayout_player_button_enabled_alpha,0f);
        buttonDisabledAlpha = a.getFloat(R.styleable.MediaPlayerControlLayout_player_button_disabled_alpha,0f);
        setTimeBarMinUpdateInterval(a.getInt(R.styleable.MediaPlayerControlLayout_player_time_bar_min_update_interval, 0));
      } finally {
        a.recycle();
      }
    }
  }

  /**
   * Sets the {@link ProgressUpdateListener}.
   *
   * @param listener The listener to be notified about when progress is updated.
   */
  public void setProgressUpdateListener(@Nullable ProgressUpdateListener listener) {
    this.progressUpdateListener = listener;
  }

  /**
   * Sets the {@link PlaybackPreparer}.
   *
   * @param playbackPreparer The {@link PlaybackPreparer}, or null to remove the current playback
   *     preparer.
   */
  public void setPlaybackPreparer(@Nullable PlaybackPreparer playbackPreparer) {
    this.playbackPreparer=playbackPreparer;
  }

  /**
   * Sets the {@link com.google.android.exoplayer2.ControlDispatcher}.
   *
   * @param controlDispatcher The {@link com.google.android.exoplayer2.ControlDispatcher}, or null
   *     to use {@link com.google.android.exoplayer2.DefaultControlDispatcher}.
   */
  public void setControlDispatcher(@Nullable ControlDispatcher controlDispatcher) {
    this.controlDispatcher=controlDispatcher == null ? new DefaultControlDispatcher() : controlDispatcher;
  }

  /**
   * Sets the rewind increment in milliseconds.
   *
   * @param rewindMs The rewind increment in milliseconds. A non-positive value will cause the
   *     rewind button to be disabled.
   */
  public void setRewindIncrementMs(int rewindMs) {
    this.rewindIncrement = rewindMs;
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_REWIND,viewHolder);
  }

  /**
   * Sets the fast forward increment in milliseconds.
   *
   * @param fastForwardMs The fast forward increment in milliseconds. A non-positive value will
   *     cause the fast forward button to be disabled.
   */
  public void setFastForwardIncrementMs(int fastForwardMs) {
    this.forwardIncrement = fastForwardMs;
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_NAVIGATION,viewHolder);
  }

  /**
   * Sets which repeat toggle modes are enabled.
   *
   * @param repeatToggleMode A set of {@link RepeatModeUtil.RepeatToggleModes}.
   */
  public void setRepeatToggleMode(@RepeatModeUtil.RepeatToggleModes int repeatToggleMode) {
    this.repeatToggleMode = repeatToggleMode;
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_REPEAT,viewHolder);
  }

  /**
   * Sets whether the shuffle button is shown.
   *
   * @param showShuffleButton Whether the shuffle button is shown.
   */
  public void setShowShuffleButton(boolean showShuffleButton) {
    this.showShuffleButton = showShuffleButton;
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_SHUFFLE,viewHolder);
  }

  /**
   * Sets the minimum interval between time bar position updates.
   *
   * <p>Note that smaller intervals, e.g. 33ms, will result in a smooth movement but will use more
   * CPU resources while the time bar is visible, whereas larger intervals, e.g. 200ms, will result
   * in a step-wise update with less CPU usage.
   *
   * @param minUpdateIntervalMs The minimum interval between time bar position updates, in
   *     milliseconds.
   */
  public void setTimeBarMinUpdateInterval(int minUpdateIntervalMs) {
    // Do not accept values below 16ms (60fps) and larger than the maximum update interval.
    timeBarMinUpdateInterval = Util.constrainValue(minUpdateIntervalMs, 16, MAX_UPDATE_INTERVAL_MS);
  }

  /**
   * Sets the {@link Player} to control.
   *
   * @param player The {@link Player} to control, or {@code null} to detach the current player. Only
   *     players which are accessed on the main thread are supported ({@code
   *     player.getApplicationLooper() == Looper.getMainLooper()}).
   */
  public void setPlayer(@Nullable Player player) {
    Assertions.checkState(Looper.myLooper() == Looper.getMainLooper());
    Assertions.checkArgument(
            player == null || player.getApplicationLooper() == Looper.getMainLooper());
    if (this.player == player) {
      return;
    }
    if (this.player != null) {
      this.player.removeListener(this);
    }
    this.player = player;
    if (player != null) {
      player.addListener(this);
      updateProgress();
    }
    updateAllComponents();
  }

  /**
   * For sub-class to override. When the control layout is shown.
   */
  @Override
  public void onLayoutShown() {
    super.onLayoutShown();
    updateAllComponents();
  }

  /**
   * For sub-class to override. When the control layout is hidden.
   */
  @Override
  public void onLayoutHidden() {
    super.onLayoutHidden();
  }

  /**
   * Update all the registered components.
   */
  public void updateAllComponents(){
    Collection<PlayerComponent> playerComponentList = playerComponentManager.getPlayerComponentList();
    for(PlayerComponent playerComponent:playerComponentList){
      playerComponent.update(viewHolder);
    }
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    //Bind view to the player component.
    Collection<PlayerComponent> componentList = playerComponentManager.getPlayerComponentList();
    for(PlayerComponent playerComponent:componentList){
      playerComponent.attachToPlayerControlLayout(this);
    }
    updateAllComponents();
  }

  @Override
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    removeCallbacks(updateProgressAction);
    Collection<PlayerComponent> playerComponentList = playerComponentManager.getPlayerComponentList();
    for(PlayerComponent component:playerComponentList){
      component.detachFromPlayerControlLayout(this);
    }
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    return dispatchMediaKeyEvent(event) || super.dispatchKeyEvent(event);
  }

  protected void updateProgress(){
    TimeBarComponent timeBarComponent=playerComponentManager.getPlayerComponent(PlayerComponent.COMPONENT_TIME_BAR);
    if(null==timeBarComponent) return;
    long position = timeBarComponent.getPosition();
     long bufferedPosition = timeBarComponent.getBufferedPosition();
      if (progressUpdateListener != null) {
      progressUpdateListener.onProgressUpdate(position, bufferedPosition);
    }
    // Cancel any pending updates and schedule a new one if necessary.
    removeCallbacks(updateProgressAction);
    //Update the progress of the media player.
    timeBarComponent.updateProgress();
    //Update the progress for the rest of the timeBar component.
    Collection<PlayerComponent> playerComponentList = playerComponentManager.getPlayerComponentList();
    for(PlayerComponent playerComponent : playerComponentList){
      if(playerComponent != timeBarComponent && playerComponent instanceof TimeBarComponent){
        TimeBarComponent component = (TimeBarComponent) playerComponent;
        component.updateProgress();
      }
    }
    //Also update the FastForward component
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_FAST_FORWARD,viewHolder);

    int playbackState = player == null ? Player.STATE_IDLE : player.getPlaybackState();
    if (player != null && player.isPlaying()) {
      long mediaTimeDelayMs = viewHolder.timeBar != null ? viewHolder.timeBar.getPreferredUpdateDelay() : MAX_UPDATE_INTERVAL_MS;
      // Limit delay to the start of the next full second to ensure position display is smooth.
      long mediaTimeUntilNextFullSecondMs = 1000 - position % 1000;
      mediaTimeDelayMs = Math.min(mediaTimeDelayMs, mediaTimeUntilNextFullSecondMs);
      // Calculate the delay until the next update in real time, taking playbackSpeed into account.
      float playbackSpeed = player.getPlaybackParameters().speed;
      long delayMs = playbackSpeed > 0 ? (long) (mediaTimeDelayMs / playbackSpeed) : MAX_UPDATE_INTERVAL_MS;
      // Constrain the delay to avoid too frequent / infrequent updates.
      int timeBarMinUpdateInterval = getTimeBarMinUpdateInterval();
      delayMs = Util.constrainValue(delayMs, timeBarMinUpdateInterval, MAX_UPDATE_INTERVAL_MS);
      postDelayed(updateProgressAction, delayMs);
    } else if (playbackState != Player.STATE_ENDED && playbackState != Player.STATE_IDLE) {
      postDelayed(updateProgressAction, MAX_UPDATE_INTERVAL_MS);
    }
  }

  /**
   * Called to process media key events. Any {@link KeyEvent} can be passed but only media key
   * events will be handled.
   *
   * @param event A key event.
   * @return Whether the key event was handled.
   */
  public boolean dispatchMediaKeyEvent(KeyEvent event) {
    int keyCode = event.getKeyCode();
    Player player = getPlayer();
    if (player == null || !isHandledMediaKey(keyCode)) {
      return false;
    }
    if (event.getAction() == KeyEvent.ACTION_DOWN) {
      if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
        playerComponentManager.executeComponent(PlayerComponent.COMPONENT_FAST_FORWARD);
      } else if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
        playerComponentManager.executeComponent(PlayerComponent.COMPONENT_REPEAT);
      } else if (event.getRepeatCount() == 0) {
        switch (keyCode) {
          case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            dispatchSetPlayWhenReady(!player.getPlayWhenReady());
            break;
          case KeyEvent.KEYCODE_MEDIA_PLAY:
            dispatchSetPlayWhenReady(true);
            break;
          case KeyEvent.KEYCODE_MEDIA_PAUSE:
            dispatchSetPlayWhenReady(false);
            break;
          case KeyEvent.KEYCODE_MEDIA_NEXT:
            playerComponentManager.executeComponent(PlayerComponent.COMPONENT_NEXT);
            break;
          case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            playerComponentManager.executeComponent(PlayerComponent.COMPONENT_PREVIOUS);
            break;
          default:
            break;
        }
      }
    }
    return true;
  }

  private static boolean isHandledMediaKey(int keyCode) {
    return keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
        || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
        || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
        || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY
        || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE
        || keyCode == KeyEvent.KEYCODE_MEDIA_NEXT
        || keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS;
  }

  //--------------------------------------
  //The #PlayerComponentManager
  //--------------------------------------
  @Override
  public MediaPlayerViewHolder getViewHolder() {
    return viewHolder;
  }

  @Override
  public Player getPlayer() {
    return player;
  }

  @Override
  public PlaybackPreparer getPlaybackPreparer() {
    return playbackPreparer;
  }

  @Override
  public Timeline.Period getTimelinePeriod() {
    return period;
  }

  @Override
  public Timeline.Window getTimelineWindow() {
    return window;
  }

  @Override
  public void registerPlayerComponent(@NonNull PlayerComponent component) {
    if(null!=component){
      playerComponentManager.registerPlayerComponent(component);
      //Attach the component to the control layout.
      component.attachToPlayerControlLayout(this);
      //The component create with the ViewHolder
      Context context = getContext();
      component.onComponentCreated(context,viewHolder);
    }
  }

  @Override
  public void unregisterPlayerComponent(@NonNull PlayerComponent component) {
    playerComponentManager.unregisterPlayerComponent(component);
  }

  public <T extends PlayerComponent> T getPlayerComponent(String componentName){
    return playerComponentManager.getPlayerComponent(componentName);
  }


  @Override
  public void seekToOffset(long offsetMs) {
    if(null==player) return;
    long positionMs = player.getCurrentPosition() + offsetMs;
    long durationMs = player.getDuration();
    if (durationMs != C.TIME_UNSET) {
      positionMs = Math.min(positionMs, durationMs);
    }
    positionMs = Math.max(positionMs, 0);
    dispatchSeekTo(player.getCurrentWindowIndex(), positionMs);
  }

  @Override
  public void dispatchSetPlayWhenReady(boolean playWhenReady) {
    controlDispatcher.dispatchSetPlayWhenReady(player,playWhenReady);
  }

  @Override
  public void dispatchSetRepeatMode(int repeatMode) {
    controlDispatcher.dispatchSetRepeatMode(player,repeatMode);
  }

  @Override
  public void dispatchSetShuffleModeEnabled(boolean shuffleModeEnabled) {
    controlDispatcher.dispatchSetShuffleModeEnabled(player,shuffleModeEnabled);
  }

  @Override
  public boolean dispatchSeekTo(int windowIndex, long positionMs) {
    return controlDispatcher.dispatchSeekTo(player,windowIndex,positionMs);
  }

  @Override
  public void dispatchStop(boolean reset) {
    controlDispatcher.dispatchStop(player,reset);
  }


  //--------------------------------------
  //The PlayerControlConfiguration
  //--------------------------------------
  @Override
  public int getRewindIncrement() {
    return rewindIncrement;
  }

  @Override
  public int getForwardIncrement() {
    return forwardIncrement;
  }

  /**
   * Returns which repeat toggle modes are enabled.
   *
   * @return The currently enabled {@link RepeatModeUtil.RepeatToggleModes}.
   */
  @RepeatModeUtil.RepeatToggleModes
  public int getRepeatToggleMode() {
    return repeatToggleMode;
  }

  /**
   * Returns whether the shuffle button is shown.
   */
  @Override
  public boolean isShowShuffleButton() {
    return showShuffleButton;
  }

  @Override
  public float getButtonEnabledAlpha() {
    return buttonEnabledAlpha;
  }

  @Override
  public float getButtonDisabledAlpha() {
    return buttonDisabledAlpha;
  }

  @Override
  public int getTimeBarMinUpdateInterval() {
    return timeBarMinUpdateInterval;
  }

  //--------------------------------------
  //The Player.EventListener
  //--------------------------------------
  @Override
  public void onPlayerStateChanged(boolean playWhenReady, @Player.State int playbackState) {
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_PLAY_PAUSE,viewHolder);
    updateProgress();
  }

  @Override
  public void onIsPlayingChanged(boolean isPlaying) {
    updateProgress();
  }

  @Override
  public void onRepeatModeChanged(int repeatMode) {
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_REPEAT,viewHolder);
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_NAVIGATION,viewHolder);
  }

  @Override
  public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_SHUFFLE,viewHolder);
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_NAVIGATION,viewHolder);
  }

  @Override
  public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_NAVIGATION,viewHolder);
    updateTimeBarComponent();
  }

  @Override
  public void onTimelineChanged(Timeline timeline, @Player.TimelineChangeReason int reason) {
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_NAVIGATION,viewHolder);
    updateTimeBarComponent();
  }

  private void updateTimeBarComponent() {
    Collection<PlayerComponent> playerComponentList = playerComponentManager.getPlayerComponentList();
    for(PlayerComponent playerComponent : playerComponentList){
      if(playerComponent instanceof TimeBarComponent){
        TimeBarComponent component = (TimeBarComponent) playerComponent;
        component.update(viewHolder);
      }
    }
  }

  /**
   * Listener to be notified when progress has been updated.
   */
  public interface ProgressUpdateListener {

    /**
     * Called when progress needs to be updated.
     *
     * @param position The current position.
     * @param bufferedPosition The current buffered position.
     */
    void onProgressUpdate(long position, long bufferedPosition);
  }

}
