package org.cz.media.player.library.extension.detect;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import org.cz.media.player.library.extension.GestureDetectorAdapter;

import java.util.Arrays;

/**
 * @author Created by cz
 * @date 2020/9/1 4:41 PM
 * @email binigo110@126.com
 *
 * Detect single tap.
 */
public class SingleTapGestureDetector extends GestureDetectorAdapter {
    private static final String TAG="SingleTapGestureDetector";
    private View layout;
    private int lastX,lastY;
    private int touchSlop;
    private View.OnClickListener listener;
    /**
     * When the finger starts scroll or we detect this is a double-tap. We abort the event.
     */
    private boolean abortSingleTap;
    private boolean abortWhenTimeout;
    private PerformClick performClick;
    private long startTabTime;
    private long[] detectTabTimes;
    private int tapTimeout;

    public SingleTapGestureDetector(View layout) {
        this(layout, ViewConfiguration.getDoubleTapTimeout(),2);
    }

    public SingleTapGestureDetector(View layout, int tapTimeout, int detectTapCount){
        this.layout=layout;
        if(0 < tapTimeout){
            this.tapTimeout=tapTimeout;
        } else {
            this.tapTimeout= Math.max(ViewConfiguration.getDoubleTapTimeout(), ViewConfiguration.getLongPressTimeout());
        }
        this.detectTabTimes = new long[detectTapCount];
        Context context = layout.getContext();
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setAbortWhenTimeout(boolean abortWhenTimeout) {
        this.abortWhenTimeout = abortWhenTimeout;
    }

    @Override
    public boolean onInterceptTouchEvent(View v, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(View v, MotionEvent e) {
        int action = e.getActionMasked();
        if(MotionEvent.ACTION_DOWN==action){
            lastX= (int) e.getX();
            lastY= (int) e.getY();
            layout.removeCallbacks(performClick);
            if(!abortSingleTap){
                startTabTime = SystemClock.elapsedRealtime();
            }
        } else if(MotionEvent.ACTION_MOVE==action){
            final int x = (int) e.getX();
            final int y = (int) e.getY();
            int diffX = Math.abs(x-lastX);
            int diffY = Math.abs(y-lastY);
            if ((diffX >= touchSlop) || diffY>=touchSlop){
                //start scroll, We cancel the click event.
                abortSingleTap =true;
                layout.removeCallbacks(performClick);
            }
        } else if(MotionEvent.ACTION_UP==action){
            if(performClick == null) {
                performClick = new PerformClick();
            }
            int length = detectTabTimes.length - 1;
            System.arraycopy(detectTabTimes, 1, detectTabTimes, 0, length);
            detectTabTimes[length] = SystemClock.elapsedRealtime();
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (detectTabTimes[length] - detectTabTimes[0] < tapTimeout) {
                //Notice this could be a double tap.
                abortSingleTap=false;
                removeTapCallback();
                Arrays.fill(detectTabTimes,0);
                Log.i(TAG,"detect double tap");
            } else if(!abortSingleTap){
                Log.i(TAG,"not abortSingleTap");
                if(elapsedRealtime-startTabTime<tapTimeout){
                    removeTapCallback();
                    long doubleTabTimeout = tapTimeout-(elapsedRealtime - startTabTime);
                    layout.postDelayed(performClick,doubleTabTimeout);
                } else if(abortWhenTimeout){
                    abortSingleTap=false;
                    removeTapCallback();
                } else {
                    if(!layout.post(performClick)) {
                        performClick();
                    }
                    Arrays.fill(detectTabTimes,0);
                }
            }
            abortSingleTap=false;
        } else if(MotionEvent.ACTION_CANCEL==action){
            Log.i(TAG,"ACTION_CANCEL");
            abortSingleTap =false;
            removeTapCallback();
        }
    }

    /**
     * Remove the tap detection event.
     */
    private void removeTapCallback() {
        if (null!=layout) {
            layout.removeCallbacks(performClick);
        }
    }

    private void performClick(){
        abortSingleTap =false;
        if(null!=listener){
            listener.onClick(layout);
        }
    }

    public void setOnSingleTapListener(View.OnClickListener listener){
        this.listener=listener;
    }

    private final class PerformClick implements Runnable {
        public void run() {
            performClick();
        }
    }


}
