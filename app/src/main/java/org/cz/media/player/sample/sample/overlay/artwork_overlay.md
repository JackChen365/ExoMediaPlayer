## 封面浮层使用

> 演示播放面板浮层,主要知识点:

* 扩展视图浮层.
* 加载视频自定义封面

#### 布局预览

```
<org.cz.media.player.library.AppCompatMediaPlayerView
    android:id="@+id/playerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/black"/>
```

#### 代码初始化

```
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_media_playlist)
     //Attach the player overlay to the MediaPlayerView.
    val defaultMediaPlayerOverlay = GlideArtworkOverlay(this, "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2491915268,3538783058&fm=15&gp=0.jpg")
    defaultMediaPlayerOverlay.attachToPlayerView(this,playerView)
}


private fun initializePlayer() {
    ...
}

override fun onResume() {
    super.onResume()
    if(null==playerView.player){
        initializePlayer()
    }
}

override fun onPause() {
    super.onPause()
    releasePlayer()
}


private fun releasePlayer() {
    playerView.player?.release()
}
```

核心代码介绍 

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
        //使用 Glide 加载图片.或者其他途径.
        Glide.with(context).load(url).into(imageView);
    }
}


```