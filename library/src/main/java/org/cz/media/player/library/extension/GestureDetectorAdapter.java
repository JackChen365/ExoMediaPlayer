package org.cz.media.player.library.extension;

import android.view.MotionEvent;
import android.view.View;

/**
 * The gesture detector adapter class.
 * Because not all the method you will want to process.
 * Another benefit is When I change one method in the interface. It won't change everything.
 */
public class GestureDetectorAdapter implements GestureDetector {

    @Override
    public void onStartDetect(View v) {
    }

    @Override
    public void onTouchEvent(View v, MotionEvent e) {
    }

    @Override
    public boolean onInterceptTouchEvent(View v, MotionEvent e) {
        return false;
    }

    @Override
    public void onStopDetect(View v) {
    }
}