package org.cz.media.player.library.extension.detect;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.Player;
import org.cz.media.player.library.R;
import org.cz.media.player.base.MediaPlayerView;
import org.cz.media.player.base.timebar.DefaultTimeBar;

import java.util.concurrent.TimeUnit;

/**
 * @author Created by cz
 * @date 2020/8/31 4:41 PM
 * @email binigo110@126.com
 *
 * Detect gesture horizontally.
 *
 */
public class PlayerProgressGestureDetector extends ScrollGestureDetector {
    private final int[] tmpDrawingLocation = new int[2];
    private final int[] tmpScreenLocation = new int[2];
    private final int[] tmpAppLocation = new int[2];
    private MediaPlayerView playerView;
    private TextView textView;
    private DefaultTimeBar timeBar;
    private boolean isInterceptDetect;
    private int textOffsetX;
    private int textOffsetY;

    public PlayerProgressGestureDetector(Context context, MediaPlayerView playerView, TextView textView) {
        super(context);
        this.playerView = playerView;
        this.textView=textView;
        this.timeBar=playerView.findViewById(R.id.playerTimeBar);
    }

    @Override
    public boolean disallowInterceptTouchEvent() {
        return true;
    }

    @Override
    public void onStartDetect(View v) {
        super.onStartDetect(v);
        timeBar.beginFakeSeek();
        //Initially, align to the bottom-left corner of the anchor plus offsets.
        final int[] textDrawingLocation = getViewDrawingLocation(textView);
        int offsetX = textDrawingLocation[0];
        int offsetY = textDrawingLocation[1];
        final int[] drawingLocation = getViewDrawingLocation(timeBar);
        textOffsetX=drawingLocation[0]-offsetX;
        textOffsetY=drawingLocation[1]-offsetY;
    }

    @Override
    public boolean onInterceptDetectGesture(View v, float startX, float startY, float moveX, float moveY) {
        isInterceptDetect = Math.abs(moveX) > Math.abs(moveY);
        return isInterceptDetect;
    }

    public boolean isInterceptDetect() {
        return isInterceptDetect;
    }

    @Override
    public void onDetectGesture(View v, float startX, float startY, float diffX, float diffY, float moveX, float moveY) {
        Player player = playerView.getPlayer();
        if(null!=player){
            int width = v.getWidth();
            float fraction = diffX / width;
            long contentPosition = timeBar.getPosition();
            long duration = timeBar.getDuration();

            long position = contentPosition + Math.round(fraction * duration);
            long safePosition= Math.max(0, Math.min(duration,position));

            String formatValue = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(safePosition),
                    TimeUnit.MILLISECONDS.toMinutes(safePosition) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(safePosition)), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(safePosition) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(safePosition)));
            textView.setVisibility(View.VISIBLE);
            textView.setText(formatValue);

            int paddingLeft = timeBar.getPaddingLeft();
            int paddingRight = timeBar.getPaddingRight();
            int scrubberSize = timeBar.getScrubberSize();
            int timeBarWidth = timeBar.getWidth()-paddingLeft-paddingRight-scrubberSize;
            int offsetX = scrubberSize/2+ Math.round(safePosition * 1f / duration * timeBarWidth);

            int textViewWidth = textView.getWidth();
            int textViewHeight = textView.getHeight();
            int x = textOffsetX;
            int y = textOffsetY;
            textView.setTranslationX(x+offsetX-textViewWidth/2);
            textView.setTranslationY(y-textViewHeight);
            timeBar.seekToPosition(safePosition);
        }
    }

    private int[] getViewDrawingLocation(View view) {
        final int[] appScreenLocation = tmpAppLocation;
        final View appRootView = view.getRootView();
        appRootView.getLocationOnScreen(appScreenLocation);

        final int[] screenLocation = tmpScreenLocation;
        view.getLocationOnScreen(screenLocation);

        final int[] drawingLocation = tmpDrawingLocation;
        drawingLocation[0] = screenLocation[0] - appScreenLocation[0];
        drawingLocation[1] = screenLocation[1] - appScreenLocation[1];
        return drawingLocation;
    }

    @Override
    public void onStopDetect(View v) {
        super.onStopDetect(v);
        Player player = playerView.getPlayer();
        if(isInterceptDetect&&null!=player){
            long position = timeBar.getPosition();
            player.seekTo(position);
        }
        textView.setVisibility(View.GONE);
        textView.setTranslationX(0);
        textView.setTranslationY(0);
        isInterceptDetect=false;
        timeBar.endFakeSeek();
    }
}
