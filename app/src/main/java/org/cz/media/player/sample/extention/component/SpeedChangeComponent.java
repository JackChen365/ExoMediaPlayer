package org.cz.media.player.sample.extention.component;

import android.content.Context;
import android.view.View;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import org.cz.media.player.base.controller.MediaPlayerViewHolder;
import org.cz.media.player.base.controller.component.PlayerComponent;
import org.cz.media.player.sample.R;
import org.cz.media.player.sample.menu.ListPopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by cz
 * @date 2020/9/14 4:26 PM
 * @email bingo110@126.com
 */
public class SpeedChangeComponent extends PlayerComponent {
    private List<String> listData=new ArrayList<>();

    public SpeedChangeComponent(List<String> listData) {
        if(null!=listData){
            this.listData.addAll(listData);
        }
    }

    /**
     * Return the component's name. Here we use 'speed'.
     * @return
     */
    @Override
    public String getComponentName() {
        return "Speed";
    }


    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
        //We don't have to update the component.
    }

    /**
     * When you execute the component.It display a popupWindow.
     * And you could change the player's speed.
     */
    @Override
    public void execute() {
        MediaPlayerViewHolder viewHolder = getViewHolder();
        View playerSpeedButton=viewHolder.itemView.findViewById(R.id.playerSpeedButton);
        if(null!=playerSpeedButton){
            Context context = playerSpeedButton.getContext();
            ListPopupWindow listPopupWindow = new ListPopupWindow(context);
            listPopupWindow.setItemData(listData);
            listPopupWindow.setFocusable(true);
            listPopupWindow.setAnchorView(playerSpeedButton);
            listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
                Player player = getPlayer();
                if(null!=player){
                    float speed=1f;
                    try {
                        speed=Float.parseFloat(listData.get(position));
                    } catch (Exception e){
                    }
                    PlaybackParameters playbackParameters = new PlaybackParameters(speed);
                    player.setPlaybackParameters(playbackParameters);
                }
            });
            listPopupWindow.show();
        }
    }

    /**
     * Create the component's view.
     * The view with the id:playerSpeedButton is where you define in your layout resources.
     * @param context
     * @param viewHolder
     */
    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
        View playerSpeedButton=viewHolder.itemView.findViewById(R.id.playerSpeedButton);
        if(null!=playerSpeedButton){
            playerSpeedButton.setOnClickListener(this);
        }
    }
}
