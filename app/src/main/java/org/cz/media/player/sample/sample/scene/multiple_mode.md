## 多个窗口尺寸演示

> 演示动态切换播放器窗口,主要知识点:

* 动态切换不同窗口
* 了解MediaPlayerViewStub以及MediaPlayerGroup使用

#### 布局预览

```
<org.cz.media.player.library.AppCompatMediaPlayerView
        android:id="@+id/playerView1"
        android:layout_width="300dp"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:background="@android:color/black"/>

<org.cz.media.player.library.MediaPlayerViewStub
    android:id="@+id/playerView2"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout="@layout/media_player_blue"/>
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

### MediaPlayerViewStub介绍

当我们存在多个窗口时,其他窗口其实是并存在一个布局,一并加载的.因为播放器控件的视图复杂度较高,同时用户并不一定需要切换视图.随用户行为变化.<br>
所以此处,我们扩展了控件:MediaPlayerViewStub,不使用 ViewStub 的原因是因为增加播放器加载的特征.主要为动态识别是不是包含 MediaPlayerView 做自动判断.

注意.此控件可单独使用.使用方法与 ViewStub 一致.

```
<org.cz.media.player.library.MediaPlayerViewStub
    android:id="@+id/playerView2"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout="@layout/media_player_blue"/>



//layout/media_player_blue
<?xml version="1.0" encoding="utf-8"?>
<org.cz.media.player.library.AppCompatMediaPlayerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/playerView2"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:visibility="gone"
    android:background="@color/md_grey_200"
    android:theme="@style/MediaPlayer.Compat.Blue"/>

```

### MediaPlayerGroup介绍

用于管理一个组内的 MediaPlayer 对象.

给定一个父容器.会自动遍历这个容器内的 AppCompatMediaPlayerView 与MediaPlayerViewStub.然后维护一个列表.

提供两个方法

* show 展示指定 id 的 MediaPlayerView

* next 切换致组内下一个MediaPlayerView.

注意 Next 如果组内有3个 View,会一直按照 0->1->2->0->1->2顺序切换.

使用示例

```
val mediaPlayerGroup = MediaPlayerGroup()
//关到此容器.然后检索出此容器内的所有AppCompatMediaPlayerView与MediaPlayerViewStub.
mediaPlayerGroup.attachToHost(frameLayout)
mediaPlayerGroup.show(R.id.playerView1)

nextButton.setOnClickListener {
    mediaPlayerGroup.next()
}
```
