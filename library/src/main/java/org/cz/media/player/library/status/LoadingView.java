package org.cz.media.player.library.status;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * @author Created by cz
 * @date 2020/10/12 2:18 PM
 * @email bingo110@126.com
 */
public class LoadingView extends AppCompatImageView {
    private static final String TAG="LoadingView";
    private static final int ANIMATION_DURATION=1600;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private final Runnable animationAction=new Runnable() {
        @Override
        public void run() {
            startAnimation();
        }
    };
    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        handler.removeCallbacks(animationAction);
        if(View.VISIBLE == visibility){
            startAnimation();
        } else {
            stopAnimation();
        }
    }

    private void startAnimation() {
        Drawable drawable = getDrawable();
        if(null!=drawable&&drawable instanceof Animatable){
            Animatable animatable = (Animatable) drawable;
            animatable.start();
            handler.postDelayed(animationAction,ANIMATION_DURATION);
        }
    }

    private void stopAnimation() {
        Drawable drawable = getDrawable();
        if(null!=drawable&&drawable instanceof Animatable){
            Animatable animatable = (Animatable) drawable;
            animatable.stop();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacks(animationAction);
        super.onDetachedFromWindow();
    }
}
