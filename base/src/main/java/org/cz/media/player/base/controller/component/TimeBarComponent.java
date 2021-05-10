package org.cz.media.player.base.controller.component;

import org.cz.media.player.base.controller.MediaPlayerViewHolder;

/**
 * @author Created by cz
 * @date 2020/10/21 10:35 AM
 * @email bingo110@126.com
 */
public interface TimeBarComponent {
    /**
     * When the player receives some events. Some components need to change its appearance.
     * Like the the Player's play status changed from start to pause. The PlayPauseComponent should update.
     * @param viewHolder The component view combination.
     */
    void update(MediaPlayerViewHolder viewHolder);

    /**
     * If the component changed whatever the user clicks the button or You call it programminglly.
     * It will trigger this function, Such as you click the play button. It should play the playback.
     */
    void execute();

    void updateProgress();

    long getPosition();

    long getBufferedPosition();
}
