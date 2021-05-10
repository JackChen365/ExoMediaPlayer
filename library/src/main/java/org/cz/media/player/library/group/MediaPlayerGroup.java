package org.cz.media.player.library.group;

import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.MainThread;

import com.google.android.exoplayer2.Player;

import org.cz.media.player.base.MediaPlayerView;
import org.cz.media.player.library.AppCompatMediaPlayerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by cz
 * @date 2020/9/7 6:17 PM
 * @email binigo110@126.com
 */
public class MediaPlayerGroup {
    /**
     * The current shown layout id.
     */
    private int currentId= View.NO_ID;
    private List<View> mediaPlayerList =null;
    private SparseBooleanArray initialArray=null;
    private OnMediaPlayerViewInitialListener listener;

    public MediaPlayerGroup() {
    }

    /**
     * Attach to the host view.
     * @param host
     */
    public void attachToHost(ViewGroup host){
        host.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                //Release all the views.
                if(null!=mediaPlayerList){
                    mediaPlayerList.clear();
                    mediaPlayerList=null;
                }
            }
        });
        recursivelyAddPlayerView(host);
    }

    @MainThread
    public void addPlayerView(View playerView){
        if(null==mediaPlayerList){
            mediaPlayerList=new ArrayList<>();
        }
        playerView.setVisibility(View.GONE);
        mediaPlayerList.add(playerView);
    }

    private void recursivelyAddPlayerView(View view){
        if(view instanceof MediaPlayerView||view instanceof org.cz.media.player.library.MediaPlayerViewStub){
            addPlayerView(view);
        } else if(view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for(int i=0;i<childCount;i++){
                View childView = viewGroup.getChildAt(i);
                recursivelyAddPlayerView(childView);
            }
        }
    }

    @MainThread
    public void show(@IdRes int id){
        if(null!=mediaPlayerList){
            MediaPlayerView oldPlayerView=null;
            View newView=null;
            for(View view:mediaPlayerList){
                int viewId = view.getId();
                if(currentId==viewId){
                    oldPlayerView= (MediaPlayerView) view;
                } else if(id==viewId){
                    newView=view;
                }
            }
            Player player=null;
            if(null!=oldPlayerView){
                oldPlayerView.setVisibility(View.GONE);
                player = oldPlayerView.getPlayer();
            }
            MediaPlayerView newPlayerView=null;
            if(null!=newView){
                if(newView instanceof AppCompatMediaPlayerView){
                    newPlayerView = (AppCompatMediaPlayerView)newView;
                } else if(newView instanceof org.cz.media.player.library.MediaPlayerViewStub){
                    org.cz.media.player.library.MediaPlayerViewStub viewStub = (org.cz.media.player.library.MediaPlayerViewStub) newView;
                    View view = viewStub.inflate();
                    if(!(view instanceof AppCompatMediaPlayerView)){
                        throw new IllegalArgumentException("Media player view only allow you inflate the AppCompatMediaPlayerView. Player make sure your resource file has the right widget.");
                    }
                    newPlayerView = (AppCompatMediaPlayerView)view;
                    int index = mediaPlayerList.indexOf(newView);
                    mediaPlayerList.set(index,newPlayerView);
                    mediaPlayerList.remove(newView);
                    id=newPlayerView.getId();
                }
                newPlayerView.setVisibility(View.VISIBLE);
                //We call back the event only when it is the first time shown;
                if(null!=listener){
                    int newPlayerViewId = newPlayerView.getId();
                    if(null==initialArray){
                        initialArray=new SparseBooleanArray();
                    }
                    boolean isFirstVisible = initialArray.get(newPlayerViewId,false);
                    if(!isFirstVisible){
                        initialArray.put(newPlayerViewId,true);
                        listener.onPlayerViewInitial(newPlayerView);
                    }
                }
            }
            if(null!=player){
                MediaPlayerView.switchTargetView(player,oldPlayerView,newPlayerView);
            }
            currentId=id;
        }
    }

    public void next(){
        if(null!=mediaPlayerList){
            MediaPlayerView currentPlayerView = getCurrentMediaPlayer();
            if(null!=currentPlayerView){
                int index = mediaPlayerList.indexOf(currentPlayerView);
                if(index + 1 < mediaPlayerList.size()){
                    View mediaPlayerView = mediaPlayerList.get(index + 1);
                    if(null!=mediaPlayerView){
                        show(mediaPlayerView.getId());
                    }
                } else {
                    View mediaPlayerView = mediaPlayerList.get(0);
                    if(null!=mediaPlayerView){
                        show(mediaPlayerView.getId());
                    }
                }
            }
        }
    }

    public MediaPlayerView getCurrentMediaPlayer() {
        MediaPlayerView currentPlayerView=null;
        for(View view:mediaPlayerList){
            int viewId = view.getId();
            if(currentId==viewId){
                currentPlayerView= (MediaPlayerView) view;
                break;
            }
        }
        return currentPlayerView;
    }

    public void setOnMediaPlayerViewInitialListener(OnMediaPlayerViewInitialListener listener){
        this.listener=listener;
    }

    public interface OnMediaPlayerViewInitialListener{
        void onPlayerViewInitial(MediaPlayerView playerView);
    }
}
