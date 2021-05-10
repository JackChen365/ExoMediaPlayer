package org.cz.media.player.library.extension.detect;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import org.cz.media.player.library.extension.GestureDetectorAdapter;

/**
 * @author Created by cz
 * @date 2020/9/1 4:41 PM
 * @email binigo110@126.com
 *
 * Detect long click event.
 *
 */
public class LongTapGestureDetector extends GestureDetectorAdapter {
    private View layout;
    private int lastX,lastY;
    private int touchSlop;

    private long startTabTime;
    private boolean abortLongPressEvent;
    private OnLongClickListener listener;
    private CheckForLongPress pendingCheckForLongPress;

    public LongTapGestureDetector(View layout) {
        this.layout=layout;
        Context context = layout.getContext();
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public void onTouchEvent(View v, MotionEvent e) {
        super.onTouchEvent(v,e);
        int action = e.getActionMasked();
        if(MotionEvent.ACTION_DOWN==action){
            lastX= (int) e.getX();
            lastY= (int) e.getY();
            startTabTime= SystemClock.elapsedRealtime();
            if(null==pendingCheckForLongPress){
                pendingCheckForLongPress=new CheckForLongPress();
            }
            layout.postDelayed(pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout());
        } else if(MotionEvent.ACTION_MOVE==action){
            final int x = (int) e.getX();
            final int y = (int) e.getY();
            int diffX = Math.abs(x-lastX);
            int diffY = Math.abs(y-lastY);
            if ((diffX >= touchSlop) || diffY>=touchSlop){
                //start scroll, We cancel the click event.
                abortLongPressEvent =true;
                removeTapCallback();
            }
        } else if(MotionEvent.ACTION_UP==action){
            long tapElapsedRealtime = SystemClock.elapsedRealtime() - startTabTime;
            int longPressTimeout = ViewConfiguration.getLongPressTimeout();
            if(!abortLongPressEvent||tapElapsedRealtime<longPressTimeout){
                removeTapCallback();
            }
            abortLongPressEvent=false;
        } else if(MotionEvent.ACTION_CANCEL==action){
            removeTapCallback();
            abortLongPressEvent=false;
        }
    }

    private final class CheckForTap implements Runnable {
        public void run() {
            postCheckForLongClick(ViewConfiguration.getTapTimeout());
        }
    }

    /**
     * Remove the tap detection event.
     */
    private void removeTapCallback() {
        layout.removeCallbacks(pendingCheckForLongPress);
    }

    private void postCheckForLongClick(int delayOffset) {
        if (pendingCheckForLongPress == null) {
            pendingCheckForLongPress = new CheckForLongPress();
        }
        layout.postDelayed(pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - delayOffset);
    }

    class CheckForLongPress implements Runnable {

        public void run() {
            //The function already invoked. So abort when the finger off the screen.
            abortLongPressEvent=true;
            ViewParent parent = layout.getParent();
            if (parent != null&&null!=listener) {
                //trigger long click event.
                listener.onLongClick(layout);
            }
        }
    }

    public void setOnLongClickListener(OnLongClickListener listener){
        this.listener=listener;
    }

    /**
     * Interface definition for a callback to be invoked when a view has been clicked and held.
     */
    public interface OnLongClickListener {
        /**
         * Called when a view has been clicked and held.
         *
         * @param v The view that was clicked and held.
         *
         * @return true if the callback consumed the long click, false otherwise.
         */
        void onLongClick(View v);
    }


}
