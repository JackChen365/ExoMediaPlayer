package org.cz.media.player.library.extension.detect;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import org.cz.media.player.library.extension.ProgressStatusView;


/**
 * @author Created by cz
 * @date 2020/8/31 4:41 PM
 * @email binigo110@126.com
 *
 * Detect gesture on the left side.
 * Only when the finger scroll vertically and on the left side.
 *
 */
public class BrightnessGestureDetector extends ScrollGestureDetector {
    private View parentView;
    private ProgressStatusView progressStatusView;
    private ImageView hintImageView;

    public BrightnessGestureDetector(Context context, View parentView, ProgressStatusView progressStatusView, ImageView imageView) {
        super(context);
        this.parentView=parentView;
        this.progressStatusView = progressStatusView;
        this.hintImageView =imageView;
    }

    @Override
    public boolean onInterceptDetectGesture(View v, float startX, float startY, float moveX, float moveY) {
        return startX < v.getWidth()/2 && Math.abs(moveX)< Math.abs(moveY);
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
}
