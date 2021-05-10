package org.cz.media.player.library.overlay;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.cz.media.player.library.R;
import org.cz.media.player.base.MediaPlayerView;
import org.cz.media.player.base.controller.component.PlayerComponent;
import org.cz.media.player.library.extension.PlayerGestureLayout;
import org.cz.media.player.library.extension.ProgressStatusView;
import org.cz.media.player.library.extension.detect.AcceleratePressGestureDetector;
import org.cz.media.player.library.extension.detect.BrightnessGestureDetector;
import org.cz.media.player.library.extension.detect.DoubleTapGestureDetector;
import org.cz.media.player.library.extension.detect.LongTapGestureDetector;
import org.cz.media.player.library.extension.detect.PlayerProgressGestureDetector;
import org.cz.media.player.library.extension.detect.SingleTapGestureDetector;
import org.cz.media.player.library.extension.detect.SoundGestureDetector;

/**
 * @author Created by cz
 * @date 2020/9/7 11:39 AM
 * @email binigo110@126.com
 */
public class MediaPlayerGestureDetectOverlay extends MediaPlayerOverlay {
    private static int MAX_VOLUME_VALUE=15;
    private Activity activity;

    public MediaPlayerGestureDetectOverlay(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateOverlay(Context context, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.media_player_gesture_overlay_layout,parent,false);
    }

    @Override
    public void onAttachToPlayer(MediaPlayerView playerView, View contentView) {
        super.onAttachToPlayer(playerView,contentView);
        Context context = contentView.getContext();
        //Replace the view.
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ViewGroup parentView = (ViewGroup) playerView.getParent();
        ViewGroup layout = (ViewGroup) layoutInflater.inflate(R.layout.media_player_gesture_progress_overlay_layout, parentView, false);
        replaceChildWithView(playerView,layout);
        layout.addView(playerView);

        //Initialize all the gesture detectors.
        PlayerGestureLayout playerGestureLayout=contentView.findViewById(R.id.playerGestureLayout);
        ProgressStatusView brightnessStatusView=contentView.findViewById(R.id.brightnessStatusView);
        brightnessStatusView.setOnProgressChangeListener((progressStatusView, progress,max) -> {
            Window window = activity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.screenBrightness=(progress)*1f/max;
            window.setAttributes(attributes);
        });
        View brightnessLayout = contentView.findViewById(R.id.brightnessLayout);
        ImageView brightnessImage=contentView.findViewById(R.id.brightnessImage);
        BrightnessGestureDetector brightnessGestureDetector = new BrightnessGestureDetector(context,brightnessLayout,brightnessStatusView, brightnessImage);
        playerGestureLayout.addGestureDetector(brightnessGestureDetector);

        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        ProgressStatusView soundStatusView=playerGestureLayout.findViewById(R.id.soundStatusView);
        soundStatusView.setOnProgressChangeListener((progressStatusView, progress, max) -> {
            int volume= (int) (progress*1f/max * MAX_VOLUME_VALUE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
        });
        View soundLayout = layout.findViewById(R.id.soundLayout);
        ImageView soundImage=layout.findViewById(R.id.soundImage);
        SoundGestureDetector soundGestureDetector = new SoundGestureDetector(context,soundLayout,soundStatusView, soundImage);
        playerGestureLayout.addGestureDetector(soundGestureDetector);

        TextView progressText=layout.findViewById(R.id.progressText);

        PlayerProgressGestureDetector playerProgressGestureDetector = new PlayerProgressGestureDetector(context,playerView,progressText);
        playerGestureLayout.addGestureDetector(playerProgressGestureDetector);

        SingleTapGestureDetector singleTapGestureDetector = new SingleTapGestureDetector(playerGestureLayout);
        singleTapGestureDetector.setAbortWhenTimeout(true);
        singleTapGestureDetector.setOnSingleTapListener(v -> playerView.performClick());
        playerGestureLayout.addGestureDetector(singleTapGestureDetector);

        DoubleTapGestureDetector doubleTapGestureDetector = new DoubleTapGestureDetector(playerGestureLayout);
        doubleTapGestureDetector.setOnTapListener(v -> {
            PlayerComponent playerComponent = playerView.getPlayerComponent(PlayerComponent.COMPONENT_PLAY_PAUSE);
            if(null!=playerComponent){
                playerComponent.execute();
            }
        });
        playerGestureLayout.addGestureDetector(doubleTapGestureDetector);

//        MultipleTapGestureDetector multipleTapTimesGestureDetector = new MultipleTapGestureDetector(playerGestureLayout, ViewConfiguration.getDoubleTapTimeout(),2);
//        multipleTapTimesGestureDetector.setOnTapListener(v->System.out.println("Multiple Tap."));
//        playerGestureLayout.addDetectGestureListener(multipleTapTimesGestureDetector);

        final TextView accelerateText=layout.findViewById(R.id.accelerateText);
        AcceleratePressGestureDetector acceleratePressGestureDetector = new AcceleratePressGestureDetector(playerGestureLayout);
        acceleratePressGestureDetector.setAccelerateDuration(2000L);
        acceleratePressGestureDetector.setOnAccelerateEventChangeListener(new AcceleratePressGestureDetector.OnAccelerateEventChangeListener() {
            private int counter=0;
            @Override
            public void onAccelerateStarted() {
                accelerateText.setVisibility(View.VISIBLE);
                counter = 0;
                accelerateText.setText(String.valueOf(counter));
            }

            @Override
            public void onTick() {
                counter++;
                accelerateText.setText(String.valueOf(counter));
            }

            @Override
            public void onAccelerateStopped() {
                accelerateText.setVisibility(View.GONE);
            }
        });
        playerGestureLayout.addGestureDetector(acceleratePressGestureDetector);

        LongTapGestureDetector longTapGestureDetector = new LongTapGestureDetector(playerGestureLayout);
        longTapGestureDetector.setOnLongClickListener(v-> System.out.println("Long Tap."));
        playerGestureLayout.addGestureDetector(longTapGestureDetector);

        playerGestureLayout.attachToPlayer(playerView);
    }

    private void replaceChildWithView(View childView, View replaceView) {
        ViewParent viewParent = childView.getParent();
        if(null!=viewParent && viewParent instanceof ViewGroup){
            ViewGroup parent=(ViewGroup)viewParent;
            final int index = parent.indexOfChild(childView);
            parent.removeViewInLayout(childView);

            final ViewGroup.LayoutParams layoutParams = childView.getLayoutParams();
            if (layoutParams != null) {
                parent.addView(replaceView, index, layoutParams);
            } else {
                parent.addView(replaceView, index);
            }
        }
    }
}
