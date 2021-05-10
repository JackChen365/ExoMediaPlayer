package org.cz.media.player.library.extension;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import org.cz.media.player.library.R;


/**
 * @author Created by cz
 * @date 2020/8/31 3:31 PM
 * @email binigo110@126.com
 *
 * The progress view.
 */
public class ProgressStatusView extends View {
    public static final int HORIZONTAL= LinearLayout.HORIZONTAL;
    public static final int VERTICAL= LinearLayout.VERTICAL;

    private OnProgressChangeListener listener;
    private Drawable progressBackground;
    private Drawable progressDrawable;
    private int orientation=VERTICAL;
    private int max;
    private int progress;

    public ProgressStatusView(Context context) {
        this(context,null,0);
    }

    public ProgressStatusView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public ProgressStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressStatusView);
        setOrientation(a.getInt(R.styleable.ProgressStatusView_android_orientation,VERTICAL));
        setMax(a.getInteger(R.styleable.ProgressStatusView_android_max,100));
        setProgress(a.getInteger(R.styleable.ProgressStatusView_android_progress,0));
        setProgressDrawable(a.getDrawable(R.styleable.ProgressStatusView_android_progressDrawable));
        setProgressBackground(a.getDrawable(R.styleable.ProgressStatusView_progressBackground));
        a.recycle();
    }

    public void setOrientation(int orientation){
        this.orientation=orientation;
        requestLayout();
    }

    public void setMax(int max){
        this.max=max;
        invalidate();
    }

    public void setProgress(int progress){
        this.progress=progress;
        if(null!=listener){
            listener.onProgressChanged(this,progress,max);
        }
        invalidate();
    }

    public void offset(int delta){
        this.progress= Math.max(0, Math.min(progress+delta,max));
        if(null!=listener){
            listener.onProgressChanged(this,progress,max);
        }
        invalidate();
    }

    public void setProgressBackground(Drawable drawable) {
        progressBackground=drawable;
        invalidate();
    }

    public void setProgressDrawable(Drawable drawable){
        this.progressDrawable=drawable;
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public int getProgress() {
        return progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        if(HORIZONTAL==orientation){
            drawHorizontalProgress(canvas);
        } else if(VERTICAL==orientation){
            drawVerticalProgress(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        if(null!=progressBackground){
            int left=getPaddingLeft();
            int top=getPaddingTop();
            int right=getWidth()-getPaddingRight();
            int bottom=getHeight()-getPaddingBottom();
            progressBackground.setBounds(left,top,right,bottom);
            progressBackground.draw(canvas);
        }
    }

    private void drawHorizontalProgress(Canvas canvas) {
        if(null!=progressDrawable){
            int left=getPaddingLeft();
            int top=getPaddingTop();
            int right=getWidth()-getPaddingRight();
            int bottom=getHeight()-getPaddingBottom();
            int distance = (int) ((right - left) * (progress * 1f / max));
            progressDrawable.setBounds(left,top,left+distance,bottom);
            progressDrawable.draw(canvas);
        }
    }

    private void drawVerticalProgress(Canvas canvas) {
        if(null!=progressDrawable){
            int left=getPaddingLeft();
            int top=getPaddingTop();
            int right=getWidth()-getPaddingRight();
            int bottom=getHeight()-getPaddingBottom();
            int distance = (int) ((bottom - top) * (progress * 1f / max));
            progressDrawable.setBounds(left,bottom-distance,right,bottom);
            progressDrawable.draw(canvas);
        }
    }

    public void setOnProgressChangeListener(OnProgressChangeListener listener){
        this.listener=listener;
    }

    /**
     * Interface responsible for receiving progress value change event
     */
    public interface OnProgressChangeListener{
        /**
         * when progress changed
         * @param progressStatusView
         * @param progress
         * @param max
         */
        void onProgressChanged(ProgressStatusView progressStatusView, int progress,int max);
    }
}
