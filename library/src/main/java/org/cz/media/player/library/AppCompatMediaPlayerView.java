package org.cz.media.player.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import org.cz.media.player.base.MediaPlayerView;
import org.cz.media.player.library.error.PlayerErrorMessageProvider;
import org.cz.media.player.library.status.AbsPlayerViewWrapper;
import org.cz.media.player.library.status.PlayerViewWrapper;
import org.cz.media.player.library.status.transition.ContentFrameTransition;
import org.cz.media.player.library.status.transition.DefaultFrameTransition;
import org.cz.media.player.library.status.transition.FrameTransition;
import org.cz.media.player.library.status.trigger.FrameTrigger;
import org.cz.media.player.library.status.trigger.PlayerErrorTrigger;
import org.cz.media.player.library.status.trigger.PlayerEventTrigger;

/**
 * @author Created by cz
 * @date 2020/9/9 11:16 AM
 * @email binigo110@126.com
 */
public class AppCompatMediaPlayerView extends MediaPlayerView {
    @Nullable
    private ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider;
    /**
     * The player view wrapper. We use view wrapper to support functions like:
     * 1. Display the load error page.
     * 2. Display the loading page.
     * ...
     */
    private AbsPlayerViewWrapper playerViewWrapper;

    public AppCompatMediaPlayerView(@NonNull Context context) {
        this(context,null);
    }

    public AppCompatMediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setErrorMessageProvider(new PlayerErrorMessageProvider(context));
    }

    public AppCompatMediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setErrorMessageProvider(new PlayerErrorMessageProvider(context));
    }

    /**
     * Sets the optional {@link ErrorMessageProvider}.
     *
     * @param errorMessageProvider The error message provider.
     */
    public void setErrorMessageProvider(
            @Nullable ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider) {
        if (this.errorMessageProvider != errorMessageProvider) {
            this.errorMessageProvider = errorMessageProvider;
            updateErrorMessage();
        }
    }

    private void updateErrorMessage() {
        if(null!=playerViewWrapper){
            View errorMessageView = playerViewWrapper.findFrameView(PlayerViewWrapper.FRAME_NETWORK_ERROR, R.id.errorText);
            if(null!=errorMessageView){
                Player player = getPlayer();
                @Nullable ExoPlaybackException error = null != player ? player.getPlaybackError() : null;
                if (error != null && errorMessageProvider != null) {
                    CharSequence errorMessage = errorMessageProvider.getErrorMessage(error).second;
                    TextView textView = (TextView) errorMessageView;
                    textView.setText(errorMessage);
                }
            }
        }
    }

    public void clearVideoSurface(){
        View videoSurfaceView = getVideoSurfaceView();
        if(null!=videoSurfaceView){
            if(videoSurfaceView instanceof SurfaceView){
                SurfaceView surfaceView = (SurfaceView) videoSurfaceView;
                SurfaceHolder holder = surfaceView.getHolder();
                holder.setFormat(PixelFormat.TRANSPARENT);
                holder.setFormat(PixelFormat.OPAQUE);
            } else if(videoSurfaceView instanceof TextureView){
                TextureView textureView = (TextureView) videoSurfaceView;
                Canvas canvas = null;
                try {
                    canvas = textureView.lockCanvas();
                    if(null!=canvas){
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    }
                } finally {
                    if(null!=canvas){
                        textureView.unlockCanvasAndPost(canvas);
                    }
                }
            } else {
                videoSurfaceView.invalidate();
            }
        }
    }

    /**
     * Register a new player frame.
     * @param id
     * @param layout
     */
    public void registerPlayerFrame(int id,@LayoutRes int layout){
        AbsPlayerViewWrapper.registerFrame(id,layout);
    }

    /**
     * Setting up a new player frame wrapper.
     * @param playerViewWrapper
     */
    public void setPlayerViewWrapper(AbsPlayerViewWrapper playerViewWrapper){
        this.playerViewWrapper=playerViewWrapper;
        ViewGroup hostView = playerViewWrapper.getHostView();
        View videoSurfaceView = getVideoSurfaceView();
        if(hostView!=videoSurfaceView){
            throw new RuntimeException("The PlayerFrameWrapper not wrapped the video surface view. Please check your code!");
        }
        Context context = getContext();
        playerViewWrapper.setFrameTransition(new ContentFrameTransition());
        playerViewWrapper.addFrameTrigger(new PlayerEventTrigger(this));
        playerViewWrapper.addFrameTrigger(new PlayerErrorTrigger(context,this));
    }

    /**
     * Changes the frame transition. This is for the frame when you are changing the frame from one to another.
     * It changes with animation.
     * @see DefaultFrameTransition This is the default layout transition.
     * @param frameTransition
     */
    public void setFrameTransition(FrameTransition frameTransition) {
        this.playerViewWrapper.setFrameTransition(frameTransition);
    }

    /**
     * Add a frame trigger.
     * @see FrameTrigger
     * @param trigger
     */
    public void addFrameTrigger(@NonNull FrameTrigger trigger){
        playerViewWrapper.addFrameTrigger(trigger);
    }

    /**
     * Remove a frame trigger from the list.
     * @param trigger
     */
    public void removeFrameTrigger(FrameTrigger trigger){
        playerViewWrapper.removeFrameTrigger(trigger);
    }

    @Override
    public void setPlayer(@Nullable Player player) {
        //Make it initial earlier than the others.
        if(null!=player){
            player.addListener(new Player.EventListener() {
                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    updateErrorMessage();
                }
            });
        }
        super.setPlayer(player);
        //Always make the container display the content after we setup a new player.
        if(null != playerViewWrapper){
            playerViewWrapper.setFrame(PlayerViewWrapper.FRAME_CONTAINER);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean dispatchKeyEvent = super.dispatchKeyEvent(event);
        if(!dispatchKeyEvent){
            FrameLayout overlayFrameLayout = getOverlayFrameLayout();
            overlayFrameLayout.dispatchKeyEvent(event);
        }
        return dispatchKeyEvent;
    }

    @Override
    public void onGenerateSurfaceView(View view) {
        super.onGenerateSurfaceView(view);
        if(null!=view){
            Context context = getContext();
            playerViewWrapper=new PlayerViewWrapper(this,view);
            playerViewWrapper.setFrameTransition(new ContentFrameTransition());
            playerViewWrapper.addFrameTrigger(new PlayerEventTrigger(this));
            playerViewWrapper.addFrameTrigger(new PlayerErrorTrigger(context,this));
        }
    }
}
