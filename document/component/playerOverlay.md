## PlayerOverLay

### 关联演示
* app/sample/overlay/ArtworkOverlaySampleActivity.java(封面浮层)
* app/sample/overlay/GestureDetectOverlaySampleActivity.java(手势探测浮层)

### 图例

* ![artwork](https://github.com/momodae/LibraryResources/blob/master/ExoMediaPlayer/image/overlay_artwork.gif?raw=true)
* ![gesture](https://github.com/momodae/LibraryResources/blob/master/ExoMediaPlayer/image/overlay_gesture.gif?raw=true)

### 浮层扩展

目前已经实现浮层
* MediaPlayerGestureDetectOverlay 手势探测浮层
    内容包容:
    * AcceleratePressGestureDetector(长按加速)
    * BrightnessGestureDetector(亮度调节)
    * DoubleTapGestureDetector(双击)
    * LongTapGestureDetector(长按)
    * MultipleTapGestureDetector(多击行为,如双击,双次点击)
    * PlayerProgressGestureDetector(播放进度调节)
    * SingleTapGestureDetector(单击)
    * SoundGestureDetector(音量调节)
    
    以上手势并不是所有的都是相互独立的.注意长按加速,与长按事件是互斥的.两种行为会同一触发.
    
* MediaPlayerArtworkOverlay 封面浮层
    主要用于扩展封面,暴露了图片并加载方法,需要外部根据图片加载库,或者其他加载途径加载图片.


因为并不是所有场景都需要手势行为,所以此处定义为一个可选择的扩展,以下代码块是关联默认的手势面板.

```
//Attach the player overlay to the MediaPlayerView.
//{@code MediaPlayerGestureDetectOverlay}
val defaultMediaPlayerOverlay = MessageGestureDetectOverlay(this)
defaultMediaPlayerOverlay.attachToPlayerView(this,playerView)
```

* 自定义浮层面板

以下示例,演示了一个自定义的封面浮层扩展.注意这个扩展由开发人员根据业务需求自行定义.因为 Glide库依赖应该由上层业务对接.


```
public class GlideArtworkOverlay extends MediaPlayerArtworkOverlay {
    private Context context;
    private final String url;

    public GlideArtworkOverlay(Context context,String url) {
        this.context=context;
        this.url = url;
    }

    @Override
    public void onAttachToPlayer(MediaPlayerView playerView, View contentView) {
        super.onAttachToPlayer(playerView, contentView);
        playerView.addOnPlayerChangeListener((newPlayer, oldPlayer) -> {
            newPlayer.addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if(Player.STATE_READY==playbackState&&playWhenReady){
                        contentView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
//                    contentView.setVisibility(isPlaying?View.GONE:View.VISIBLE);
                }
            });
        });
    }

    @Override
    public void onLoadImage(ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }
}

```

浮层扩展主要实现两个方法

* onCreateOverlay 创建浮层视图

```
//方法原型与示例
@Override
public View onCreateOverlay(Context context, ViewGroup parent) {
    return LayoutInflater.from(context).inflate(R.layout.media_player_artwork_overlay_layout,parent,false);
}
```

* onAttachToPlayer 当扩展浮层附加到视图时

```
//方法原型与示例
@Override
public void onAttachToPlayer(MediaPlayerView playerView,View contentView) {
    super.onAttachToPlayer(playerView,contentView);
    ImageView imageView = (ImageView) contentView;
    playerView.addOnPlayerChangeListener(this);
    onLoadImage(imageView);
}
```

### 扩展自定义手势探测介绍

```
public interface GestureDetector {
    /**
     * Make the container handle the event in the beginning.
     * Some gestures like scrolling horizontally may be a conflict with the container outside.
     * For example if the View inside the ViewPager. Because you are going to detect the gesture.
     * So you will not know if you need to handle the event or not, And the container will consume the event every time.
     * Under this circumstance and make sure you have the change to receive the events. You could disallow the parent view to intercept the events.
     * @return
     */
    default boolean disallowInterceptTouchEvent(){
        return false;
    }
    /**
     * When the finger presses the screen. Just like the motion event: {@link MotionEvent#ACTION_DOWN}
     * You could do somethings like initialize your widget or some class fields you might want to use.
     * Noticed whatever you will handle the event or not.
     * The pair of functions {@link #onStartDetect(View)} and {@link #onStopDetect(View)} will always be invoke.
     *
     * @param v The parent view.
     */
    void onStartDetect(View v);

    /**
     * Return true if you want to handle all the following events.
     * It more like the The {@link android.view.ViewGroup#onInterceptTouchEvent(MotionEvent)}
     * But here the difference was everything implementation has the change to intercept the event by their own behavior.
     *
     * For example, every detector receives the event, Meanwhile, one guy guesses This gesture seems like the finger scrolling horizontally.
     * I could handle it. then next time you return true and the others will never receive the event.
     * @param v
     * @param e
     * @return
     */
    boolean onInterceptTouchEvent(View v, MotionEvent e);

    /**
     * The touch event.
     * This method will invoked Either no one intercept the event or you intercept the event.
     * {@link #onInterceptTouchEvent(View, MotionEvent)} means you want to handle the event.
     *
     * @param v
     * @param e
     */
    void onTouchEvent(View v, MotionEvent e);

    /**
     * The stop event. Occur when the event was {@link MotionEvent#ACTION_UP} or {@link MotionEvent#ACTION_CANCEL}
     * No matter you handle the event or not, The method still invoked when the event received.
     * @param v
     */
    void onStopDetect(View v);
}
```

* disallowInterceptTouchEvent 
> 是否禁止父容器拦截事件. 用于手势探测的子类决定是否强行拦截事件.禁止在父容器中,如 ViewPager中接收不到事件的问题.但同样要注意,一旦生效了.父容器就接受不到事件了.<br>
> 这方式的用处是,如果一个手势浮层内,你可选组装的手势内,没有冲突的,你可以直接使用,但是如果有,你可以根据逻辑确定.选择让内部生效,还是外部.

* onStartDetect 开始手势探测时触发,此方法一定会触发,与 onStopDetect配合使用

* onInterceptTouchEvent 决定某种条件合适下,拦截事件.会导致其他的实现类再也接收不到事件.但在此之前.所有实现类都可以接收到.即如果所有实现类都不拦截,可以正常实现多种手势同样生效的目的

* onTouchEvent 正常事件处理.


### 注意事项
* 扩展浮动的视图在播放器控制面板的下面.意味着播放面板会优先处理事件,以及显示等操作. 如果需要盖在播放面板上面.请自行使用桢布局或者其他布局,直接操作.

