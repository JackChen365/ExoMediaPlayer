package org.cz.media.player.base.controller.component;

import android.content.Context;
import android.view.View;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import org.cz.media.player.base.controller.MediaPlayerViewHolder;

/**
 * @author Created by cz
 * @date 2020/8/13 10:55 AM
 * @email binigo110@126.com
 */
public class PlayerPlayPauseComponent extends PlayerComponent {

    @Override
    public String getComponentName() {
        return COMPONENT_PLAY_PAUSE;
    }

    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
        if (!isVisible() || !isAttachedToWindow()) {
            return;
        }
        boolean requestPlayPauseFocus = false;
        boolean shouldShowPauseButton = shouldShowPauseButton();
        View playButton = viewHolder.playButton;
        if (playButton != null) {
            requestPlayPauseFocus |= shouldShowPauseButton && playButton.isFocused();
            playButton.setVisibility(shouldShowPauseButton ? View.GONE : View.VISIBLE);
        }
        View pauseButton= viewHolder.pauseButton;
        if (pauseButton != null) {
            requestPlayPauseFocus |= !shouldShowPauseButton && pauseButton.isFocused();
            pauseButton.setVisibility(shouldShowPauseButton ? View.VISIBLE : View.GONE);
        }
        if (requestPlayPauseFocus) {
            requestPlayPauseFocus();
        }
    }

    @Override
    public void execute() {
        Player player = getPlayer();
        if(null!=player){
            MediaPlayerViewHolder viewHolder = getViewHolder();
            boolean shouldShowPauseButton = shouldShowPauseButton();
            if (!shouldShowPauseButton && viewHolder.playButton != null) {
                play(player);
            } else if (shouldShowPauseButton && viewHolder.pauseButton != null) {
                dispatchSetPlayWhenReady(false);
            }
        }
    }

    private void play(Player player) {
        if (player.getPlaybackState() == Player.STATE_IDLE) {
            PlaybackPreparer playbackPreparer = getPlaybackPreparer();
            if (playbackPreparer != null) {
                playbackPreparer.preparePlayback();
            }
        } else if (player.getPlaybackState() == Player.STATE_ENDED) {
            dispatchSeekTo(player.getCurrentWindowIndex(), C.TIME_UNSET);
        }
        dispatchSetPlayWhenReady(true);
    }

    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
        if(null!=viewHolder.playButton){
            viewHolder.playButton.setOnClickListener(this);
        }
        if(null!=viewHolder.pauseButton){
            viewHolder.pauseButton.setOnClickListener(this);
        }
    }

    public boolean shouldShowPauseButton() {
        Player player = getPlayer();
        return player != null
                && player.getPlaybackState() != Player.STATE_ENDED
                && player.getPlaybackState() != Player.STATE_IDLE
                && player.getPlayWhenReady();
    }

    public void requestPlayPauseFocus() {
        MediaPlayerViewHolder viewHolder = getViewHolder();
        boolean shouldShowPauseButton = shouldShowPauseButton();
        if (!shouldShowPauseButton && viewHolder.playButton != null) {
            viewHolder.playButton.requestFocus();
        } else if (shouldShowPauseButton && viewHolder.pauseButton != null) {
            viewHolder.pauseButton.requestFocus();
        }
    }
}
