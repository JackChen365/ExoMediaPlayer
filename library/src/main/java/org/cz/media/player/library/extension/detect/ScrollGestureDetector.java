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
 * Detect scroll gesture.
 */
public class ScrollGestureDetector extends GestureDetectorAdapter {
    private float startX,startY;
    private float lastX,lastY;
    private float moveX,moveY;
    private boolean startDetectGesture;
    private boolean interceptTouchEvent;

    private int scaledTouchSlop;

    public ScrollGestureDetector(Context context){
        scaledTouchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public final boolean onInterceptTouchEvent(View v, MotionEvent e) {
        return interceptTouchEvent;
    }

    @Override
    public final void onTouchEvent(View v, MotionEvent event) {
        super.onTouchEvent(v, event);
        //Detect the gesture.
        int action = event.getActionMasked();
        if(MotionEvent.ACTION_DOWN==action){
            float x = event.getX();
            float y= event.getY();
            startX = x;
            startY = y;
            lastX = x;
            lastY = y;
        } else if(MotionEvent.ACTION_MOVE==action){
            float x = event.getX();
            float y= event.getY();
            float diffX=x-lastX;
            float diffY=y-lastY;
            moveX += diffX;
            moveY += diffY;
            if(!startDetectGesture && (Math.abs(x-startX) > scaledTouchSlop|| Math.abs(y-startY)> scaledTouchSlop)){
                //Start scroll.
                startDetectGesture = true;
                if(onInterceptDetectGesture(v,startX,startY,moveX,moveY)){
                    interceptTouchEvent = true;
                }
            }
            if(interceptTouchEvent){
                onDetectGesture(v,startX,startY,diffX,diffY,moveX,moveY);
            }
            lastX = x;
            lastY = y;
        } else if(MotionEvent.ACTION_CANCEL==action|| MotionEvent.ACTION_UP==action){
            startX = startY = 0;
            lastX = lastY = 0;
            moveX = moveY = 0;
            startDetectGesture =false;
            interceptTouchEvent=false;
        }
    }

    public boolean onInterceptDetectGesture(View v, float startX, float startY, float moveX, float moveY) {
        return false;
    }

    public void onDetectGesture(View v, float startX, float startY, float diffX, float diffY, float moveX, float moveY){
    }

}
