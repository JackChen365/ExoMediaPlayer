package org.cz.media.player.library.extension.detect;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import org.cz.media.player.library.extension.GestureDetectorAdapter;

/**
 * @author Created by cz
 * @date 2020/9/1 4:41 PM
 * @email binigo110@126.com
 *
 * Detect long accelerate event.
 */
public class AcceleratePressGestureDetector extends GestureDetectorAdapter {
    private static final int DEFAULT_FRAME_DRAWING_TIME=16;
    private static final long ACCELERATE_START_INTERVAL_TIME=100;
    private static final long ACCELERATE_END_INTERVAL_TIME=20;
    private static final long ACCELERATE_DURATION =2000;

    private long accelerateStartIntervalTime=ACCELERATE_START_INTERVAL_TIME;
    private long accelerateEndIntervalTime=ACCELERATE_END_INTERVAL_TIME;
    private long accelerateDuration = ACCELERATE_DURATION;
    private float animationAccelerateFraction;
    private float animationAccelerateTime;
    private boolean animationRunning;

    private int lastX,lastY;
    private int touchSlop;
    private OnAccelerateEventChangeListener listener;
    private Runnable acceleratePressAction;
    private View layout;


    public AcceleratePressGestureDetector(View layout) {
        this.layout=layout;
        Context context = layout.getContext();
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setAccelerateStartIntervalTime(long accelerateStartIntervalTime) {
        this.accelerateStartIntervalTime = accelerateStartIntervalTime;
    }

    public void setAccelerateEndIntervalTime(long accelerateEndIntervalTime) {
        this.accelerateEndIntervalTime = accelerateEndIntervalTime;
    }

    public void setAccelerateDuration(long accelerateDuration) {
        this.accelerateDuration = accelerateDuration;
    }

    @Override
    public boolean onInterceptTouchEvent(View v, MotionEvent e) {
        return animationRunning;
    }

    @Override
    public void onTouchEvent(View v, MotionEvent e) {
        super.onTouchEvent(v,e);
        int action = e.getActionMasked();
        if(MotionEvent.ACTION_DOWN==action){
            lastX= (int) e.getX();
            lastY= (int) e.getY();
            layout.removeCallbacks(acceleratePressAction);
            if(null == acceleratePressAction){
                acceleratePressAction =new Runnable(){
                    @Override
                    public void run() {
                        animationRunning=true;
                        AccelerateAction accelerateAction = new AccelerateAction();
                        accelerateAction.run();
                        if(null!=listener){
                            listener.onAccelerateStarted();
                        }
                    }
                };
            }
            int tapTimeout = ViewConfiguration.getLongPressTimeout();
            animationAccelerateFraction =0;
            animationAccelerateTime =0;
            layout.postDelayed(acceleratePressAction,tapTimeout);
        } else if(MotionEvent.ACTION_MOVE==action){
            final int x = (int) e.getX();
            final int y = (int) e.getY();
            int diffX = Math.abs(x-lastX);
            int diffY = Math.abs(y-lastY);
            if ((diffX >= touchSlop) || diffY>=touchSlop){
                //start scroll, We should cancel the click event.
                removeTapCallback();
            }
        } else if(MotionEvent.ACTION_UP==action|| MotionEvent.ACTION_CANCEL==action) {
            removeTapCallback();
        }
    }


    /**
     * Remove the tap detection event.
     */
    private void removeTapCallback() {
        //The animation is running. We are going to stop it.
        if(animationRunning){
            animationAccelerateTime=0;
            animationAccelerateFraction=0;
            if(null != listener){
                listener.onAccelerateStopped();
            }
        }
        animationRunning=false;
        layout.removeCallbacks(acceleratePressAction);
    }

    private final class AccelerateAction implements Runnable {

        public void run() {
            float frameLoopTime=DEFAULT_FRAME_DRAWING_TIME;
            if(animationAccelerateTime<= accelerateDuration){
                animationAccelerateFraction = (float) Math.pow(animationAccelerateTime/ accelerateDuration, 2);
                frameLoopTime=accelerateStartIntervalTime+(accelerateEndIntervalTime-accelerateStartIntervalTime)* animationAccelerateFraction;
            }
            if(frameLoopTime<accelerateEndIntervalTime){
                frameLoopTime=accelerateEndIntervalTime;
            }
            animationAccelerateTime+=frameLoopTime;
            if(null!=listener){
                listener.onTick();
            }
            if(animationRunning){
                layout.postDelayed(this, (long) frameLoopTime);
            }
        }
    }

    public void setOnAccelerateEventChangeListener(OnAccelerateEventChangeListener listener){
        this.listener=listener;
    }

    public interface OnAccelerateEventChangeListener{
        void onAccelerateStarted();
        void onTick();
        void onAccelerateStopped();
    }


}
