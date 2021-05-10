package org.cz.media.player.base.controller;

import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.cz.media.player.base.R;
import org.cz.media.player.base.timebar.TimeBar;

/**
 * @author Created by cz
 * @date 2020/9/15 11:26 AM
 * @email bingo110@126.com
 *
 * Holding all the control layout component view.
 * If you have your own component implementation. Use the {{@link #itemView}} to find your view.
 *
 * @see MediaPlayerControlLayout
 * @see MediaPlayerComponentManager
 *
 */
public class MediaPlayerViewHolder{
    @Nullable public final View itemView;
    @Nullable public final View previousButton;
    @Nullable public final View nextButton;
    @Nullable public final View playButton;
    @Nullable public final View pauseButton;
    @Nullable public final View fastForwardButton;
    @Nullable public final View rewindButton;
    @Nullable public final ImageView repeatToggleButton;
    @Nullable public final ImageView shuffleButton;
    @Nullable public final TextView durationView;
    @Nullable public final TextView positionView;
    @Nullable public final TimeBar timeBar;

    public MediaPlayerViewHolder(View itemView) {
        this.itemView=itemView;
        this.durationView = itemView.findViewById(R.id.playerDurationText);
        this.positionView = itemView.findViewById(R.id.playerPositionText);
        this.playButton = itemView.findViewById(R.id.playerPlayButton);
        this.pauseButton = itemView.findViewById(R.id.playerPauseButton);
        this.previousButton = itemView.findViewById(R.id.playerPrevButton);
        this.nextButton = itemView.findViewById(R.id.playerNextButton);
        this.rewindButton = itemView.findViewById(R.id.playerRewindButton);
        this.fastForwardButton = itemView.findViewById(R.id.playerForwardButton);
        this.repeatToggleButton = itemView.findViewById(R.id.playerRepeatToggleButton);
        this.shuffleButton = itemView.findViewById(R.id.playerShuffleButton);
        this.timeBar = itemView.findViewById(R.id.playerTimeBar);
        if(null==timeBar){
            throw new NullPointerException("The time bar view should not be null or it will cause critical problems!");
        } else if(!(timeBar instanceof TimeBar)){
            throw new IllegalArgumentException("The timeBar view should implement from The interface: TimeBar!");
        }
    }
}