package org.cz.media.player.library;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Created by cz
 * @date 2020/10/12 3:22 PM
 * @email bingo110@126.com
 */
public class AppCompatAudioPlayerView extends AppCompatMediaPlayerView{
    public AppCompatAudioPlayerView(@NonNull Context context) {
        super(context);
    }

    public AppCompatAudioPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AppCompatAudioPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View generateSurfaceView(@NonNull Context context, int surfaceType) {
        return null;
    }
}
