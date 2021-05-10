package org.cz.media.player.sample.extention.overlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.Player;
import org.cz.media.player.base.MediaPlayerView;
import org.cz.media.player.library.overlay.MediaPlayerOverlay;
import org.cz.media.player.sample.R;

/**
 * @author Created by cz
 * @date 2020/9/17 11:39 AM
 * @email binigo110@126.com
 */
public class MediaPlayerTrafficWarningOverlay extends MediaPlayerOverlay implements MediaPlayerView.OnPlayerChangeListener {

    private String buttonText;

    @Override
    public View onCreateOverlay(Context context, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.media_player_traffic_warning_overlay_layout,parent,false);
    }

    @Override
    public void onAttachToPlayer(MediaPlayerView playerView,View contentView) {
        super.onAttachToPlayer(playerView,contentView);
        playerView.addOnPlayerChangeListener(this);
    }

    @Override
    public void onPlayerChanged(Player newPlayer, Player oldPlayer) {
    }
}
