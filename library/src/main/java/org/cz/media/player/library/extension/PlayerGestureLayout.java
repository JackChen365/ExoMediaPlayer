package org.cz.media.player.library.extension;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

import org.cz.media.player.base.MediaPlayerView;
import org.cz.media.player.base.overlay.Touchable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by cz
 * @date 2020/8/31 2:33 PM
 * @email binigo110@126.com
 * The gesture detect layout.
 * This layout manager all the Gesture detector and dispatch the event to the Detector.
 *
 * @see #addGestureDetector(GestureDetector) Add a gesture detector to the list.
 */
public class PlayerGestureLayout extends FrameLayout implements Touchable {
    private static final String TAG="PlayerGestureLayout";
    /**
     * The gesture detector list.
     */
    private List<GestureDetector> gestureDetectorList =new ArrayList<>();
    /**
     * The one who handle the gesture.
     * Temporarily hold the detector to dispatch all the event only to one gesture detector.
     * When the finger up from the screen. We release the gesture detector.
     */
    private GestureDetector temporaryDetectGestureListener;

    private float startX, startY;
    private boolean isBeginDragging=false;
    private int scaledTouchSlop;

    public PlayerGestureLayout(Context context) {
        this(context,null,0);
    }

    public PlayerGestureLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PlayerGestureLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        scaledTouchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * Attach to the player view.
     * We add ourselves to the player view's overlay. In that case.
     * The control layout is over us the rest of the blank area is where we could process with.
     * @param playerView
     */
    public void attachToPlayer(MediaPlayerView playerView){
        FrameLayout overlayFrameLayout = playerView.getOverlayFrameLayout();
        ViewParent parent = getParent();
        if(null==parent){
            overlayFrameLayout.addView(this, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if(MotionEvent.ACTION_DOWN==action){
            float x = event.getX();
            float y= event.getY();
            startX = x;
            startY = y;
            //determine if you need to handle the event.
            for(GestureDetector listener: gestureDetectorList){
                if(listener.disallowInterceptTouchEvent()){
                    ViewParent parent = getParent();
                    if(null!=parent){
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    break;
                }
            }
        } else if(MotionEvent.ACTION_MOVE==action){
            float x = event.getX();
            float y= event.getY();
            if(!isBeginDragging && (Math.abs(x-startX) > scaledTouchSlop|| Math.abs(y-startY)> scaledTouchSlop)){
                //Start scroll.
                isBeginDragging = true;
                ViewParent parent = getParent();
                if(null!=parent){
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                return true;
            }
        } else if(MotionEvent.ACTION_CANCEL==action|| MotionEvent.ACTION_UP==action){
            startX = startY = 0;
        }
        return isBeginDragging||super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Detect the gesture.
        int action = event.getActionMasked();
        //Dispatch all the events to the observers.
        dispatchTouchEvent(this,event);
        if(MotionEvent.ACTION_DOWN==action){
            dispatchStartDetectEvent();
            //Handle this touch event.
            if(null!=temporaryDetectGestureListener){
                ViewParent parent = getParent();
                if(null!=parent){
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                return true;
            }
        } else if(MotionEvent.ACTION_CANCEL==action|| MotionEvent.ACTION_UP==action){
            temporaryDetectGestureListener=null;
            dispatchStopDetectEvent();
        }
        return super.onTouchEvent(event);
    }

    private void dispatchStartDetectEvent() {
        for(GestureDetector listener: gestureDetectorList){
            listener.onStartDetect(this);
        }
    }

    private void dispatchStopDetectEvent() {
        for(GestureDetector listener: gestureDetectorList){
            listener.onStopDetect(this);
        }
    }

    private void dispatchTouchEvent(View v, MotionEvent event){
        if(null!=temporaryDetectGestureListener){
            temporaryDetectGestureListener.onTouchEvent(v,event);
        } else {
            for(GestureDetector listener: gestureDetectorList){
                if (listener.onInterceptTouchEvent(v, event)) {
                    temporaryDetectGestureListener = listener;
                    listener.onTouchEvent(v,event);
                    break;
                }
                listener.onTouchEvent(v,event);
            }
            //Cancel the children that we maybe processed somewhere in between.
            if(null!=temporaryDetectGestureListener){
                MotionEvent cancelEvent = MotionEvent.obtain(event);
                cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                for(GestureDetector listener: gestureDetectorList){
                    if(listener!=temporaryDetectGestureListener){
                        listener.onTouchEvent(this,cancelEvent);
                    }
                }
            }
        }
    }

    public void addGestureDetector(GestureDetector detector){
        gestureDetectorList.add(detector);
    }


}
