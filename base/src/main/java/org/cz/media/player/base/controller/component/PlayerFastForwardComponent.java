package org.cz.media.player.base.controller.component;

import android.content.Context;

import com.google.android.exoplayer2.Player;
import org.cz.media.player.base.controller.MediaPlayerViewHolder;

/**
 * @author Created by cz
 * @date 2020/8/13 10:55 AM
 * @email binigo110@126.com
 */
public class PlayerFastForwardComponent extends PlayerComponent {
    private static final String TAG="PlayerFastForwardComponent";
    @Override
    @ComponentName
    public String getComponentName() {
        return COMPONENT_FAST_FORWARD;
    }

    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
        Player player = getPlayer();
        if(null==player) return;
        long duration = player.getDuration();
        long currentPosition = player.getCurrentPosition();
        setButtonEnabled(currentPosition < duration,viewHolder.fastForwardButton);
    }

    @Override
    public void execute() {
        Player player = getPlayer();
        if(null==player) return;
        PlayerControlConfiguration configuration = getPlayerControlConfiguration();
        int forwardIncrement = configuration.getForwardIncrement();
        if (player.isCurrentWindowSeekable() && forwardIncrement > 0) {
            seekToOffset(forwardIncrement);
        }
    }

    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
        if(null!=viewHolder.fastForwardButton){
            viewHolder.fastForwardButton.setOnClickListener(this);
        }
    }

}
