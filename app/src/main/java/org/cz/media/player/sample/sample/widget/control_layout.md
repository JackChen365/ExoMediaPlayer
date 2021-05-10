## 控制面板演示

#### 演示目的

* 演示控制面板区域划分.
* 演示控制面板样式,布局,动画.

#### 布局预览

```
<org.cz.media.player.base.layout.AnimationControlLayout
    android:id="@+id/controllerLayout"
    android:layout_width="match_parent"
    android:layout_height="320dp"
    style="@style/PlayerAnimationControlLayout.Custom"/>

```

#### 动画样式

```
<style name="PlayerAnimationControlLayout.Custom">
    <item name="control_left_layout">@layout/media_controller_left_placeholder_layout1</item>
    <item name="control_top_layout">@layout/media_controller_top_placeholder_layout1</item>
    <item name="control_right_layout">@layout/media_controller_right_placeholder_layout1</item>
    <item name="control_bottom_layout">@layout/media_controller_bottom_placeholder_layout1</item>
    <item name="control_center_layout">@layout/media_controller_placeholder_layout1</item>
</style>

//layout/media_controller_left_placeholder_layout1
<?xml version="1.0" encoding="utf-8"?>
<View xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="80dp"
    android:layout_height="match_parent"
    android:background="@android:color/holo_green_dark"
    app:layout_control_animation="@style/left_side_animation"/>
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