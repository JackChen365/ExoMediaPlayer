## 基本使用

演示基本的AppCompatMediaPlayerView使用,主要展示从三种不同来源加载视频.

#### 布局预览

```
<org.cz.media.player.library.AppCompatMediaPlayerView
        android:id="@+id/playerView"
        android:layout_width="match_parent"
        android:layout_height="320dp"
        android:background="@android:color/black"/>
```

#### 代码初始化

```
radioButtonLayout.setOnCheckedChangeListener { _, checkedId ->
    releasePlayer()
    when(checkedId){
        0->initializePlayerFromUrl()
        1->initializePlayerFromDisk()
        2->initializePlayerFromAssets()
    }
}

//Uri from url.
private fun initializePlayerFromUrl() {
    val player = playerView.player
    if(null==player){
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.player = mediaPlayer
        val url="http://vfx.mtime.cn/Video/2019/03/13/mp4/190313094901111138.mp4";
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(url))
        mediaPlayer.playWhenReady=true
        mediaPlayer.prepare(mediaSource,false,false)
    }
}

//Uri from file.
private fun initializePlayerFromDisk() {
    val mediaPlayer = SimpleExoPlayer.Builder(this).build()
    playerView.player = mediaPlayer
    val file = File(file_path)
    val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(Uri.fromFile(file))
    mediaPlayer.playWhenReady=true
    mediaPlayer.prepare(mediaSource)
}

//Uri from assets.
private fun initializePlayerFromAssets() {
    val mediaPlayer = SimpleExoPlayer.Builder(this).build()
    playerView.player = mediaPlayer
    val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
    val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
        .createMediaSource(Uri.parse("asset:///sample media.mp4"))
    mediaPlayer.playWhenReady=true
    mediaPlayer.prepare(mediaSource,false,false)
}
```