package org.cz.media.player.base.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.C;
import org.cz.media.player.base.R;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Created by cz
 * @date 2020/8/11 2:38 PM
 * @email binigo110@126.com
 */
public class AnimationControlLayout extends SimpleConstraintLayout {
    private static final String TAG="AnimationControlLayout";
    /** The default show timeout, in milliseconds. */
    public static final int DEFAULT_SHOW_TIMEOUT_MS = 5000;

    private static final int CONTROL_NO_GRAVITY=-1;
    private static final int CONTROL_LEFT_PANEL=0x00;
    private static final int CONTROL_TOP_PANEL=0x01;
    private static final int CONTROL_RIGHT_PANEL=0x02;
    private static final int CONTROL_BOTTOM_PANEL=0x03;
    private static final int CONTROL_CENTER_PANEL=0x04;

    private final CopyOnWriteArrayList<VisibilityListener> visibilityListeners = new CopyOnWriteArrayList<>();
    private final Runnable hideAction=this::hide;
    private final Runnable animationDelayedHideAction=new Runnable() {
        @Override
        public void run() {
            Log.i(TAG,"animationDelayedHideAction");
            setVisibility(View.INVISIBLE);
        }
    };
    private boolean hideAfterTimeout=false;
    private int showTimeoutMs=DEFAULT_SHOW_TIMEOUT_MS;
    private long hideAtMs= C.TIME_UNSET;

    private int leftLayoutResource;
    private int topLayoutResource;
    private int rightLayoutResource;
    private int bottomLayoutResource;
    private int centerLayoutResource;

    public AnimationControlLayout(Context context) {
        this(context,null,R.attr.playerAnimationControlLayout);
        initialControlLayout();
    }

    public AnimationControlLayout(Context context, AttributeSet attrs) {
        this(context, attrs,R.attr.playerAnimationControlLayout);
    }

