## 全局样式定制

> 演示定制全局样式

#### 布局预览

```
<org.cz.media.player.library.AppCompatMediaPlayerView
        android:id="@+id/playerView"
        android:layout_width="match_parent"
        android:layout_height="320dp"
        //此处注意.使用了系统的 theme 声明加载布局时的包装样式
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

### MediaPlayer.Compat.Blue

> 本样式复写了全局的样式体系,一般用于在对接播放器之前,对应用需要的样式进行定制.

```
<style name="MediaPlayer.Compat.Blue">
    <!--The different status styles, like buffering or error-->
    <item name="playerStatusLayout">@style/MediaPlayerStatusStyle.Blue</item>
    <item name="playerControlLayoutBase">@style/PlayerControlLayoutBase.Blue</item>
    <!--标志覆盖样式-->
    <item name="playerOverrideStyle">true</item>
</style>

<!-- ===================================================== -->
<!--The basic style of media player-->
<!-- ===================================================== -->
<style name="PlayerControlLayoutBase.Blue">
    <!--The basic button style-->
    <item name="playerShuffleButton">@style/MediaButton.ShuffleOn.Blue</item>
    <item name="playerShuffleButtonOn">@style/MediaButton.ShuffleOn.Blue</item>
    <item name="playerShuffleButtonOff">@style/MediaButton.ShuffleOff.Blue</item>
    [...]
</style>
```

此处,我们采用了大量的样式复写.只覆盖需要修改的部分样式,具体样式请查看演示.



