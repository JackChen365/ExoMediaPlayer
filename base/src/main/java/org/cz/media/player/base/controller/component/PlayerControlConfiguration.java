package org.cz.media.player.base.controller.component;

/**
 * @author Created by cz
 * @date 2020/8/13 2:34 PM
 * @email binigo110@126.com
 * The basic configuration for {@link PlayerComponent}
 * It provider configuration information for all the components.
 *
 *
 */
public interface PlayerControlConfiguration {
    /**
     * The rewind increment. This is for {@link PlayerRewindComponent}.
     * @return
     */
    int getRewindIncrement();

    /**
     * The fast forward increment. This is for {@link PlayerRewindComponent}.
     * @return
     */
    int getForwardIncrement();

    /**
     * The repeat toggle mode.
     * @see com.google.android.exoplayer2.Player#REPEAT_MODE_ALL
     * @see com.google.android.exoplayer2.Player#REPEAT_MODE_ONE
     * @see com.google.android.exoplayer2.Player#REPEAT_MODE_OFF
     * @return
     */
    int getRepeatToggleMode();

    /**
     * Play the list in shuffle mode.
     * @return
     */
    boolean isShowShuffleButton();

    /**
     * Return the button enabled alpha. If the enable the button. How it looks like.
     * @return
     */
    float getButtonEnabledAlpha();

    /**
     * Return the button disable alpha. If the enable the button. How it looks like.
     * @return
     */
    float getButtonDisabledAlpha();

    /**
     * The timeBar minimum update time.
     * Because for a MediaPlayer the TimeBar doesn't have to update frequently.
     * @return
     */
    int getTimeBarMinUpdateInterval();

}
