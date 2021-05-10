## 播放列表

> 演示播放媒体列表,主要知识点:

* 播放列表的初始化.
* 播放条目变化事件同步

#### 布局预览

```
<org.cz.media.player.base.MediaPlayerView
    android:id="@+id/playerView"
    android:layout_width="match_parent"
    android:layout_height="300dp" />
```

#### 代码初始化

```
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_media_playlist)
    recyclerView.layoutManager=GridLayoutManager(this,5)
    val videoListAdapter = PlaybackListAdapter(buildVideoList())
    videoListAdapter.setOnItemClickListener { v, position ->
        player?.seekToDefaultPosition(position)
    }
    recyclerView.adapter=videoListAdapter
    recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
}


private fun initializePlayer() {
    val mediaPlayer = SimpleExoPlayer.Builder(this).build()
    player=mediaPlayer
    playerView.setPlayer(mediaPlayer)

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
    mediaPlayer.addVideoListener(object :VideoListener{

    })
    mediaPlayer.seekToDefaultPosition()
    mediaPlayer.addListener(object : Player.EventListener{
        override fun onPositionDiscontinuity(reason: Int) {
            //Change the window index Dynamically
            if(Player.TIMELINE_CHANGE_REASON_PREPARED==reason||Player.TIMELINE_CHANGE_REASON_DYNAMIC==reason){
                adapter.setSelectPosition(mediaPlayer.currentWindowIndex)
            }
        }
    })
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
//添加播放事件,监听 window 变化,同步到数据适配器列表变化
mediaPlayer.addListener(object : Player.EventListener{
    override fun onPositionDiscontinuity(reason: Int) {
        //Change the window index Dynamically
        if(Player.TIMELINE_CHANGE_REASON_PREPARED==reason||Player.TIMELINE_CHANGE_REASON_DYNAMIC==reason){
            adapter.setSelectPosition(mediaPlayer.currentWindowIndex)
        }
    }
})

public class PlaybackListAdapter {
    [...]
    public void setSelectPosition(int position){
        if(currentSelectPosition!=position){
            int oldPosition=currentSelectPosition;
            currentSelectPosition=position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(position);
        }
    }
    [...]
}

```