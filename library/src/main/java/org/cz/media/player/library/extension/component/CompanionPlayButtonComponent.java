package org.cz.media.player.library.extension.component;

import android.content.Context;
import android.view.View;

import org.cz.media.player.base.controller.MediaPlayerViewHolder;
import org.cz.media.player.base.controller.component.PlayerPlayPauseComponent;
import org.cz.media.player.library.R;

/**
 * @author Created by cz
 * @date 2020/10/12 2:52 PM
 * @email bingo110@126.com
 */
public class CompanionPlayButtonComponent extends PlayerPlayPauseComponent {

    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
        super.update(viewHolder);
        boolean shouldShowPauseButton = shouldShowPauseButton();
        View playButton = viewHolder.itemView.findViewById(R.id.playerCenterPlayButton);
        if (playButton != null) {
            playButton.setVisibility(shouldShowPauseButton ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
        super.onComponentCreated(context,viewHolder);
        View playButton = viewHolder.itemView.findViewById(R.id.playerCenterPlayButton);
        if(null!=playButton){
            playButton.setOnClickListener(this);
        }
    }

}
