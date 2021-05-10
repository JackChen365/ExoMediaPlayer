package org.cz.media.player.base.controller.component;

import android.content.Context;

import com.google.android.exoplayer2.Player;
import org.cz.media.player.base.controller.MediaPlayerViewHolder;

/**
 * @author Created by cz
 * @date 2020/8/13 10:55 AM
 * @email binigo110@126.com
 */
public class PlayerRewindComponent extends PlayerComponent {

    @Override
    @ComponentName
    public String getComponentName() {
        return COMPONENT_REWIND;
    }

    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
    }

    @Override
    public void execute() {
        Player player = getPlayer();
        if(null==player) return;
        PlayerControlConfiguration configuration = getPlayerControlConfiguration();
        int rewindIncrement = configuration.getRewindIncrement();
        if (player.isCurrentWindowSeekable() && rewindIncrement > 0) {
            seekToOffset(-rewindIncrement);
        }
    }

    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
        if(null!=viewHolder.rewindButton){
            viewHolder.rewindButton.setOnClickListener(this);
        }
    }

}
