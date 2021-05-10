package org.cz.media.player.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.IntDef;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.AbsSavedState;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoDecoderGLSurfaceView;
import com.google.android.exoplayer2.video.VideoListener;
import org.cz.media.player.base.controller.MediaPlayerControlLayout;
import org.cz.media.player.base.controller.component.PlayerComponent;
import org.cz.media.player.base.layout.AnimationControlLayout;
import org.cz.media.player.base.overlay.FrameOverlayLayout;
import org.cz.media.player.base.overlay.Touchable;
import org.cz.media.player.base.subtitle.SubtitleView;

import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Created by cz
 * @date 2020/8/4 9:54 AM
 * @email binigo110@126.com
 */
public class MediaPlayerView extends FrameLayout {
    private static final String TAG="MediaPlayerView";

    /**
     * Determines when the buffering view is shown. One of {@link #SHOW_BUFFERING_NEVER}, {@link
     * #SHOW_BUFFERING_WHEN_PLAYING} or {@link #SHOW_BUFFERING_ALWAYS}.
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_BUFFERING_NEVER, SHOW_BUFFERING_WHEN_PLAYING, SHOW_BUFFERING_ALWAYS})
    public @interface ShowBuffering {
    }
    /** The buffering view is never shown. */
    public static final int SHOW_BUFFERING_NEVER = 0;
    /**
     * The buffering view is shown when the player is in the {@link Player#STATE_BUFFERING buffering}
     * state and {@link Player#getPlayWhenReady() playWhenReady} is {@code true}.
     */
    public static final int SHOW_BUFFERING_WHEN_PLAYING = 1;
    /**
     * The buffering view is always shown when the player is in the {@link Player#STATE_BUFFERING
     * buffering} state.
     */
    public static final int SHOW_BUFFERING_ALWAYS = 2;

    private static final int SURFACE_TYPE_NONE = 0;
    private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;

    private static final int TOUCH_MODE_AUTO_HIDE=0;
    private static final int TOUCH_MODE_CLICK_TOGGLE=1;
    private static final int TOUCH_MODE_NONE=2;

    private final ComponentListener componentListener=new ComponentListener();
    private List<OnPlayerChangeListener> playerChangeListenerList;
    private SurfaceViewCreateFactory surfaceViewCreateFactory;
    @Nullable private org.cz.media.player.base.AspectRatioFrameLayout contentFrame;
    @Nullable private View surfaceView;
    @Nullable private SubtitleView subtitleView;
    @Nullable private MediaPlayerControlLayout controller;
    @Nullable private FrameOverlayLayout overlayFrameLayout;

    @Nullable private Player player;
    private boolean useController;
    private boolean controllerAutoShow;
    private int controllerTouchMode;
    private int textureViewRotation;
    private int showBuffering=SHOW_BUFFERING_ALWAYS;
    private int resizeMode = org.cz.media.player.base.AspectRatioFrameLayout.RESIZE_MODE_FIT;

    /**
     * The intercept controller counter. If something wants to intercept the visible event of the controller layout.
     * call {@link #startInterceptControllerVisibleEvent()} after all the work. call {@link #stopInterceptControllerVisibleEvent()}
     */
    private int interceptController;

    private boolean isTouching;

    public MediaPlayerView(@NonNull Context context) {
        this(context,null,0);
    }

