package org.cz.media.player.base.overlay;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author Created by cz
 * @date 2020/9/28 12:35 PM
 * @email bingo110@126.com
 */
public class FrameOverlayLayout extends FrameLayout {
    private boolean dispatchTouchEvent=true;
    public FrameOverlayLayout(@NonNull Context context) {
        super(context);
    }

    public FrameOverlayLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameOverlayLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * This flag determined if we want to handle the touch event.
     * @param dispatchTouchEvent
     */
    public void setDispatchTouchEvent(boolean dispatchTouchEvent){
        this.dispatchTouchEvent = dispatchTouchEvent;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return dispatchTouchEvent&&super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View childView = getChildAt(i);
            if(childView.dispatchKeyEvent(event)){
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
