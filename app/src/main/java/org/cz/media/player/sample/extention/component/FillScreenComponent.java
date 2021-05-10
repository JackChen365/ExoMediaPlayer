package org.cz.media.player.sample.extention.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.View;

import androidx.annotation.IdRes;

import org.cz.media.player.base.controller.MediaPlayerControlLayout;
import org.cz.media.player.base.controller.MediaPlayerViewHolder;
import org.cz.media.player.base.controller.component.PlayerComponent;
import org.cz.media.player.sample.R;

/**
 * @author Created by cz
 * @date 2020/9/15 11:26 AM
 * @email bingo110@126.com
 */
public class FillScreenComponent extends PlayerComponent {
    private Activity activity;
    private int buttonId;

    public FillScreenComponent(Activity activity, @IdRes int buttonId) {
        this.activity = activity;
        this.buttonId=buttonId;
    }

    @Override
    public String getComponentName() {
        return "FillScreen";
    }

    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
        View view=viewHolder.itemView.findViewById(R.id.playerFillScreenButton);
        if(null!=view){
            int requestedOrientation = activity.getRequestedOrientation();
            if(requestedOrientation== ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED||
                    requestedOrientation== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                view.setBackgroundResource(R.mipmap.ic_fullscreen_enter);
                view.setContentDescription(activity.getString(R.string.fill_screen));
            } else if(requestedOrientation== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                view.setBackgroundResource(R.mipmap.ic_fullscreen_exit);
                view.setContentDescription(activity.getString(R.string.normal_screen));
            }
        }
    }

    @Override
    public void execute() {
        int requestedOrientation = activity.getRequestedOrientation();
        if(requestedOrientation== ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED||
                requestedOrientation== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if(requestedOrientation== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        MediaPlayerViewHolder viewHolder = getViewHolder();
        View view=viewHolder.itemView.findViewById(buttonId);
        if(null!=view){
            update(viewHolder);
        }
    }

    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
        View playerFillScreenButton=viewHolder.itemView.findViewById(buttonId);
        if(null!=playerFillScreenButton){
            playerFillScreenButton.setOnClickListener(this);
        }
    }

    @Override
    public void onDetachFromPlayerControlLayout(MediaPlayerControlLayout controlLayout) {
        super.onDetachFromPlayerControlLayout(controlLayout);
        activity=null;
    }
}
