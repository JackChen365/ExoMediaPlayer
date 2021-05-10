## 全局样式定制

> 演示定制全局样式

#### 布局预览

```
//使用默认样式,但修改了控制面板
<org.cz.media.player.library.AppCompatMediaPlayerView
    android:id="@+id/playerView1"
    android:layout_width="320dp"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    app:control_layout_id="@layout/media_player_mini_control_layout" />

//使用定制样式,但使用默认面板
<org.cz.media.player.library.AppCompatMediaPlayerView
    android:id="@+id/playerView2"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:visibility="gone"
    android:theme="@style/MediaPlayer.Compat.Blue"/>

```

#### 代码初始化

```
private fun initializePlayerFromAssets() {
    val mediaPlayer = SimpleExoPlayer.Builder(this).build()
    playerView.player = mediaPlayer
    //Uri from assets.
    val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
    val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
        .createMediaSource(Uri.parse("asset:///sample media.mp4"))
    mediaPlayer.playWhenReady=false
    mediaPlayer.prepare(mediaSource,false,false)
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

### 使用 MediaPlayerGroup 管理多个 AppCompatMediaPlayerView 对象

```
val mediaPlayerGroup = MediaPlayerGroup()
mediaPlayerGroup.attachToHost(frameLayout)
mediaPlayerGroup.show(R.id.playerView1)

nextButton.setOnClickListener {
    mediaPlayerGroup.next()
}
```


样式:android:theme="@style/MediaPlayer.Compat.Blue" 声明在文件[app:/res/value/player-style.xml]. 具体效果请查看演示
