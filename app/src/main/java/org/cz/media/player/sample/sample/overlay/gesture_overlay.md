## 手势浮层使用

> 演示播放面板手势操作浮层,主要知识点:

* 如何探测手势,并自定义手势.
* 使用不同的已编写的手势

#### 己编写的手势包括:

* AcceleratePressGestureDetector(长按加速)
* BrightnessGestureDetector(亮度调节)
* DoubleTapGestureDetector(双击)
* LongTapGestureDetector(长按)
* MultipleTapGestureDetector(多击行为,如双击,双次点击)
* PlayerProgressGestureDetector(播放进度调节)
* SingleTapGestureDetector(单击)
* SoundGestureDetector(音量调节)
    
以上手势并不是所有的都是相互独立的.注意长按加速,与长按事件是互斥的.两种行为会同一触发.

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

override fun onStart() {
    super.onStart()
    initializePlayerFromAssets()
}

override fun onStop() {
    super.onStop()
    releasePlayer()
}

private fun releasePlayer() {
    playerView.player?.release()
}
```

具体内容请查看本演示源码.