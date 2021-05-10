## 基本使用

演示基本的AppCompatMediaPlayerView定制控制面板使用,用于显示随机/播放重复模式.

#### 布局预览

```
<org.cz.media.player.base.MediaPlayerView
        android:id="@+id/playerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:control_layout_id="@layout/media_player_custom_control_layout"/>
```

#### 代码初始化

```
private fun initializePlayer() {
    //Uri from assets
    val mediaPlayer = SimpleExoPlayer.Builder(this).build()
    playerView.player = mediaPlayer
    val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
    val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
        .createMediaSource(Uri.parse("asset:///sample media.mp4"))
    mediaPlayer.prepare(mediaSource,false,false)
}

override fun onStart() {
    super.onStart()
    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
        initializePlayer()
    }
}

override fun onResume() {
    super.onResume()
    val player = playerView.player
    if(Build.VERSION.SDK_INT< Build.VERSION_CODES.N||null==player){
        initializePlayer()
    }
}

override fun onPause() {
    super.onPause()
    if(Build.VERSION.SDK_INT< Build.VERSION_CODES.N){
        releasePlayer();
    }
}

override fun onStop() {
    super.onStop()
    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
        releasePlayer()
    }
}
```