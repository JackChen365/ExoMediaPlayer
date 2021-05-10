package org.cz.media.player.base.controller.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.util.RepeatModeUtil;
import org.cz.media.player.base.R;
import org.cz.media.player.base.controller.MediaPlayerViewHolder;

/**
 * @author Created by cz
 * @date 2020/8/13 10:55 AM
 * @email binigo110@126.com
 */
public class PlayerRepeatComponent extends PlayerComponent {
    private Drawable repeatOffButtonDrawable;
    private Drawable repeatOneButtonDrawable;
    private Drawable repeatAllButtonDrawable;
    private String repeatOffButtonContentDescription;
    private String repeatOneButtonContentDescription;
    private String repeatAllButtonContentDescription;

    @Override
    @ComponentName
    public String getComponentName() {
        return COMPONENT_REPEAT;
    }

    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
        updateRepeatModeButton();
    }

    @Override
    public void execute() {
        Player player = getPlayer();
        if(null!=player){
            int repeatMode = player.getRepeatMode();
            int nextRepeatMode = getNextRepeatMode(repeatMode);
            dispatchSetRepeatMode(nextRepeatMode);
        }
    }

    @Player.RepeatMode
    private int getNextRepeatMode(@Player.RepeatMode int currentMode) {
        return (currentMode + 1) % 3;
    }

    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
//        <item name="playerRepeatButtonOff">@style/MediaButton.RepeatOff</item>
//        <item name="playerRepeatButtonOne">@style/MediaButton.RepeatOne</item>
//        <item name="playerRepeatButtonAll">@style/MediaButton.RepeatAll</item>
        TypedArray a = context.obtainStyledAttributes(R.styleable.PlayerRepeatButton);
        int repeatButtonOffStyle = a.getResourceId(R.styleable.PlayerRepeatButton_playerRepeatButtonOff, 0);
        if(0 != repeatButtonOffStyle){
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, repeatButtonOffStyle);
            TypedArray a1 = contextThemeWrapper.obtainStyledAttributes(new int[]{android.R.attr.src, android.R.attr.contentDescription});
            repeatOffButtonDrawable=a1.getDrawable(0);
            repeatOffButtonContentDescription=a1.getString(1);
            a1.recycle();
        }
        int repeatButtonOneStyle = a.getResourceId(R.styleable.PlayerRepeatButton_playerRepeatButtonOne, 0);
        if(0 != repeatButtonOffStyle){
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, repeatButtonOneStyle);
            TypedArray a1 = contextThemeWrapper.obtainStyledAttributes(new int[]{android.R.attr.src, android.R.attr.contentDescription});
            repeatOneButtonDrawable=a1.getDrawable(0);
            repeatOneButtonContentDescription=a1.getString(1);
            a1.recycle();
        }
        int repeatButtonAllStyle = a.getResourceId(R.styleable.PlayerRepeatButton_playerRepeatButtonAll, 0);
        if(0 != repeatButtonOffStyle){
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, repeatButtonAllStyle);
            TypedArray a1 = contextThemeWrapper.obtainStyledAttributes(new int[]{android.R.attr.src, android.R.attr.contentDescription});
            repeatAllButtonDrawable=a1.getDrawable(0);
            repeatAllButtonContentDescription=a1.getString(1);
            a1.recycle();
        }
        a.recycle();
        if(null!=viewHolder.repeatToggleButton){
            viewHolder.repeatToggleButton.setOnClickListener(this);
        }
    }

    /**
     * Sets which repeat toggle modes are enabled.
     *
     * @param repeatToggleMode A set of {@link RepeatModeUtil.RepeatToggleModes}.
     */
    public void setRepeatToggleMode(@RepeatModeUtil.RepeatToggleModes int repeatToggleMode) {
        Player player = getPlayer();
        if (player != null) {
            @Player.RepeatMode int currentMode = player.getRepeatMode();
            if (repeatToggleMode == RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE
                    && currentMode != Player.REPEAT_MODE_OFF) {
                dispatchSetRepeatMode(Player.REPEAT_MODE_OFF);
            } else if (repeatToggleMode == RepeatModeUtil.REPEAT_TOGGLE_MODE_ONE
                    && currentMode == Player.REPEAT_MODE_ALL) {
                dispatchSetRepeatMode(Player.REPEAT_MODE_ONE);
            } else if (repeatToggleMode == RepeatModeUtil.REPEAT_TOGGLE_MODE_ALL
                    && currentMode == Player.REPEAT_MODE_ONE) {
                dispatchSetRepeatMode(Player.REPEAT_MODE_ALL);
            }
        }
        MediaPlayerViewHolder viewHolder = getViewHolder();
        update(viewHolder);
    }

    public void updateRepeatModeButton() {
        MediaPlayerViewHolder viewHolder = getViewHolder();
        if (!isVisible() || !isAttachedToWindow() || null==viewHolder.repeatToggleButton) {
            return;
        }

        PlayerControlConfiguration playerControlConfiguration = getPlayerControlConfiguration();
        int repeatToggleModes = playerControlConfiguration.getRepeatToggleMode();
        if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE) {
            viewHolder.repeatToggleButton.setVisibility(View.GONE);
            return;
        }
        Player player = getPlayer();
        if (player == null) {
            setButtonEnabled(false, viewHolder.repeatToggleButton);
            viewHolder.repeatToggleButton.setImageDrawable(repeatOffButtonDrawable);
            viewHolder.repeatToggleButton.setContentDescription(repeatOffButtonContentDescription);
            return;
        }

        setButtonEnabled(true, viewHolder.repeatToggleButton);
        switch (player.getRepeatMode()) {
            case Player.REPEAT_MODE_OFF:
                viewHolder.repeatToggleButton.setImageDrawable(repeatOffButtonDrawable);
                viewHolder.repeatToggleButton.setContentDescription(repeatOffButtonContentDescription);
                break;
            case Player.REPEAT_MODE_ONE:
                viewHolder.repeatToggleButton.setImageDrawable(repeatOneButtonDrawable);
                viewHolder.repeatToggleButton.setContentDescription(repeatOneButtonContentDescription);
                break;
            case Player.REPEAT_MODE_ALL:
                viewHolder.repeatToggleButton.setImageDrawable(repeatAllButtonDrawable);
                viewHolder.repeatToggleButton.setContentDescription(repeatAllButtonContentDescription);
                break;
            default:
                // Never happens.
        }
        viewHolder.repeatToggleButton.setVisibility(View.VISIBLE);
    }


}
