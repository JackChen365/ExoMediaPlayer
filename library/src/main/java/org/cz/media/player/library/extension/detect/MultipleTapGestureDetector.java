package org.cz.media.player.library.extension.detect;

import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import org.cz.media.player.library.extension.GestureDetectorAdapter;

import java.util.Arrays;

/**
 * @author Created by cz
 * @date 2020/9/4 11:41 PM
 * @email binigo110@126.com
 *
 * Detect multiple times tap event.
 *
 */
public class MultipleTapGestureDetector extends GestureDetectorAdapter {
    private long[] tapTimeArray;

    private CheckForTap checkForTap =new CheckForTap();
    private OnTapListener listener;
    private View layout;
    private long startTabTime;
    /**
     * Click interval time.
     */
    private long tapTimeout;
    private short detectTapTimes;

    public MultipleTapGestureDetector(View layout, int detectTapTimes) {
        this(layout,0,detectTapTimes);
    }

    public MultipleTapGestureDetector(View layout, int tapTimeout, int maxTapTimes) {
        this.layout=layout;
        if(0 < tapTimeout){
            this.tapTimeout = tapTimeout;
        } else {
            this.tapTimeout = ViewConfiguration.getDoubleTapTimeout();
        }
        this.tapTimeArray = new long[maxTapTimes];
    }

    @Override
    public void onTouchEvent(View v, MotionEvent e) {
        super.onTouchEvent(v,e);
        int action = e.getActionMasked();
        if(MotionEvent.ACTION_DOWN==action){
            long elapsedRealtime = SystemClock.elapsedRealtime();
            int doubleTapTimeout = ViewConfiguration.getDoubleTapTimeout();
            layout.removeCallbacks(checkForTap);
            if(0 == detectTapTimes){
                startTabTime= SystemClock.elapsedRealtime();
            } else if(elapsedRealtime-startTabTime>=doubleTapTimeout){
                detectTapTimes =0;
                startTabTime= SystemClock.elapsedRealtime();
            }
            if(elapsedRealtime-startTabTime<doubleTapTimeout){
                detectTapTimes++;
            }
        } else if(MotionEvent.ACTION_UP==action){
            int length = tapTimeArray.length - 1;
            System.arraycopy(tapTimeArray, 1, tapTimeArray, 0, length);
            tapTimeArray[length] = SystemClock.elapsedRealtime();
            Log.i("onTouchEvent","time:"+ Arrays.toString(tapTimeArray)+" "+(tapTimeArray[length] - tapTimeArray[0])+" tapTimeout:"+tapTimeout);
            if (tapTimeArray[length] - tapTimeArray[0] < tapTimeout) {
                Arrays.fill(tapTimeArray,0);
                if(!layout.post(checkForTap)){
                    checkForTap.run();
                }
            }
        } else if(MotionEvent.ACTION_CANCEL==action){
            detectTapTimes =0;
            removeTapCallback();
        }
    }


    /**
     * Remove the tap detection event.
     */
    private void removeTapCallback() {
        if (null!=layout) {
            layout.removeCallbacks(checkForTap);
        }
    }

    class CheckForTap implements Runnable {

        public void run() {
            ViewParent parent = layout.getParent();
            if (null!=parent&&null!=listener) {
                //trigger multiple tap event.
                listener.onTap(layout);
            }
        }
    }

    public void setOnTapListener(OnTapListener listener){
        this.listener=listener;
    }

    public interface OnTapListener {
        void onTap(View v);
    }


}
