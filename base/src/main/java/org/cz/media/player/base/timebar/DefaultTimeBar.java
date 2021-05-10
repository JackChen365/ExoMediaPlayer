package org.cz.media.player.base.timebar;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.core.BuildConfig;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import org.cz.media.player.base.R;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A time bar that shows a current position, buffered position, duration and ad markers.
 *
 * <p>A DefaultTimeBar can be customized by setting attributes, as outlined below.
 *
 * <h3>Attributes</h3>
 *
 * The following attributes can be set on a DefaultTimeBar when used in a layout XML file:
 *
 * <p>
 * <ul>
 *   <li><b>{@code bar_scrubber}</b> - The drawable resource of the scrubber handle of the time bar.
 *       <ul>
 *         <li> {@link #setScrubberDrawable(Drawable)}
 *       </ul>
 *   <li><b>{@code bar_buffered}</b> - The drawable resource for the time bar that illustrates the buffer progress.
 *       <ul>
 *         <li> {@link #setBufferedDrawable(Drawable)}
 *       </ul>
 *   <li><b>{@code bar_played}</b> - The drawable resource for the time bar that illustrates the played progress.
 *       <ul>
 *         <li> {@link #setProgressDrawable(Drawable)}
 *       </ul>
 *   <li><b>{@code bar_background}</b> - The background of the time bar without the padding.
 *       <ul>
 *         <li> {@link #setTimeBarBackground(Drawable)}
 *       </ul>
 *   <li><b>{@code bar_mark_read}</b> - The marked drawable resource for read state.
 *       <ul>
 *         <li>Corresponding method: {@link #setMarkReadDrawable(Drawable)}}
 *       </ul>
 *   <li><b>{@code bar_mark_unread}</b> - The marked drawable resource for unread state.
 *       <ul>
 *         <li>Corresponding method: {@link #setMarkUnreadDrawable(Drawable)}
 *       </ul>
 * </ul>
 */
public class DefaultTimeBar extends View implements TimeBar {
  /**
   * The name of the Android SDK view that most closely resembles this custom view. Used as the
   * class name for accessibility.
   */
  private static final String ACCESSIBILITY_CLASS_NAME = "android.widget.SeekBar";
  /**
   * The time after which the scrubbing listener is notified that scrubbing has stopped after
   * performing an incremental scrub using key input.
   */
  private static final long STOP_SCRUBBING_TIMEOUT_MS = 1000;

  private static final int DEFAULT_INCREMENT_COUNT = 20;

  private static final float SHOWN_PRESS_SCRUBBER_SCALE = 1.2f;
  private static final float SHOWN_SCRUBBER_SCALE = 1.0f;
  private static final float HIDDEN_SCRUBBER_SCALE = 0.0f;

  @Nullable private Drawable scrubberDrawable;
  @Nullable private Drawable bufferedDrawable;
  @Nullable private Drawable progressDrawable;
  @Nullable private Drawable markReadDrawable;
  @Nullable private Drawable markUnreadDrawable;
  @Nullable private Drawable progressBackground;
  @MonotonicNonNull private Rect lastExclusionRectangle;

  private final StringBuilder formatBuilder=new StringBuilder();
  private final Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
  private final Runnable stopScrubbingRunnable= () -> stopScrubbing(/* canceled= */ false);
  private final CopyOnWriteArraySet<OnScrubListener> listeners=new CopyOnWriteArraySet<>();
  private final Point touchPosition = new Point();;
  private int keyCountIncrement;
  private long keyTimeIncrement;

  private ValueAnimator scrubberScalingAnimator = new ValueAnimator();
  private ValueAnimator scrubberPressAnimator = new ValueAnimator();
  private float scrubberPressScale=SHOWN_PRESS_SCRUBBER_SCALE;
  private float scrubberScale=SHOWN_SCRUBBER_SCALE;
  private int beginFakeSeek;
  private boolean scrubbing;
  private long scrubPosition;
  private long duration;
  private long position;
  private long bufferedPosition;

  private int markGroupCount;
  @Nullable private long[] markGroupTimesMs;
  @Nullable private boolean[] playedMarkGroups;

  public DefaultTimeBar(Context context) {
    this(context, null);
  }

  public DefaultTimeBar(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DefaultTimeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, R.style.DefaultTimeBar);
  }

  public DefaultTimeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DefaultTimeBar, defStyleAttr, defStyleRes);
    setScrubberDrawable(a.getDrawable(R.styleable.DefaultTimeBar_bar_scrubber));
    setBufferedDrawable(a.getDrawable(R.styleable.DefaultTimeBar_bar_buffered));
    setProgressDrawable(a.getDrawable(R.styleable.DefaultTimeBar_bar_progress));
    setTimeBarBackground(a.getDrawable(R.styleable.DefaultTimeBar_bar_background));
    setMarkReadDrawable(a.getDrawable(R.styleable.DefaultTimeBar_bar_mark_read));
    setMarkUnreadDrawable(a.getDrawable(R.styleable.DefaultTimeBar_bar_mark_unread));
    setScrubberPressScaled(a.getFloat(R.styleable.DefaultTimeBar_bar_press_scale,0f));
    a.recycle();


    scrubberScalingAnimator.addUpdateListener(animation -> {
        scrubberScale = (float) animation.getAnimatedValue();
        invalidate();
    });
    duration = C.TIME_UNSET;
    keyTimeIncrement = C.TIME_UNSET;
    keyCountIncrement = DEFAULT_INCREMENT_COUNT;


    scrubberPressAnimator.addUpdateListener(animation -> {
      scrubberScale = (float) animation.getAnimatedValue();
      invalidate();
    });

    setFocusable(true);
    if (getImportantForAccessibility() == View.IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
      setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
    }
  }

  private void setScrubberDrawable(@NonNull Drawable drawable) {
      this.scrubberDrawable=drawable;
  }

  private void setBufferedDrawable(@NonNull Drawable drawable) {
    this.bufferedDrawable=drawable;
  }

  private void setProgressDrawable(@NonNull Drawable drawable) {
    this.progressDrawable =drawable;
  }

  private void setTimeBarBackground(@NonNull Drawable drawable) {
    this.progressBackground =drawable;
  }

  private void setMarkReadDrawable(@NonNull Drawable drawable) {
    this.markReadDrawable =drawable;
  }

  private void setMarkUnreadDrawable(@NonNull Drawable drawable) {
    this.markUnreadDrawable =drawable;
  }

  private void setScrubberPressScaled(float pressScale) {
    this.scrubberPressScale=pressScale;
  }

  /** Shows the scrubber handle. */
  public void showScrubber() {
    showScrubber(/* showAnimationDurationMs= */ 0);
  }

  /**
   * Shows the scrubber handle with animation.
   *
   * @param showAnimationDurationMs The duration for scrubber showing animation.
   */
  public void showScrubber(long showAnimationDurationMs) {
    if (scrubberScalingAnimator.isStarted()) {
      scrubberScalingAnimator.cancel();
    }
    scrubberScalingAnimator.setFloatValues(scrubberScale, SHOWN_SCRUBBER_SCALE);
    scrubberScalingAnimator.setDuration(showAnimationDurationMs);
    scrubberScalingAnimator.start();
  }

  /** Hides the scrubber handle. */
  public void hideScrubber() {
    hideScrubber(/* hideAnimationDurationMs= */ 0);
  }

  /**
   * Hides the scrubber handle with animation.
   *
   * @param hideAnimationDurationMs The duration for scrubber hiding animation.
   */
  public void hideScrubber(long hideAnimationDurationMs) {
    if (scrubberScalingAnimator.isStarted()) {
      scrubberScalingAnimator.cancel();
    }
    scrubberScalingAnimator.setFloatValues(scrubberScale, HIDDEN_SCRUBBER_SCALE);
    scrubberScalingAnimator.setDuration(hideAnimationDurationMs);
    scrubberScalingAnimator.start();
  }


  /**
   * Shows the scrubber handle with animation.
   *
   */
  public void playPressAnimation() {
    if (scrubberScalingAnimator.isStarted()) {
      scrubberScalingAnimator.cancel();
    }
    if(SHOWN_SCRUBBER_SCALE!=scrubberPressScale){
      scrubberScalingAnimator.setFloatValues(SHOWN_SCRUBBER_SCALE,scrubberPressScale);
      scrubberScalingAnimator.start();
    }
  }

  /**
   * Hides the scrubber handle with animation.
   *
   */
  public void playUnPressAnimation() {
    if (scrubberScalingAnimator.isStarted()) {
      scrubberScalingAnimator.cancel();
    }
    if(SHOWN_SCRUBBER_SCALE!=scrubberPressScale){
      scrubberScalingAnimator.setFloatValues(scrubberPressScale, SHOWN_SCRUBBER_SCALE);
      scrubberScalingAnimator.start();
    }
  }

  // TimeBar implementation.
  @Override
  public void addListener(OnScrubListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(OnScrubListener listener) {
    listeners.remove(listener);
  }

  @Override
  public void setKeyTimeIncrement(long time) {
    Assertions.checkArgument(time > 0);
    keyCountIncrement = C.INDEX_UNSET;
    keyTimeIncrement = time;
  }

  @Override
  public void setKeyCountIncrement(int count) {
    Assertions.checkArgument(count > 0);
    keyCountIncrement = count;
    keyTimeIncrement = C.TIME_UNSET;
  }

  @Override
  public void offset(long offset) {
    setPosition(position+offset);
  }

  public int getScrubberSize() {
    int scrubberSize=0;
    if(null!=scrubberDrawable){
      scrubberSize=scrubberDrawable.getIntrinsicWidth();
    }
    return scrubberSize;
  }

  @Override
  public void setPosition(long position) {
    if(!isBeginFakeSeek()){
      this.position = position;
      setContentDescription(getProgressText());
      updateTimeBar();
    }
  }

  @Override
  public void setBufferedPosition(long bufferedPosition) {
    this.bufferedPosition = bufferedPosition;
    updateTimeBar();
  }

  @Override
  public void setDuration(long duration) {
    this.duration = duration;
    if (scrubbing && duration == C.TIME_UNSET) {
      stopScrubbing(/* canceled= */ true);
    }
    updateTimeBar();
  }

  @Override
  public long getDuration() {
    return duration;
  }

  @Override
  public long getPosition() {
    return position;
  }

  /**
   * Begin fake seek means the widget still be changed.
   * But I won't dispatch any events.
   * @see #startScrubbing(long)
   * @see #updateScrubbing(long)
   * @see #stopScrubbing(boolean)
   * @return
   */
  @Override
  public boolean isBeginFakeSeek() {
    return 0 < beginFakeSeek;
  }

  @Override
  public void seekToPosition(long position) {
    if(isBeginFakeSeek()){
      this.position = position;
      setContentDescription(getProgressText());
      updateTimeBar();
    }
  }

  @Override
  public void beginFakeSeek() {
    beginFakeSeek++;
  }

  @Override
  public void endFakeSeek() {
    beginFakeSeek--;
  }

  @Override
  public long getPreferredUpdateDelay() {
    Rect bounds = progressBackground.getBounds();
    if(bounds.isEmpty()){
      return 0;
    } else {
      return duration == 0 || duration == C.TIME_UNSET
          ? Long.MAX_VALUE
          : duration / bounds.width();
    }
  }

  @Override
  public void setMarkGroupTimesMs(@Nullable long[] adGroupTimesMs, @Nullable boolean[] playedAdGroups,
                                  int adGroupCount) {
    Assertions.checkArgument(adGroupCount == 0
        || (adGroupTimesMs != null && playedAdGroups != null));
    this.markGroupCount = adGroupCount;
    this.markGroupTimesMs = adGroupTimesMs;
    this.playedMarkGroups = playedAdGroups;
    updateTimeBar();
  }

  // View methods.
  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if (scrubbing && !enabled) {
      stopScrubbing(/* canceled= */ true);
    }
  }

  @Override
  public void onDraw(Canvas canvas) {
    drawTimeBar(canvas);
    drawPlayHead(canvas);
    debugDrawing(canvas);
  }

  private void debugDrawing(Canvas canvas) {
    if(BuildConfig.DEBUG||isInEditMode()){
      int paddingLeft = getPaddingLeft();
      int paddingTop = getPaddingTop();
      int paddingRight = getPaddingRight();
      int paddingBottom = getPaddingBottom();
      int width = getWidth();
      int height = getHeight();
      Paint paint=new Paint();
      paint.setStrokeWidth(2);
      paint.setColor(Color.RED);
      paint.setStyle(Paint.Style.STROKE);
      canvas.drawRect(paddingLeft,paddingTop,width-paddingRight,height-paddingBottom,paint);
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!isEnabled() || duration <= 0) {
      return false;
    }
    Point touchPosition = resolveRelativeTouchPosition(event);
    int x = touchPosition.x;
    int y = touchPosition.y;
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        if (isInSeekBar(x, y)) {
          playPressAnimation();
          positionScrubber(x);
          startScrubbing(getScrubberPosition());
          updateTimeBar();
          invalidate();
          ViewParent parent = getParent();
          if(null!=parent){
            parent.requestDisallowInterceptTouchEvent(true);
          }
          return true;
        }
        break;
      case MotionEvent.ACTION_MOVE:
        if (scrubbing) {
          positionScrubber(x);
          updateScrubbing(getScrubberPosition());
          updateTimeBar();
          invalidate();
          return true;
        }
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        if (scrubbing) {
          playUnPressAnimation();
          stopScrubbing(/* canceled= */ event.getAction() == MotionEvent.ACTION_CANCEL);
          return true;
        }
        break;
      default:
        // Do nothing.
    }
    return super.onTouchEvent(event);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (isEnabled()) {
      long positionIncrement = getPositionIncrement();
      switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_LEFT:
          positionIncrement = -positionIncrement;
          // Fall through.
        case KeyEvent.KEYCODE_DPAD_RIGHT:
          if (scrubIncrementally(positionIncrement)) {
            removeCallbacks(stopScrubbingRunnable);
            postDelayed(stopScrubbingRunnable, STOP_SCRUBBING_TIMEOUT_MS);
            return true;
          }
          break;
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
          if (scrubbing) {
            stopScrubbing(/* canceled= */ false);
            return true;
          }
          break;
        default:
          // Do nothing.
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  protected void onFocusChanged(
      boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
    super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    if (scrubbing && !gainFocus) {
      stopScrubbing(/* canceled= */ false);
    }
  }

  @Override
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    updateDrawableState();
  }

  private void updateDrawableState() {
    boolean needInvalidate=false;
    int[] drawableState = getDrawableState();
    if (progressBackground != null && progressBackground.isStateful()
            && progressBackground.setState(drawableState)) {
      needInvalidate=true;
    }
    if (progressDrawable != null && progressDrawable.isStateful()
            && progressDrawable.setState(drawableState)) {
      needInvalidate=true;
    }
    if (bufferedDrawable != null && bufferedDrawable.isStateful()
            && bufferedDrawable.setState(drawableState)) {
      needInvalidate=true;
    }
    if (scrubberDrawable != null && scrubberDrawable.isStateful()
            && scrubberDrawable.setState(drawableState)) {
      needInvalidate=true;
    }
    if (markReadDrawable != null && markReadDrawable.isStateful()
            && markReadDrawable.setState(drawableState)) {
      needInvalidate=true;
    }
    if (markUnreadDrawable != null && markUnreadDrawable.isStateful()
            && markUnreadDrawable.setState(drawableState)) {
      needInvalidate=true;
    }
    if(needInvalidate){
      invalidate();
    }
  }

  @Override
  public void jumpDrawablesToCurrentState() {
    super.jumpDrawablesToCurrentState();
    if (progressBackground != null) {
      progressBackground.jumpToCurrentState();
    }
    if (progressDrawable != null) {
      progressDrawable.jumpToCurrentState();
    }
    if (bufferedDrawable != null) {
      bufferedDrawable.jumpToCurrentState();
    }
    if (scrubberDrawable != null) {
      scrubberDrawable.jumpToCurrentState();
    }
    if (markReadDrawable != null) {
      markReadDrawable.jumpToCurrentState();
    }
    if (markUnreadDrawable != null) {
      markUnreadDrawable.jumpToCurrentState();
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int paddingTop = getPaddingTop();
    int paddingBottom = getPaddingBottom();
    int scrubberHeight=0;
    if(null!=scrubberDrawable){
       scrubberHeight=scrubberDrawable.getIntrinsicHeight();
    }
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    int height = heightMode == MeasureSpec.UNSPECIFIED ? paddingTop+scrubberHeight+paddingBottom
        : heightMode == MeasureSpec.EXACTLY ? heightSize : Math.min(paddingTop+scrubberHeight+paddingBottom, heightSize);
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    updateDrawableState();
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    int width = right-left;
    int height = bottom-top;
    setDrawableBoundary(progressBackground,width,height);
    setDrawableBoundary(bufferedDrawable,0,height);
    setDrawableBoundary(progressDrawable,0,height);

    int drawableHeight=0;
    if(null!= scrubberDrawable){
      drawableHeight= scrubberDrawable.getIntrinsicHeight();
    }
    if(null!= scrubberDrawable){
      Rect bounds = progressBackground.getBounds();
      int scrubberRadius = getScrubberRadius();
      scrubberDrawable.setBounds(bounds.left-scrubberRadius, (height - drawableHeight) / 2, bounds.left+scrubberRadius, (height + drawableHeight) / 2);
    }
    if (Build.VERSION.SDK_INT >= 29) {
      setSystemGestureExclusionRectsV29(width, height);
    }
    updateTimeBar();
  }

  private int getScrubberRadius() {
    int intrinsicWidth = scrubberDrawable.getIntrinsicWidth();
    int intrinsicHeight = scrubberDrawable.getIntrinsicHeight();
    return Math.max(intrinsicWidth / 2, intrinsicHeight / 2);
  }

  private void setDrawableBoundary(Drawable drawable, int width, int height){
    int scrubberRadius = getScrubberRadius();
    int left = getPaddingLeft()+scrubberRadius;
    int right = width - getPaddingRight()-scrubberRadius;
    int drawableHeight=0;
    if(null!= drawable){
      drawableHeight= drawable.getIntrinsicHeight();
    }
    if(null!= drawable){
      drawable.setBounds(left, (height - drawableHeight) / 2, right, (height + drawableHeight) / 2);
    }
  }

  @Override
  public void onRtlPropertiesChanged(int layoutDirection) {
    if (scrubberDrawable != null && setDrawableLayoutDirection(scrubberDrawable, layoutDirection)) {
      invalidate();
    }
  }

  @Override
  public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
    super.onInitializeAccessibilityEvent(event);
    if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SELECTED) {
      event.getText().add(getProgressText());
    }
    event.setClassName(ACCESSIBILITY_CLASS_NAME);
  }

  @TargetApi(21)
  @Override
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
    super.onInitializeAccessibilityNodeInfo(info);
    info.setClassName(ACCESSIBILITY_CLASS_NAME);
    info.setContentDescription(getProgressText());
    if (duration <= 0) {
      return;
    }
    if (Util.SDK_INT >= 21) {
      info.addAction(AccessibilityAction.ACTION_SCROLL_FORWARD);
      info.addAction(AccessibilityAction.ACTION_SCROLL_BACKWARD);
    } else {
      info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
      info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }
  }

  @Override
  public boolean performAccessibilityAction(int action, @Nullable Bundle args) {
    if (super.performAccessibilityAction(action, args)) {
      return true;
    }
    if (duration <= 0) {
      return false;
    }
    if (action == AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) {
      if (scrubIncrementally(-getPositionIncrement())) {
        stopScrubbing(/* canceled= */ false);
      }
    } else if (action == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) {
      if (scrubIncrementally(getPositionIncrement())) {
        stopScrubbing(/* canceled= */ false);
      }
    } else {
      return false;
    }
    sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
    return true;
  }

  // Internal methods.

  private void startScrubbing(long scrubPosition) {
    this.scrubPosition = scrubPosition;
    scrubbing = true;
    setPressed(true);
    ViewParent parent = getParent();
    if (parent != null) {
      parent.requestDisallowInterceptTouchEvent(true);
    }
    if(!isBeginFakeSeek()){
      for(OnScrubListener listener : listeners) {
        listener.onScrubStart(this, scrubPosition);
      }
    }
  }

  private void updateScrubbing(long scrubPosition) {
    if (this.scrubPosition == scrubPosition) {
      return;
    }
    this.scrubPosition = scrubPosition;
    if(!isBeginFakeSeek()){
      for (OnScrubListener listener : listeners) {
        listener.onScrubMove(this, scrubPosition);
      }
    }
  }

  private void stopScrubbing(boolean canceled) {
    removeCallbacks(stopScrubbingRunnable);
    scrubbing = false;
    setPressed(false);
    ViewParent parent = getParent();
    if (parent != null) {
      parent.requestDisallowInterceptTouchEvent(false);
    }
    invalidate();
    if(!isBeginFakeSeek()){
      for (OnScrubListener listener : listeners) {
        listener.onScrubStop(this, scrubPosition, canceled);
      }
    }
  }

  /**
   * Incrementally scrubs the position by {@code positionChange}.
   *
   * @param positionChange The change in the scrubber position, in milliseconds. May be negative.
   * @return Returns whether the scrubber position changed.
   */
  private boolean scrubIncrementally(long positionChange) {
    if (duration <= 0) {
      return false;
    }
    long previousPosition = scrubbing ? scrubPosition : position;
    long scrubPosition = Util.constrainValue(previousPosition + positionChange, 0, duration);
    if (scrubPosition == previousPosition) {
      return false;
    }
    if (!scrubbing) {
      startScrubbing(scrubPosition);
    } else {
      updateScrubbing(scrubPosition);
    }
    updateTimeBar();
    return true;
  }

  private void updateTimeBar() {
    Rect bounds = progressBackground.getBounds();
    Rect bufferedBounds = bufferedDrawable.getBounds();
    Rect progressBounds = progressDrawable.getBounds();
    Rect scrubberBounds = scrubberDrawable.getBounds();
    long newScrubberTime = scrubbing ? scrubPosition : position;
    if (duration > 0) {
      int bufferedPixelWidth = (int) ((bounds.width() * bufferedPosition) / duration);
      int bufferedBoundRight = Math.min(bounds.left + bufferedPixelWidth, bounds.right);
      bufferedDrawable.setBounds(bufferedBounds.left,bufferedBounds.top,bufferedBoundRight,bufferedBounds.bottom);
      int scrubberPixelPosition = (int) ((bounds.width() * newScrubberTime) / duration);
      int right= Math.min(bounds.left + scrubberPixelPosition, bounds.right);
      progressDrawable.setBounds(progressBounds.left,progressBounds.top,right,progressBounds.bottom);

      int scrubberRadius = getScrubberRadius();
      scrubberDrawable.setBounds(right-scrubberRadius,scrubberBounds.top,right+scrubberRadius,scrubberBounds.bottom);
    } else {
      bufferedBounds.right = bounds.left;
      progressBounds.right = bounds.left;
      scrubberBounds.right = bounds.left;
    }
    invalidate();
  }

  private void positionScrubber(float position) {
    Rect backgroundBounds = progressBackground.getBounds();
    int left = backgroundBounds.left;
    int right = backgroundBounds.right;
    int centerX = Util.constrainValue(Math.round(position),left,right);

    int scrubberRadius = getScrubberRadius();
    Rect scrubberBounds = scrubberDrawable.getBounds();
    scrubberDrawable.setBounds(centerX,scrubberBounds.top,centerX+scrubberRadius*2,scrubberBounds.bottom);
  }

  private Point resolveRelativeTouchPosition(MotionEvent motionEvent) {
    touchPosition.set((int) motionEvent.getX(), (int) motionEvent.getY());
    return touchPosition;
  }

  private long getScrubberPosition() {
    Rect progressBounds = progressBackground.getBounds();
    if (progressBounds.width() <= 0 || duration == C.TIME_UNSET) {
      return 0;
    }
    Rect scrubberBounds = scrubberDrawable.getBounds();
    int paddingLeft = getPaddingLeft();
    int scrubberRadius = getScrubberRadius();
    int offset = scrubberBounds.left - paddingLeft - scrubberRadius;
    return (offset * duration) / progressBounds.width();
  }

  private boolean isInSeekBar(float x, float y) {
    int left=0;
    int top=0;
    int right = getWidth();
    int bottom = getHeight();
    return left < right && top < bottom && x >= left && x < right && y >= top && y < bottom;
  }

  private void drawTimeBar(Canvas canvas) {
    //Step1. draw progress background.
    if(null!= progressBackground){
      progressBackground.draw(canvas);
    }
    //Step2. Draw the buffer drawable.
    if(null!=bufferedDrawable){
      bufferedDrawable.draw(canvas);
    }
    //Step3. Draw the progress drawable.
    if(null!=progressDrawable){
      progressDrawable.draw(canvas);
    }
    //Step4. Draw the marked advertise.
    drawMarkedDrawable(canvas);
  }

  private void drawMarkedDrawable(Canvas canvas) {
    if (markGroupCount == 0) {
      return;
    }
    Rect backgroundBounds = progressBackground.getBounds();
    int progressBarHeight = backgroundBounds.height();
    int barTop = backgroundBounds.centerY() - progressBarHeight / 2;
    int barBottom = barTop + progressBarHeight;

    long[] markGroupTimesMs = Assertions.checkNotNull(this.markGroupTimesMs);
    boolean[] playedMarkGroups = Assertions.checkNotNull(this.playedMarkGroups);
    int intrinsicWidth = markReadDrawable.getIntrinsicWidth();

    int adMarkerOffset = intrinsicWidth / 2;
    for (int i = 0; i < markGroupCount; i++) {
      long markGroupTimeMs = Util.constrainValue(markGroupTimesMs[i], 0, duration);
      int markerPositionOffset =
          (int) (backgroundBounds.width() * markGroupTimeMs / duration) - adMarkerOffset;
      int markerLeft = backgroundBounds.left + Math.min(backgroundBounds.width() - intrinsicWidth,
          Math.max(0, markerPositionOffset));
      if(playedMarkGroups[i]){
        //read
        markReadDrawable.setBounds(markerLeft, barTop, markerLeft + intrinsicWidth, barBottom);
        markReadDrawable.draw(canvas);
      } else {
        //unread.
        markUnreadDrawable.setBounds(markerLeft, barTop, markerLeft + intrinsicWidth, barBottom);
        markUnreadDrawable.draw(canvas);
      }
    }
  }

  private void drawPlayHead(Canvas canvas) {
    if (duration <= 0) {
      return;
    }
    Rect scrubberBounds = scrubberDrawable.getBounds();
    int scrubberDrawableWidth = (int) (scrubberDrawable.getIntrinsicWidth() * scrubberScale);
    int scrubberDrawableHeight = (int) (scrubberDrawable.getIntrinsicHeight() * scrubberScale);
    int playHeadX = scrubberBounds.centerX();
    int playHeadY = scrubberBounds.centerY();
    scrubberDrawable.setBounds(
        playHeadX - scrubberDrawableWidth / 2,
        playHeadY - scrubberDrawableHeight / 2,
        playHeadX + scrubberDrawableWidth / 2,
        playHeadY + scrubberDrawableHeight / 2);
    scrubberDrawable.draw(canvas);
  }

  @RequiresApi(29)
  private void setSystemGestureExclusionRectsV29(int width, int height) {
    if (lastExclusionRectangle != null
            && lastExclusionRectangle.width() == width
            && lastExclusionRectangle.height() == height) {
      // Allocating inside onLayout is considered a DrawAllocation lint error, so avoid if possible.
      return;
    }
    lastExclusionRectangle = new Rect(/* left= */ 0, /* top= */ 0, width, height);
//    setSystemGestureExclusionRects(Collections.singletonList(lastExclusionRectangle));
  }

  private String getProgressText() {
    return Util.getStringForTime(formatBuilder, formatter, position);
  }

  private long getPositionIncrement() {
    return keyTimeIncrement == C.TIME_UNSET
        ? (duration == C.TIME_UNSET ? 0 : (duration / keyCountIncrement)) : keyTimeIncrement;
  }

  private boolean setDrawableLayoutDirection(Drawable drawable) {
    return Util.SDK_INT >= 23 && setDrawableLayoutDirection(drawable, getLayoutDirection());
  }

  private static boolean setDrawableLayoutDirection(Drawable drawable, int layoutDirection) {
    return Util.SDK_INT >= 23 && drawable.setLayoutDirection(layoutDirection);
  }
}