    public AnimationControlLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        TypedArray a = null;
        try {
            context = org.cz.media.player.base.ContextHelper.wrapPlayerContext(context, R.attr.mediaPlayer);
            a = context.obtainStyledAttributes(attrs, R.styleable.AnimationControlLayout, defStyleAttr,R.style.PlayerAnimationControlLayout);
            leftLayoutResource = a.getResourceId(R.styleable.AnimationControlLayout_control_left_layout, NO_ID);
            topLayoutResource = a.getResourceId(R.styleable.AnimationControlLayout_control_top_layout, NO_ID);
            rightLayoutResource = a.getResourceId(R.styleable.AnimationControlLayout_control_right_layout, NO_ID);
            bottomLayoutResource = a.getResourceId(R.styleable.AnimationControlLayout_control_bottom_layout, NO_ID);
            centerLayoutResource = a.getResourceId(R.styleable.AnimationControlLayout_control_center_layout, NO_ID);

            setShowTimeoutMs(a.getInteger(R.styleable.AnimationControlLayout_control_show_timeout,0));
        } finally {
            if(null!=a){
                a.recycle();
            }
        }
    }

    private void addLeftLayoutRule(View layout) {
        if(null!=layout){
            LayoutParams layoutParams = (LayoutParams) layout.getLayoutParams();
            layoutParams.addRule(SimpleConstraintLayout.LEFT_TO_LEFT,SimpleConstraintLayout.PARENT);
            layoutParams.addRule(SimpleConstraintLayout.TOP_TO_BOTTOM,R.id.controllerTopLayout);
            layoutParams.addRule(SimpleConstraintLayout.BOTTOM_TO_TOP,R.id.controllerBottomLayout);
        }
    }

    private void addTopLayoutRule(View layout) {
        if(null!=layout){
            LayoutParams layoutParams = (LayoutParams) layout.getLayoutParams();
            layoutParams.addRule(SimpleConstraintLayout.LEFT_TO_LEFT,SimpleConstraintLayout.PARENT);
            layoutParams.addRule(SimpleConstraintLayout.TOP_TO_TOP,SimpleConstraintLayout.PARENT);
            layoutParams.addRule(SimpleConstraintLayout.RIGHT_TO_RIGHT,SimpleConstraintLayout.PARENT);
        }
    }

    private void addRightLayoutRule(View layout) {
        if(null!=layout){
            LayoutParams layoutParams = (LayoutParams) layout.getLayoutParams();
            layoutParams.addRule(SimpleConstraintLayout.RIGHT_TO_RIGHT,SimpleConstraintLayout.PARENT);
            layoutParams.addRule(SimpleConstraintLayout.TOP_TO_BOTTOM,R.id.controllerTopLayout);
            layoutParams.addRule(SimpleConstraintLayout.BOTTOM_TO_TOP,R.id.controllerBottomLayout);
        }
    }

    private void addBottomLayoutRule(View layout) {
        if(null!=layout){
            LayoutParams layoutParams = (LayoutParams) layout.getLayoutParams();
            layoutParams.addRule(SimpleConstraintLayout.LEFT_TO_LEFT,SimpleConstraintLayout.PARENT);
            layoutParams.addRule(SimpleConstraintLayout.BOTTOM_TO_BOTTOM,SimpleConstraintLayout.PARENT);
            layoutParams.addRule(SimpleConstraintLayout.RIGHT_TO_RIGHT,SimpleConstraintLayout.PARENT);
        }
    }

    private void addCenterLayoutRule(View layout) {
        if(null!=layout){
            LayoutParams layoutParams = (LayoutParams) layout.getLayoutParams();
            layoutParams.addRule(SimpleConstraintLayout.LEFT_TO_RIGHT,R.id.controllerLeftLayout);
            layoutParams.addRule(SimpleConstraintLayout.TOP_TO_BOTTOM,R.id.controllerTopLayout);
            layoutParams.addRule(SimpleConstraintLayout.RIGHT_TO_LEFT,R.id.controllerRightLayout);
            layoutParams.addRule(SimpleConstraintLayout.BOTTOM_TO_TOP,R.id.controllerBottomLayout);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initialControlLayout();
    }

    private void initialControlLayout() {
        int childCount = getChildCount();
        for(int i=0;i < childCount;i++){
            View childView = getChildAt(i);
            LayoutParams layoutParams= (LayoutParams) childView.getLayoutParams();
            switch(layoutParams.controlPanelType){
                case CONTROL_LEFT_PANEL:
                    childView.setId(R.id.controllerLeftLayout);
                    break;
                case CONTROL_TOP_PANEL:
                    childView.setId(R.id.controllerTopLayout);
                    break;
                case CONTROL_RIGHT_PANEL:
                    childView.setId(R.id.controllerRightLayout);
                    break;
                case CONTROL_BOTTOM_PANEL:
                    childView.setId(R.id.controllerBottomLayout);
                    break;
                case CONTROL_CENTER_PANEL:
                    childView.setId(R.id.controllerCenterLayout);
                    break;
            }
        }
        Context context = getContext();
        View leftLayout = checkAndInflateControlLayout(context, R.id.controllerLeftLayout, leftLayoutResource);
        addLeftLayoutRule(leftLayout);

        View topLayout = checkAndInflateControlLayout(context, R.id.controllerTopLayout, topLayoutResource);
        addTopLayoutRule(topLayout);

        View rightLayout = checkAndInflateControlLayout(context, R.id.controllerRightLayout, rightLayoutResource);
        addRightLayoutRule(rightLayout);

        View bottomLayout = checkAndInflateControlLayout(context, R.id.controllerBottomLayout, bottomLayoutResource);
        addBottomLayoutRule(bottomLayout);

        View centerLayout = checkAndInflateControlLayout(context, R.id.controllerCenterLayout, centerLayoutResource);
        addCenterLayoutRule(centerLayout);

        onInitializeControlLayout();
    }

    private View checkAndInflateControlLayout(Context context, int id, int resourceId) {
        View view = findViewById(id);
        if(null==view&& View.NO_ID!=resourceId){
            LayoutInflater factory = LayoutInflater.from(context);
            view = factory.inflate(resourceId,this,false);
            view.setId(id);
            addView(view);
        }
        return view;
    }

    protected void onInitializeControlLayout(){
    }

    public void setHideAfterTimeout(boolean hideAfterTimeout) {
        this.hideAfterTimeout = hideAfterTimeout;
    }

    /**
     * Returns the playback controls timeout. The playback controls are automatically hidden after
     * this duration of time has elapsed without user input.
     *
     * @return The duration in milliseconds. A non-positive value indicates that the controls will
     *     remain visible indefinitely.
     */
    public int getShowTimeoutMs() {
        return showTimeoutMs;
    }

    /**
     * Sets the playback controls timeout. The playback controls are automatically hidden after this
     * duration of time has elapsed without user input.
     *
     * @param showTimeoutMs The duration in milliseconds. A non-positive value will cause the controls
     *     to remain visible indefinitely.
     */
    public void setShowTimeoutMs(int showTimeoutMs) {
        this.showTimeoutMs = showTimeoutMs;
        if (isVisible()) {
            // Reset the timeout.
            hideAfterTimeout();
        }
    }

    /**
     * Shows the playback controls. If {@link #getShowTimeoutMs()} is positive then the controls will
     * be automatically hidden after this duration of time has elapsed without user input.
     */
    public void show() {
        if (!isVisible()) {
            clearAnimation();
            setVisibility(VISIBLE);
            removeCallbacks(hideAction);
            int childCount = getChildCount();
            for(int i=0;i<childCount;i++){
                View childView = getChildAt(i);
                childView.clearAnimation();
                LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
                if(null!=layoutParams.enterAnimation){
                    childView.startAnimation(layoutParams.enterAnimation);
                }
            }
            Log.i(TAG,"show:"+isShown());
            for (VisibilityListener visibilityListener : visibilityListeners) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            onLayoutShown();
        }
        // Call hideAfterTimeout even if already visible to reset the timeout.
        hideAfterTimeout();
    }

    public void onLayoutShown(){
    }

    /**
     * Hides the controller.
     */
    public void hide() {
        if (isVisible()) {
            long animationDuration=0;
            setVisibility(!isShown()? View.INVISIBLE: View.VISIBLE);
            int childCount = getChildCount();
            for(int i=0;i<childCount;i++){
                View childView = getChildAt(i);
                LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
                if(null!=layoutParams.exitAnimation){
                    childView.clearAnimation();
                    long duration = layoutParams.exitAnimation.getDuration();
                    if(animationDuration < duration){
                        animationDuration= duration;
                    }
                    childView.startAnimation(layoutParams.exitAnimation);
                }
            }
            for (VisibilityListener visibilityListener : visibilityListeners) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            Log.i(TAG,"hide:"+isShown());
            removeCallbacks(hideAction);
            postDelayed(animationDelayedHideAction,animationDuration);
        }
        hideAtMs = C.TIME_UNSET;
        onLayoutHidden();
    }

    public void onLayoutHidden(){
    }

    /**
     * Returns whether the controller is currently visible.
     */
    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    void hideAfterTimeout() {
        if(hideAfterTimeout){
            removeCallbacks(hideAction);
            removeCallbacks(animationDelayedHideAction);
            if (showTimeoutMs > 0) {
                hideAtMs = SystemClock.uptimeMillis() + showTimeoutMs;
                if (isAttachedToWindow()) {
                    postDelayed(hideAction, showTimeoutMs);
                }
            } else {
                hideAtMs = C.TIME_UNSET;
            }
        }
    }

    @Override
    public final boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            removeCallbacks(hideAction);
            removeCallbacks(animationDelayedHideAction);
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            hideAfterTimeout();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (hideAtMs != C.TIME_UNSET) {
            long delayMs = hideAtMs - SystemClock.uptimeMillis();
            if (delayMs <= 0) {
                hide();
            } else {
                postDelayed(hideAction, delayMs);
            }
        } else if (isVisible()) {
            hideAfterTimeout();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(hideAction);
        removeCallbacks(animationDelayedHideAction);
    }

    /**
     * Adds a {@link VisibilityListener}.
     *
     * @param listener The listener to be notified about visibility changes.
     */
    public void addVisibilityListener(VisibilityListener listener) {
        visibilityListeners.add(listener);
    }

    /**
     * Removes a {@link VisibilityListener}.
     *
     * @param listener The listener to be removed.
     */
    public void removeVisibilityListener(VisibilityListener listener) {
        visibilityListeners.remove(listener);
    }


    @Override
    protected SimpleConstraintLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected SimpleConstraintLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public SimpleConstraintLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(),attrs);
    }

    public static class LayoutParams extends SimpleConstraintLayout.LayoutParams {
        Animation enterAnimation=null;
        Animation exitAnimation=null;
        int controlPanelType=CONTROL_NO_GRAVITY;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PlayerControllerLayout_layout, R.attr.playerControllerLayout_layout, R.style.PlayerControllerLayout_layout);
            controlPanelType=a.getInt(R.styleable.PlayerControllerLayout_layout_layout_control_panel,CONTROL_NO_GRAVITY);
            int resourceId = a.getResourceId(R.styleable.PlayerControllerLayout_layout_layout_control_animation, R.style.default_animation);
            resolveAndApplyLayoutAnimation(c,resourceId);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        private void resolveAndApplyLayoutAnimation(@NonNull Context context, int resourceId) {
            TypedArray a = context.obtainStyledAttributes(resourceId, R.styleable.ControlLayoutAnimation);
            int enterAnimationResource = a.getResourceId(R.styleable.ControlLayoutAnimation_android_windowEnterAnimation, R.anim.default_enter_animation);
            enterAnimation= AnimationUtils.loadAnimation(context,enterAnimationResource);
            int exitAnimationResource = a.getResourceId(R.styleable.ControlLayoutAnimation_android_windowExitAnimation, R.anim.default_exit_animation);
            exitAnimation= AnimationUtils.loadAnimation(context,exitAnimationResource);
            a.recycle();
        }
    }



    /**
     * Listener to be notified about changes of the visibility of the UI control.
     */
    public interface VisibilityListener {
        /**
         * Called when the visibility changes.
         *
         * @param visibility The new visibility. Either {@link View#VISIBLE} or {@link View#GONE}.
         */
        void onVisibilityChange(int visibility);
    }

}
