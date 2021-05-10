package org.cz.media.player.library.extension.detect;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import org.cz.media.player.library.extension.ProgressStatusView;


/**
 * @author Created by cz
 * @date 2020/8/31 4:41 PM
 * @email binigo110@126.com
 *
 * Detect gesture on the right side.
 * Only when the finger scroll vertically and on the right side.
 *
 */
public class SoundGestureDetector extends ScrollGestureDetector {
    private static float MAX_VOLUME_VALUE=15f;
    private View parentView;
    private ProgressStatusView progressStatusView;
    private ImageView hintImageView;
    private boolean interceptDetectGesture;

    public SoundGestureDetector(Context context, View parentView, ProgressStatusView progressStatusView, ImageView hintImageView) {
        super(context);
        this.parentView=parentView;
        this.progressStatusView = progressStatusView;
        this.hintImageView=hintImageView;
    }

    @Override
    public boolean onInterceptDetectGesture(View v, float startX, float startY, float moveX, float moveY) {
        interceptDetectGesture=startX >= v.getWidth()/2 && Math.abs(moveX)< Math.abs(moveY);
        return interceptDetectGesture;
    }

    @Override
    public void onDetectGesture(View v, float startX, float startY, float diffX, float diffY, float moveX, float moveY) {
        if(null!=progressStatusView){
            parentView.setVisibility(View.VISIBLE);
            Resources resources = v.getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            progressStatusView.offset(Math.round(-diffY/displayMetrics.density));
            int progress = progressStatusView.getProgress();
            hintImageView.setImageLevel(progress);
        }
    }

    @Override
    public void onStopDetect(View v) {
        super.onStopDetect(v);
        parentView.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if(interceptDetectGesture){
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                int max = progressStatusView.getMax();
                progressStatusView.offset((int) (MAX_VOLUME_VALUE/max));
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                int max = progressStatusView.getMax();
                progressStatusView.offset((int) (-MAX_VOLUME_VALUE/max));
                return true;
            }
        }
        return false;
    }
}
