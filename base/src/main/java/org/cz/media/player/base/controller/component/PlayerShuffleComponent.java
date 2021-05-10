package org.cz.media.player.base.controller.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.google.android.exoplayer2.Player;
import org.cz.media.player.base.R;
import org.cz.media.player.base.controller.MediaPlayerViewHolder;

/**
 * @author Created by cz
 * @date 2020/8/13 10:55 AM
 * @email binigo110@126.com
 */
public class PlayerShuffleComponent extends PlayerComponent {
    private Drawable shuffleOnButtonDrawable;
    private Drawable shuffleOffButtonDrawable;
    private String shuffleOnContentDescription;
    private String shuffleOffContentDescription;

    @Override
    @ComponentName
    public String getComponentName() {
        return COMPONENT_SHUFFLE;
    }

    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
//        <declare-styleable name="PlayerShuffleButton">
//            <attr name="playerShuffleButtonOn"/>
//            <attr name="playerShuffleButtonOff"/>
//        </declare-styleable>
        TypedArray a = context.obtainStyledAttributes(R.styleable.PlayerShuffleButton);
        int playerShuffleButtonOnStyle = a.getResourceId(R.styleable.PlayerShuffleButton_playerShuffleButtonOn, 0);
        if(0 != playerShuffleButtonOnStyle){
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, playerShuffleButtonOnStyle);
            TypedArray a1 = contextThemeWrapper.obtainStyledAttributes(new int[]{android.R.attr.src, android.R.attr.contentDescription});
            shuffleOnButtonDrawable=a1.getDrawable(0);
            shuffleOnContentDescription=a1.getString(1);
            a1.recycle();
        }
        int playerShuffleButtonOffStyle = a.getResourceId(R.styleable.PlayerShuffleButton_playerShuffleButtonOff, 0);
        if(0 != playerShuffleButtonOffStyle){
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, playerShuffleButtonOffStyle);
            TypedArray a1 = contextThemeWrapper.obtainStyledAttributes(new int[]{android.R.attr.src, android.R.attr.contentDescription});
            shuffleOffButtonDrawable=a1.getDrawable(0);
            shuffleOffContentDescription=a1.getString(1);
            a1.recycle();
        }
        if(null!=viewHolder.shuffleButton){
            viewHolder.shuffleButton.setOnClickListener(this);
        }
    }

    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
        Player player = getPlayer();
        if (!isVisible() || !isAttachedToWindow() || viewHolder.shuffleButton == null) {
            return;
        }
        PlayerControlConfiguration configuration = getPlayerControlConfiguration();
        boolean showShuffleButton = configuration.isShowShuffleButton();
        if (!showShuffleButton) {
            viewHolder.shuffleButton.setVisibility(View.GONE);
        } else if (player == null) {
            setButtonEnabled(false, viewHolder.shuffleButton);
            viewHolder.shuffleButton.setImageDrawable(shuffleOffButtonDrawable);
            viewHolder.shuffleButton.setContentDescription(shuffleOffContentDescription);
        } else {
            setButtonEnabled(true, viewHolder.shuffleButton);
            viewHolder.shuffleButton.setImageDrawable(
                    player.getShuffleModeEnabled() ? shuffleOnButtonDrawable : shuffleOffButtonDrawable);
            viewHolder.shuffleButton.setContentDescription(
                    player.getShuffleModeEnabled()
                            ? shuffleOnContentDescription
                            : shuffleOffContentDescription);
        }
    }

    @Override
    public void execute() {
        Player player = getPlayer();
        if(null!=player){
            boolean shuffleModeEnabled = player.getShuffleModeEnabled();
            dispatchSetShuffleModeEnabled(!shuffleModeEnabled);
        }
    }

}
