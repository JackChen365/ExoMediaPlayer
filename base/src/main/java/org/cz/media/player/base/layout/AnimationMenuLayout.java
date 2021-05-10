package org.cz.media.player.base.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.cz.media.player.base.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by cz
 * @date 2020/9/11 11:38 AM
 * @email binigo110@126.com
 */
public class AnimationMenuLayout extends SimpleConstraintLayout {
    private final int[] tmpDrawingLocation = new int[2];
    private final int[] tmpScreenLocation = new int[2];
    private final int[] tmpAppLocation = new int[2];
    private final Rect tempRect = new Rect();

    private boolean mIsShowing;
    private boolean mIsTransitioningToDismiss;
    private boolean mIsDropdown;
    private final List<VisibilityListener> visibilityListeners = new ArrayList<>();

    public AnimationMenuLayout(Context context) {
        super(context);
    }

    public AnimationMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimationMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs,defStyleAttr);
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
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(),attrs);
    }

    public static class LayoutParams extends SimpleConstraintLayout.LayoutParams {
        Animation enterAnimation=null;
        Animation exitAnimation=null;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PlayerControllerLayout_layout, R.attr.playerControllerLayout_layout, R.style.PlayerControllerLayout_layout);
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