    public MediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,R.attr.playerView);
    }

    public MediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            applyEditMode(context);
            return;
        }
        context = org.cz.media.player.base.ContextHelper.wrapPlayerContext(context,R.attr.mediaPlayer);
        int surfaceType = SURFACE_TYPE_SURFACE_VIEW;
        int playerLayoutId = 0;
        int controlLayoutId = 0;
        int gravity = 0;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MediaPlayerView, defStyleAttr, R.style.MediaPlayerView);
            try {
                playerLayoutId = a.getResourceId(R.styleable.MediaPlayerView_player_layout_id, 0);
                controlLayoutId = a.getResourceId(R.styleable.MediaPlayerView_control_layout_id, 0);
                useController = a.getBoolean(R.styleable.MediaPlayerView_use_controller, false);
                surfaceType = a.getInt(R.styleable.MediaPlayerView_surface_type, SURFACE_TYPE_SURFACE_VIEW);
                resizeMode = a.getInt(R.styleable.MediaPlayerView_resize_mode, resizeMode);
                controllerTouchMode = a.getInteger(R.styleable.MediaPlayerView_controller_touch_mode, TOUCH_MODE_AUTO_HIDE);
                controllerAutoShow = a.getBoolean(R.styleable.MediaPlayerView_auto_show, false);
                showBuffering = a.getInteger(R.styleable.MediaPlayerView_show_buffering, SHOW_BUFFERING_ALWAYS);
                gravity = a.getInteger(R.styleable.MediaPlayerView_android_layout, Gravity.CENTER);
            } finally {
                a.recycle();
            }
        }
        LayoutInflater.from(context).inflate(playerLayoutId, this);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        // Content frame.
        contentFrame = findViewById(R.id.playerContentView);
        if (contentFrame != null) {
            LayoutParams layoutParams = (LayoutParams) contentFrame.getLayoutParams();
            layoutParams.gravity=gravity;
            setResizeModeRaw(contentFrame, resizeMode);
        }
        // Create a surface view and insert it into the content frame, if there is one.
        surfaceView=generateSurfaceView(context, surfaceType);
        if(null!=surfaceView){
            surfaceView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            contentFrame.addView(surfaceView, 0);
        }
        onGenerateSurfaceView(surfaceView);
        // Overlay frame layout.
        overlayFrameLayout = findViewById(R.id.playerOverlayView);

        // Subtitle view.
        subtitleView = findViewById(R.id.playerSubtitleView);
        if (subtitleView != null) {
            subtitleView.setUserDefaultStyle();
            subtitleView.setUserDefaultTextSize();
        }

        // Playback control view.
        ViewStub playerControlLayoutViewStub = findViewById(R.id.playerControlLayoutViewStub);
        if(0 != controlLayoutId){
            //Use the user custom layout resource.
            Context wrappedContext = org.cz.media.player.base.ContextHelper.wrapPlayerContextInternal(context,R.attr.playerControlLayoutBase);
            playerControlLayoutViewStub.setLayoutInflater(LayoutInflater.from(wrappedContext));
            playerControlLayoutViewStub.setInflatedId(R.id.playerControlLayout);
            playerControlLayoutViewStub.setLayoutResource(controlLayoutId);
        }
        //Inflate the resource layout.
        playerControlLayoutViewStub.inflate();
        controller = findViewById(R.id.playerControlLayout);
        useController = useController && controller != null;
        hideController();
        updateContentDescription();
        if (controller != null) {
            controller.setHideAfterTimeout(TOUCH_MODE_AUTO_HIDE==controllerTouchMode);
            controller.addVisibilityListener(/* listener= */ visibility -> updateContentDescription());
        }
    }

    protected View generateSurfaceView(@NonNull Context context, int surfaceType) {
        View surfaceView;
        if (contentFrame != null && surfaceType != SURFACE_TYPE_NONE) {
            switch (surfaceType) {
                case SURFACE_TYPE_TEXTURE_VIEW:
                    surfaceView = new TextureView(context);
                    break;
                default:
                    surfaceView = new SurfaceView(context);
                    break;
            }
            //Use the view factory to create your own surface view.
            if(null!=surfaceViewCreateFactory){
                surfaceView=surfaceViewCreateFactory.onCreateView(contentFrame,surfaceView);
            }
        } else {
            surfaceView = null;
        }
        return surfaceView;
    }

    /**
     * For sub-class to overwrite and do something when the surface view generated.
     * @param view
     */
    public void onGenerateSurfaceView(View view){
    }

    private void applyEditMode(@NonNull Context context) {
        ImageView logo = new ImageView(context);
        if (Util.SDK_INT >= Build.VERSION_CODES.M) {
            configureEditModeLogoV23(getResources(), logo);
        } else {
            configureEditModeLogo(getResources(), logo);
        }
        addView(logo);
    }

    private void setResizeModeRaw(org.cz.media.player.base.AspectRatioFrameLayout aspectRatioFrame, int resizeMode) {
        aspectRatioFrame.setResizeMode(resizeMode);
    }

    public void setControllerEnable(boolean useController){
        this.useController=useController && null!=controller;
        Log.i(TAG,"setControllerEnable:"+this.useController);
    }

    public void registerPlayerComponent(PlayerComponent component) {
        if(useController()){
            controller.registerPlayerComponent(component);
        }
    }

    public void unregisterPlayerComponent(PlayerComponent component) {
        if(useController()){
            controller.unregisterPlayerComponent(component);
        }
    }

    public <T extends PlayerComponent> T getPlayerComponent(String componentName){
        return controller.getPlayerComponent(componentName);
    }

    /**
     * Called when there's a change in the aspect ratio of the content being displayed. The default
     * implementation sets the aspect ratio of the content frame to that of the content
     *
     * @param contentAspectRatio The aspect ratio of the content.
     * @param contentFrame The content frame, or {@code null}.
     */
    protected void onContentAspectRatioChanged(float contentAspectRatio, @Nullable org.cz.media.player.base.AspectRatioFrameLayout contentFrame) {
        if (contentFrame != null) {
            contentFrame.setAspectRatio(contentAspectRatio);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(useController()&&null!=controller){
            return controller.dispatchKeyEvent(event);
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    /**
     * Configured the edit mode logo
     * @param resources
     * @param logo
     */
    @TargetApi(23)
    private static void configureEditModeLogoV23(Resources resources, ImageView logo) {
        logo.setImageDrawable(resources.getDrawable(R.drawable.player_edit_mode_logo, null));
        logo.setBackgroundColor(resources.getColor(android.R.color.white, null));
    }

    private static void configureEditModeLogo(Resources resources, ImageView logo) {
        logo.setImageDrawable(resources.getDrawable(R.drawable.player_edit_mode_logo));
        logo.setBackgroundColor(resources.getColor(android.R.color.white));
    }

    /**
     * Gets the view onto which video is rendered. This is a:
     *
     * <ul>
     *   <li>{@link SurfaceView} by default, or if the {@code surface_type} attribute is set to {@code
     *       surface_view}.
     *   <li>{@link TextureView} if {@code surface_type} is {@code texture_view}.
     *   <li>{@link VideoDecoderGLSurfaceView} if {@code surface_type} is {@code
     *       video_decoder_gl_surface_view}.
     *   <li>{@code null} if {@code surface_type} is {@code none}.
     * </ul>
     *
     * @return The {@link SurfaceView}, {@link TextureView}, {@link
     *     VideoDecoderGLSurfaceView} or {@code null}.
     */
    @Nullable
    public View getVideoSurfaceView() {
        return surfaceView;
    }

    @Nullable
    public org.cz.media.player.base.AspectRatioFrameLayout getContentFrame() {
        return contentFrame;
    }

    /**
     * Gets the overlay {@link FrameLayout}, which can be populated with UI elements to show on top of
     * the player.
     *
     * @return The overlay {@link FrameLayout}, or {@code null} if the layout has been customized and
     *     the overlay is not present.
     */
    @Nullable
    public FrameOverlayLayout getOverlayFrameLayout() {
        return overlayFrameLayout;
    }

    /**
     * Gets the {@link SubtitleView}.
     *
     * @return The {@link SubtitleView}, or {@code null} if the layout has been customized and the
     *     subtitle view is not present.
     */
    @Nullable
    public SubtitleView getSubtitleView() {
        return subtitleView;
    }

    public int getShowBuffering() {
        return showBuffering;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!useController() || player == null|| controllerTouchMode == TOUCH_MODE_NONE) {
            return false;
        }
        //if we want to handle the touch event.
        FrameOverlayLayout overlayFrameLayout = getOverlayFrameLayout();
        int childCount = overlayFrameLayout.getChildCount();
        for(int i=0;i<childCount;i++){
            View childView = overlayFrameLayout.getChildAt(i);
            if(childView instanceof Touchable){
                return false;
            }
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                return true;
            case MotionEvent.ACTION_UP:
                if (isTouching) {
                    isTouching = false;
                    performClick();
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return toggleControllerVisibility();
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (!useController() || player == null) {
            return false;
        }
        maybeShowController(true);
        return true;
    }

    @EnsuresNonNullIf(expression = "controller", result = true)
    private boolean useController() {
        if (useController) {
            Assertions.checkStateNotNull(controller);
            return true;
        }
        return false;
    }

    private boolean toggleControllerVisibility() {
        if (0 != interceptController || !useController() || player == null) {
            return false;
        }
        if (!controller.isVisible()) {
            maybeShowController(true);
        } else {
            controller.hide();
        }
        return true;
    }

    /**
     * Shows the playback controls, but only if forced or shown indefinitely.
     */
    private void maybeShowController(boolean isForced) {
        if (0 == interceptController && useController()) {
            boolean wasShowingIndefinitely = controller.isVisible() && controller.getShowTimeoutMs() <= 0;
            boolean shouldShowIndefinitely = shouldShowControllerIndefinitely();
//            Log.i(TAG,"id:"+hashCode()+" maybeShowController:"+isForced+" wasShowingIndefinitely:"+wasShowingIndefinitely+" shouldShowIndefinitely:"+shouldShowIndefinitely);
            if (isForced || wasShowingIndefinitely || shouldShowIndefinitely) {
                showController();
            }
        }
    }

    public boolean shouldShowControllerIndefinitely() {
        if (player == null) {
            return true;
        }
        return controllerAutoShow;
    }

    public void showController() {
        if (!useController()) {
            return;
        }
        controller.show();
    }

    /**
     * Start to intercept the hide or show event of the controller layout.
     * Always work with the method {@link #stopInterceptControllerVisibleEvent()}
     */
    public void startInterceptControllerVisibleEvent(){
        interceptController++;
    }

    /**
     * Stop intercept the controller layout's visible event.
     * Always work with the method {@link #stopInterceptControllerVisibleEvent()}
     */
    public void stopInterceptControllerVisibleEvent(){
        interceptController--;
    }

    /**
     * Hides the playback controls. Does nothing if playback controls are disabled.
     */
    public void hideController() {
        if (0 == interceptController && controller != null) {
            controller.hide();
        }
    }

    private void updateContentDescription() {
        if (controller == null || !useController) {
            setContentDescription(/* contentDescription= */ null);
        } else if (controller.getVisibility() == View.VISIBLE) {
            setContentDescription(
                    /* contentDescription= */ controllerTouchMode != TOUCH_MODE_NONE
                            ? getResources().getString(R.string.media_controls_hide) : null);
        } else {
            setContentDescription(/* contentDescription= */ getResources().getString(R.string.media_controls_show));
        }
    }

    /**
     * Switches the view targeted by a given {@link Player}.
     *
     * @param player The player whose target view is being switched.
     * @param oldPlayerView The old view to detach from the player.
     * @param newPlayerView The new view to attach to the player.
     */
    public static void switchTargetView(
            Player player, @Nullable org.cz.media.player.base.MediaPlayerView oldPlayerView, @Nullable org.cz.media.player.base.MediaPlayerView newPlayerView) {
        if (oldPlayerView == newPlayerView) {
            return;
        }
        // We attach the new view before detaching the old one because this ordering allows the player
        // to swap directly from one surface to another, without transitioning through a state where no
        // surface is attached. This is significantly more efficient and achieves a more seamless
        // transition when using platform provided video decoders.
        if (newPlayerView != null) {
            newPlayerView.setPlayer(player);
        }
        if (oldPlayerView != null) {
            oldPlayerView.setPlayer(null);
        }
    }

    /** Returns the player currently set on this view, or null if no player is set. */
    @Nullable
    public Player getPlayer() {
        return player;
    }

    /**
     * Set the {@link Player} to use.
     *
     * <p>To transition a {@link Player} from targeting one view to another, it's recommended to use
     * {@link #switchTargetView(Player, org.cz.media.player.base.MediaPlayerView, org.cz.media.player.base.MediaPlayerView)} rather than this method. If you do
     * wish to use this method directly, be sure to attach the player to the new view <em>before</em>
     * calling {@code setPlayer(null)} to detach it from the old one. This ordering is significantly
     * more efficient and may allow for more seamless transitions.
     *
     * @param player The {@link Player} to use, or {@code null} to detach the current player. Only
     *     players which are accessed on the main thread are supported ({@code
     *     player.getApplicationLooper() == Looper.getMainLooper()}).
     */
    public void setPlayer(@Nullable Player player) {
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper());
        Assertions.checkArgument(
                player == null || player.getApplicationLooper() == Looper.getMainLooper());
        if (this.player == player) {
            return;
        }
        @Nullable Player oldPlayer = this.player;
        if (oldPlayer != null) {
            oldPlayer.removeListener(componentListener);
            @Nullable Player.VideoComponent oldVideoComponent = oldPlayer.getVideoComponent();
            if (oldVideoComponent != null) {
                oldVideoComponent.removeVideoListener(componentListener);
                if (surfaceView instanceof TextureView) {
                    oldVideoComponent.clearVideoTextureView((TextureView) surfaceView);
                } else if (surfaceView instanceof VideoDecoderGLSurfaceView) {
                    oldVideoComponent.setVideoDecoderOutputBufferRenderer(null);
                } else if (surfaceView instanceof SurfaceView) {
                    oldVideoComponent.clearVideoSurfaceView((SurfaceView) surfaceView);
                }
            }
            @Nullable Player.TextComponent oldTextComponent = oldPlayer.getTextComponent();
            if (oldTextComponent != null) {
                oldTextComponent.removeTextOutput(componentListener);
            }
        }
        this.player = player;
        if (useController()) {
            controller.setPlayer(player);
        }
        if (subtitleView != null) {
            subtitleView.setCues(null);
        }
        if (null!=player) {
            @Nullable Player.VideoComponent newVideoComponent = player.getVideoComponent();
            if (newVideoComponent != null) {
                if (surfaceView instanceof TextureView) {
                    newVideoComponent.setVideoTextureView((TextureView) surfaceView);
                } else if (surfaceView instanceof VideoDecoderGLSurfaceView) {
                    newVideoComponent.setVideoDecoderOutputBufferRenderer(
                            ((VideoDecoderGLSurfaceView) surfaceView).getVideoDecoderOutputBufferRenderer());
                } else if (surfaceView instanceof SurfaceView) {
                    newVideoComponent.setVideoSurfaceView((SurfaceView) surfaceView);
                }
                newVideoComponent.addVideoListener(componentListener);
            }
            @Nullable Player.TextComponent newTextComponent = player.getTextComponent();
            if (newTextComponent != null) {
                newTextComponent.addTextOutput(componentListener);
            }
            //To avoid the relayout of the content frame.
//            contentFrame.setVisibility(View.INVISIBLE);
//            if(player instanceof Player.VideoComponent){
//                Player.VideoComponent videoComponent = (Player.VideoComponent) player;
//                videoComponent.addVideoListener(new VideoListener() {
//                    @Override
//                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
//                        videoComponent.removeVideoListener(this);
//                        contentFrame.setVisibility(View.VISIBLE);
//                    }
//                });
//            }
            player.addListener(componentListener);
            maybeShowController(false);
        } else {
            hideController();
        }
        if(null!=player&&null!=playerChangeListenerList){
            for(OnPlayerChangeListener playerChangeListener:playerChangeListenerList){
                playerChangeListener.onPlayerChanged(player,oldPlayer);
            }
        }
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (surfaceView instanceof SurfaceView) {
            // Work around https://github.com/google/ExoPlayer/issues/3160.
            surfaceView.setVisibility(visibility);
        }
    }

    @MainThread
    public void addOnPlayerChangeListener(OnPlayerChangeListener listener){
        if(null==playerChangeListenerList){
            playerChangeListenerList=new ArrayList<>();
        }
        this.playerChangeListenerList.add(listener);
    }

    @MainThread
    public void removeOnPlayerChangeListener(OnPlayerChangeListener listener){
        if(null!=playerChangeListenerList){
            playerChangeListenerList.remove(listener);
        }
    }

    /**
     * Adds a {@link AnimationControlLayout.VisibilityListener}.
     *
     * @param listener The listener to be notified about visibility changes.
     */
    public void addControlLayoutVisibilityListener(AnimationControlLayout.VisibilityListener listener) {
        if(null!=controller){
            controller.addVisibilityListener(listener);
        }
    }

    /**
     * Removes a {@link AnimationControlLayout.VisibilityListener}.
     *
     * @param listener The listener to be removed.
     */
    public void removeControlLayoutVisibilityListener(AnimationControlLayout.VisibilityListener listener) {
        if(null!=controller){
            controller.removeVisibilityListener(listener);
        }
    }

    public void setSurfaceViewCreateFactory(SurfaceViewCreateFactory factory){
        this.surfaceViewCreateFactory =factory;
    }

    public SurfaceViewCreateFactory getSurfaceViewCreateFactory() {
        return surfaceViewCreateFactory;
    }

    /**
     * This is the persistent state that is saved by MediaPlayerView.  Only needed
     * if you are creating a sublass of MediaPlayerView that must save its own
     * state, in which case it should implement a subclass of this which
     * contains that state.
     *
     * Noticed:
     * If you don't want to save all the player state.
     * Take a look at {@link #isSaveEnabled()} and {@link #setSaveEnabled(boolean)}
     */
    public static class SavedState extends AbsSavedState {
        /**
         * This is for recover playing status.
         */
        private boolean playWhenReady=false;
        private int currentWindowIndex=0;
        private long currentPosition=0L;


        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte(playWhenReady?(byte)1:(byte)0);
            out.writeInt(currentWindowIndex);
            out.writeLong(currentPosition);
        }

        public static final Creator<org.cz.media.player.base.MediaPlayerView.SavedState> CREATOR = new ClassLoaderCreator<org.cz.media.player.base.MediaPlayerView.SavedState>() {
            @Override
            public org.cz.media.player.base.MediaPlayerView.SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new org.cz.media.player.base.MediaPlayerView.SavedState(in, loader);
            }

            @Override
            public org.cz.media.player.base.MediaPlayerView.SavedState createFromParcel(Parcel in) {
                return new org.cz.media.player.base.MediaPlayerView.SavedState(in, null);
            }
            @Override
            public org.cz.media.player.base.MediaPlayerView.SavedState[] newArray(int size) {
                return new org.cz.media.player.base.MediaPlayerView.SavedState[size];
            }
        };

        SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            playWhenReady = 0!=in.readByte();
            currentWindowIndex=in.readInt();
            currentPosition=in.readLong();
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        if(null!=player){
            ss.playWhenReady = player.getPlayWhenReady();
            ss.currentWindowIndex = player.getCurrentWindowIndex();
            ss.currentPosition = player.getContentPosition();
        }
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if(null!=player){
            player.setPlayWhenReady(ss.playWhenReady);
            player.seekTo(ss.currentWindowIndex,ss.currentPosition);
        }
    }

    private final class ComponentListener implements Player.EventListener, TextOutput, VideoListener, OnLayoutChangeListener {

        public ComponentListener() {
        }

        // TextOutput implementation
        @Override
        public void onCues(List<Cue> cues) {
            if (subtitleView != null) {
                subtitleView.onCues(cues);
            }
        }

        // VideoListener implementation
        @Override
        public void onVideoSizeChanged(
                int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            float videoAspectRatio =
                    (height == 0 || width == 0) ? 1 : (width * pixelWidthHeightRatio) / height;

            if (surfaceView instanceof TextureView) {
                // Try to apply rotation transformation when our surface is a TextureView.
                if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                    // We will apply a rotation 90/270 degree to the output texture of the TextureView.
                    // In this case, the output video's width and height will be swapped.
                    videoAspectRatio = 1 / videoAspectRatio;
                }
                if (textureViewRotation != 0) {
                    surfaceView.removeOnLayoutChangeListener(this);
                }
                textureViewRotation = unappliedRotationDegrees;
                if (textureViewRotation != 0) {
                    // The texture view's dimensions might be changed after layout step.
                    // So add an OnLayoutChangeListener to apply rotation after layout step.
                    surfaceView.addOnLayoutChangeListener(this);
                }
                applyTextureViewRotation((TextureView) surfaceView, textureViewRotation);
            }
            onContentAspectRatioChanged(videoAspectRatio, contentFrame);

            //Todo Here is the best time for us to play the video
//            Player player = getPlayer();
//            if(null!=player){
//                boolean playWhenReady = player.getPlayWhenReady();
//                if(playWhenReady){
//
//                }
//            }
        }

        /** Applies a texture rotation to a {@link TextureView}. */
        private void applyTextureViewRotation(TextureView textureView, int textureViewRotation) {
            Matrix transformMatrix = new Matrix();
            float textureViewWidth = textureView.getWidth();
            float textureViewHeight = textureView.getHeight();
            if (textureViewWidth != 0 && textureViewHeight != 0 && textureViewRotation != 0) {
                float pivotX = textureViewWidth / 2;
                float pivotY = textureViewHeight / 2;
                transformMatrix.postRotate(textureViewRotation, pivotX, pivotY);

                // After rotation, scale the rotated texture to fit the TextureView size.
                RectF originalTextureRect = new RectF(0, 0, textureViewWidth, textureViewHeight);
                RectF rotatedTextureRect = new RectF();
                transformMatrix.mapRect(rotatedTextureRect, originalTextureRect);
                transformMatrix.postScale(
                        textureViewWidth / rotatedTextureRect.width(),
                        textureViewHeight / rotatedTextureRect.height(),
                        pivotX,
                        pivotY);
            }
            textureView.setTransform(transformMatrix);
        }

        @Override
        public void onRenderedFirstFrame() {
            contentFrame.setVisibility(View.VISIBLE);
        }

        // OnLayoutChangeListener implementation
        @Override
        public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            applyTextureViewRotation((TextureView) view, textureViewRotation);
        }

        // Player.EventListener implementation
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, @Player.State int playbackState) {
//            maybeShowController(false);
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
        }
    }

    public interface OnPlayerChangeListener {
        void onPlayerChanged(Player newPlayer, Player oldPlayer);
    }

    public interface SurfaceViewCreateFactory {
        View onCreateView(ViewGroup parent, View surfaceView);
    }
}
