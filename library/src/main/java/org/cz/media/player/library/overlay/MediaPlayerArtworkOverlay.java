package org.cz.media.player.library.overlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import org.cz.media.player.library.R;
import org.cz.media.player.base.MediaPlayerView;

/**
 * @author Created by cz
 * @date 2020/9/17 11:39 AM
 * @email binigo110@126.com
 */
public abstract class MediaPlayerArtworkOverlay extends MediaPlayerOverlay{

    @Override
    public View onCreateOverlay(Context context, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.media_player_artwork_overlay_layout,parent,false);
    }

    @Override
    public void onAttachToPlayer(@NonNull MediaPlayerView playerView,@NonNull View contentView) {
        super.onAttachToPlayer(playerView,contentView);
        ImageView imageView = contentView.findViewById(R.id.playerArtworkView);
        onLoadImage(imageView);

    }

    public abstract void onLoadImage(@NonNull ImageView imageView);

}
