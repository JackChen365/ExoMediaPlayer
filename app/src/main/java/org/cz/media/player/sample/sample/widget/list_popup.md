## 全局样式定制

> 演示定制全局样式

#### 布局预览

```
<org.cz.media.player.library.AppCompatMediaPlayerView
    android:id="@+id/playerView"
    android:layout_width="match_parent"
    android:layout_height="360dp"
    android:background="@android:color/black"
    app:control_layout_id="@layout/media_player_speed_control_layout"/>

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

### 定制的 Control_layout

```
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

            <ImageButton
                android:id="@id/playerRewindButton"
                style="?attr/playerRewindButton"/>

            <ImageButton
                android:id="@id/playerPlayButton"
                style="@style/MediaButton.Play"/>

            <ImageButton
                android:id="@id/playerPauseButton"
                style="?attr/playerPauseButton"/>

            <!-- 新增此控件-->
            <ImageButton
                android:id="@+id/playerSpeedButton"
                style="@style/MediaButton"
                android:src="@mipmap/ic_speed"/>

        </LinearLayout>
    </LinearLayout>
</org.cz.media.player.base.controller.MediaPlayerControlLayout>
```

### 创建一个Component

```
public class SpeedChangeComponent extends PlayerComponent {
    private List<String> listData=new ArrayList<>();

    public SpeedChangeComponent(List<String> listData) {
        if(null!=listData){
            this.listData.addAll(listData);
        }
    }

    /**
     * Return the component's name. Here we use 'speed'.
     * @return
     */
    @Override
    public String getComponentName() {
        return "Speed";
    }


    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
        //We don't have to update the component.
    }

    /**
     * When you execute the component.It display a popupWindow.
     * And you could change the player's speed.
     */
    @Override
    public void execute() {
        MediaPlayerViewHolder viewHolder = getViewHolder();
        View playerSpeedButton=viewHolder.itemView.findViewById(R.id.playerSpeedButton);
        if(null!=playerSpeedButton){
            Context context = playerSpeedButton.getContext();
            ListPopupWindow listPopupWindow = new ListPopupWindow(context);
            listPopupWindow.setItemData(listData);
            listPopupWindow.setFocusable(true);
            listPopupWindow.setAnchorView(playerSpeedButton);
            listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
                Player player = getPlayer();
                if(null!=player){
                    float speed=1f;
                    try {
                        speed=Float.parseFloat(listData.get(position));
                    } catch (Exception e){
                    }
                    PlaybackParameters playbackParameters = new PlaybackParameters(speed);
                    player.setPlaybackParameters(playbackParameters);
                }
            });
            listPopupWindow.show();
        }
    }

    /**
     * Create the component's view.
     * The view with the id:playerSpeedButton is where you define in your layout resources.
     * @param context
     * @param viewHolder
     */
    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
        View playerSpeedButton=viewHolder.itemView.findViewById(R.id.playerSpeedButton);
        if(null!=playerSpeedButton){
            playerSpeedButton.setOnClickListener(this);
        }
    }
}
```

注册此 Component

```
val speedLevelList= listOf("0.50","0.75","Normal","1.25","1.50","1.75")
playerView.registerPlayerComponent(SpeedChangeComponent(speedLevelList))
```

注意,这是一个通用组件.可以复用.当然需要确保布局内有声明的控件.

