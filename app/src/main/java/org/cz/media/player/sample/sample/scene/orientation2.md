## 切换屏幕方向

> 演示动态切换屏幕方向(重建 Activity)

* 扩展功能组件.

#### 布局预览

```
<org.cz.media.player.library.AppCompatMediaPlayerView
        android:id="@+id/playerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/black"
        app:control_layout_id="@layout/media_player_fill_screen_control_layout"/>


//layout/media_player_fill_screen_control_layout
<?xml version="1.0" encoding="utf-8"?>
<org.cz.media.player.base.controller.MediaPlayerControlLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        app:layout_control_animation="@style/bottom_side_animation"
        app:layout_control_panel="bottom"
        android:theme="?playerControlLayoutBase">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:paddingTop="4dp"
            android:orientation="horizontal">

            [...]

            <ImageButton
                android:id="@id/playerPlayButton"
                style="@style/MediaButton.Play"/>

            <ImageButton
                android:id="@id/playerPauseButton"
                style="?attr/playerPauseButton"/>

            动态添加的操作按钮
            <ImageButton
                android:id="@+id/playerFillScreenButton"
                style="@style/MediaButton"
                android:src="@mipmap/ic_fullscreen_enter"/>

        </LinearLayout>
    </LinearLayout>
</org.cz.media.player.base.controller.MediaPlayerControlLayout>


//横屏布局 layout-land/media_player_fill_screen_control_layout
请自行查看
```

#### 代码初始化

```
private fun initializePlayer() {
    val mediaPlayer = SimpleExoPlayer.Builder(this).build()
    playerView.registerPlayerComponent(FillScreenComponent(this))
    playerView.player = mediaPlayer
    //Uri from assets.
    val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
    val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse("asset:///sample media.mp4"))
    mediaPlayer.playWhenReady=true
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

#### 核心代码介绍

* 根据方向切换.动态更改 PlayerView 尺寸. 

```
override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    // Checking the orientation of the screen
    if (newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE) {
        //First Hide other objects (listview or recyclerview), better hide them using Gone.
        val params = playerView.getLayoutParams()
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        playerView.requestLayout()
    } else if (newConfig.orientation === Configuration.ORIENTATION_PORTRAIT) {
        //unhide your objects here.
        val params = playerView.getLayoutParams()
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        playerView.requestLayout()
    }
}
```

* 切换方向时保存播放进度等信息

```
override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    //还原播放进度
    playWhenReady=savedInstanceState.getBoolean("playWhenReady")
    currentWindowIndex=savedInstanceState.getInt("currentWindowIndex")
    currentPosition=savedInstanceState.getLong("currentPosition")
    val message=savedInstanceState.getString("message")
    val output=StringBuilder()
    output.append(message)
    output.append("Activity:${hashCode()} onRestoreInstanceState\n")
    messageText.append(output)
}

override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    //保留播放进度
    val output=StringBuilder()
    output.append(messageText.text.toString())
    output.append("Activity:${hashCode()} onSaveInstanceState\n")
    outState.putString("message",output.toString())
    outState.putBoolean("playWhenReady",playWhenReady)
    outState.putInt("currentWindowIndex",currentWindowIndex)
    outState.putLong("currentPosition",currentPosition)
}
```

其他方面与示例:OrientationSample1Activity相似,请参与[orientation1](orientation1.md)了解自定义组件相关