package org.cz.media.player.library.extension;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by cz
 * @date 2020/9/11 11:10 AM
 * @email binigo110@126.com
 *
 * The gesture detector is where we start to extend our gesture to control the MediaPlayer.
 * For instance.
 * Your finger keeps scrolling vertically might change the brightness of the screen.
 * Or move horizontally may change the progress of the Media.
 *
 * We can not just combine all the potential gestures all together. That's why we have this interface.
 *
 * All the sub-classes below were the basic implementation that we used in the projects.
 *
 * @see org.cz.media.player.library.extension.detect.AcceleratePressGestureDetector
 * @see org.cz.media.player.library.extension.detect.BrightnessGestureDetector
 * @see org.cz.media.player.library.extension.detect.DoubleTapGestureDetector
 * @see org.cz.media.player.library.extension.detect.LongTapGestureDetector
 * @see org.cz.media.player.library.extension.detect.MultipleTapGestureDetector
 * @see org.cz.media.player.library.extension.detect.PlayerProgressGestureDetector
 * @see org.cz.media.player.library.extension.detect.ScrollGestureDetector
 * @see org.cz.media.player.library.extension.detect.SingleTapGestureDetector
 * @see org.cz.media.player.library.extension.detect.SoundGestureDetector
 *
 *
 */
public interface GestureDetector {
    /**
     * Make the container handle the event in the beginning.
     * Some gestures like scrolling horizontally may be a conflict with the container outside.
     * For example if the View inside the ViewPager. Because you are going to detect the gesture.
     * So you will not know if you need to handle the event or not, And the container will consume the event every time.
     * Under this circumstance and make sure you have the chance to receive the events. You could disallow the parent view to intercept the events.
     * @return
     */
    default boolean disallowInterceptTouchEvent(){
        return false;
    }
    /**
     * When the finger presses the screen. Just like the motion event: {@link MotionEvent#ACTION_DOWN}
     * You could do somethings like initialize your widget or some class fields you might want to use.
     * Noticed whatever you will handle the event or not.
     * The pair of functions {@link #onStartDetect(View)} and {@link #onStopDetect(View)} will always be invoke.
     *
     * @param v The parent view.
     */
    void onStartDetect(View v);

    /**
     * Return true if you want to handle all the following events.
     * It more like the The {@link android.view.ViewGroup#onInterceptTouchEvent(MotionEvent)}
     * But here the difference was everything implementation has the change to intercept the event by their own behavior.
     *
     * For example, every detector receives the event, Meanwhile, one guy guesses This gesture seems like the finger scrolling horizontally.
     * I could handle it. then next time you return true and the others will never receive the event.
     * @param v
     * @param e
     * @return
     */
    boolean onInterceptTouchEvent(View v, MotionEvent e);

    /**
     * The touch event.
     * This method will invoked Either no one intercept the event or you intercept the event.
     * {@link #onInterceptTouchEvent(View, MotionEvent)} means you want to handle the event.
     *
     * @param v
     * @param e
     */
    void onTouchEvent(View v, MotionEvent e);

    /**
     * The stop event. Occur when the event was {@link MotionEvent#ACTION_UP} or {@link MotionEvent#ACTION_CANCEL}
     * No matter you handle the event or not, The method still invoked when the event received.
     * @param v
     */
    void onStopDetect(View v);

    /**
     * Process key event.
     * @param event
     * @return
     */
    default boolean dispatchKeyEvent(KeyEvent event){
        return false;
    }
}