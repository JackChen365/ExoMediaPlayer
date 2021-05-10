## 播放器使用示例

### 样式介绍

| 属性名称 | 属性类型 | 示例 |
| ------ | ------ | ------ |
| use_controller | boolean | 是否使用默认的播放控制面板 |
| hide_on_touch | boolean  | 按下时隐藏或者显示播放面板 |
| auto_show | boolean | 是否自动展示播放控制面板 |
| show_buffering | enum(always/when_playing/never | 缓冲状态显示模式 |
| surface_type | enum(none/surface_view/texture_view) | None/SurfaceView/TextureView |
| player_layout_id | resources | 资源布局 |
| control_layout_id | resources | 资源布局 |
| resize_mode | enum | 视频尺寸显示模式 |


* 基本使用

```xml
<org.cz.media.player.library.AppCompatMediaPlayerView
        android:id="@+id/playerView"
        android:layout_width="match_parent"
        android:layout_height="320dp"
        android:background="@android:color/black"/>
```
* 加载视频(线上Url/本地文件/资产目录)

```
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

* 关于生命周期控制

以下是为了兼容 API 24的多窗口模式
官方介绍
Android API level 24 and higher supports multiple windows. 
As your app can be visible, but not active in split window mode, you need to initialize the player in onStart. 
Android API level 24 and lower requires you to wait as long as possible until you grab resources, so you wait until onResume before initializing the player.

```
override fun onStart() {
    super.onStart()
    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
        initializePlayerFromUrl()
    }
}

override fun onResume() {
    super.onResume()
    val player = playerView.player
    if(Build.VERSION.SDK_INT< Build.VERSION_CODES.N||null==player){
        initializePlayerFromUrl()
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

private fun releasePlayer() {
    playerView.player?.release()
}
```

* 单窗口模式

尽量推持视频初始化,避免影响视频创建.

```
override fun onResume() {
    super.onResume()
    val player = playerView.player
    if(null==player){
        initializePlayerFromUrl()
    }
}

override fun onPause() {
    super.onPause()
    releasePlayer();
}

private fun releasePlayer() {
    playerView.player?.release()
}
```

### 修改控制面板

```
<org.cz.media.player.base.MediaPlayerView
    android:id="@+id/playerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    //动态修改控制面板布局
    app:control_layout_id="@layout/media_player_custom_control_layout"/>

```

* 仅作局部属性修改

```
<?xml version="1.0" encoding="utf-8"?>
<org.cz.media.player.base.controller.MediaPlayerControlLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    //修改其中属性
    app:player_show_shuffle_button="true"
    app:player_repeat_toggle_modes="one"/>
```

* 动态修改布局
动态修改排版布局需要了解以下布局分布

![播放面板分层](https://github.com/momodae/LibraryResources/blob/master/ExoMediaPlayer/image/player_control_layout.png?raw=true)

布局内子控件主要使用:layout_control_panel="left/top/right/bottom/center" 声明布局摆放区域

```
//参考演示[style/MultipleStyleSampleActivity]
//布局[media_player_mini_control_layout]
<org.cz.media.player.base.controller.MediaPlayerControlLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!--customize the center layout-->
    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_control_panel="center">

        <ImageButton
            android:id="@id/playerPlayButton"
            android:layout_width="84dp"
            android:layout_height="84dp"
            style="?attr/playerPlayButton"
            android:layout_gravity="center"
            android:visibility="gone"
            android:background="@drawable/player_play_selector"/>

        <ImageButton
            android:id="@id/playerPauseButton"
            android:layout_width="84dp"
            android:layout_height="84dp"
            android:layout_gravity="center"
            style="?attr/playerPauseButton"
            android:visibility="gone"
            android:background="@drawable/player_play_selector"/>

    </FrameLayout>

    <!--clear the bottom layout-->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_control_panel="bottom">
        
        <!-- 进度条必须包括.无论放在哪个布局 -->
        <org.cz.media.player.base.timebar.DefaultTimeBar
            android:id="@id/playerTimeBar"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:paddingTop="24dp"
            android:paddingBottom="24dp"
            android:layout_weight="1"
            style="?playerTimeBar"/>

    </LinearLayout>

</org.cz.media.player.base.controller.MediaPlayerControlLayout>
```

* 关于预定义的样式

样式整体设计[文档](mediaPlayerStyle.md)


### 播放列表

根据列表初始化一个ConcatenatingMediaSource对象
参考演示[app/sample/list/MediaPlaylistActivity]

```
val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
val mediaSourceFactory = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
val concatenatingMediaSource = ConcatenatingMediaSource()

val adapter = recyclerView.adapter as PlaybackListAdapter
val videoList = adapter.videoList
for(videoItem in videoList){
    val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(Uri.parse(videoItem.url))
    concatenatingMediaSource.addMediaSource(mediaSource)
}

mediaPlayer.playWhenReady=true
mediaPlayer.prepare(concatenatingMediaSource,false,false)
```

数据状态同步

```
mediaPlayer.addListener(object : Player.EventListener{
    override fun onPositionDiscontinuity(reason: Int) {
        //Change the window index Dynamically
        if(Player.TIMELINE_CHANGE_REASON_PREPARED==reason||Player.TIMELINE_CHANGE_REASON_DYNAMIC==reason){
            adapter.setSelectPosition(mediaPlayer.currentWindowIndex)
        }
    }
})
```

### AppCompatMediaPlayer 
> 这是一个兼容扩展类.事实上.除了基础的 MediaPlayerView 之外的功能,都被示为扩展.主要是为了隔离业务与播放器.<br>
> 应强制使用 AppcompatMediaPlayerView.因为未来大量的兼容扩展工作,都会直接在此类进行.

* 当前本类职责
    * 对接播放器状态桢.(主要扩展网络,播放缓冲.其他播放状态,以及状态界面样式)
    * 分离了通用的异常提示信息.
    
> 根据业务.如果需要更多的状态页变化.根据状态桢的触发器自行控制.默认提供了二个作为基础的缓冲,网络变化的状态桢控制

* PlayerErrorTrigger 异常状态桢触发器
    主要负责当网络切换时的状态恢复工作,此处并不处理异常桢显示.因为视频播放涉及缓冲等情况的考虑.应该等待播放器自身触发异常事件.
    具体代码参考[PlayerEventTrigger](../library/src/main/java/org/cz/media/player/library/status/trigger/PlayerErrorTrigger.java)
    
* PlayerEventTrigger 播放事件状态桢触发器
    负责管理缓冲,异常等状态显示.当开始缓冲视频时.显示加载页.并屏蔽播放器播放操作.有异常产生时.显示异常页等.此处做了短时间多次触发缓冲的背压操作策略优化.
    具体代码参考[PlayerEventTrigger](../library/src/main/java/org/cz/media/player/library/status/trigger/PlayerEventTrigger.java)
    
* 状态桢管理模块请参考[文档](component/playerFrameWrapper.md)
    


### 浮层扩展见[文档](component/playerOverlay.md)

### 播放组件见[文档](component/playerComponent.md)

