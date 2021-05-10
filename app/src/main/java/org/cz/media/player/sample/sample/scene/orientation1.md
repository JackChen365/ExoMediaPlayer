## 切换屏幕方向

> 演示动态切换屏幕方向(不重建 Activity)

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

> 根据方向切换.动态更改 PlayerView 尺寸. 

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

#### 屏幕切换组件扩展.

```
public class FillScreenComponent extends PlayerComponent {
    private Activity activity;

    public FillScreenComponent(Activity activity) {
        this.activity = activity;
    }

    @Override
    public String getComponentName() {
        return "FillScreen";
    }

    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
        //根据方向切换控件状态
        View view=viewHolder.itemView.findViewById(R.id.playerFillScreenButton);
        if(null!=view){
            int requestedOrientation = activity.getRequestedOrientation();
            if(requestedOrientation== ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED||
                    requestedOrientation== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                view.setBackgroundResource(R.mipmap.ic_fullscreen_enter);
                view.setContentDescription(activity.getString(R.string.fill_screen));
            } else if(requestedOrientation== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                view.setBackgroundResource(R.mipmap.ic_fullscreen_exit);
                view.setContentDescription(activity.getString(R.string.normal_screen));
            }
        }
    }

    @Override
    public void execute() {
        //点击或者调用组件功能时.切换屏幕方向
        int requestedOrientation = activity.getRequestedOrientation();
        if(requestedOrientation== ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED||
                requestedOrientation== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if(requestedOrientation== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        MediaPlayerViewHolder viewHolder = getViewHolder();
        View view=viewHolder.itemView.findViewById(R.id.playerFillScreenButton);
        if(null!=view){
            update(viewHolder);
        }
    }

    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
        //关联控件与组件功能.注意此控件是上面自定义面板布局内新增的控件.
        View playerFillScreenButton=viewHolder.itemView.findViewById(R.id.playerFillScreenButton);
        if(null!=playerFillScreenButton){
            playerFillScreenButton.setOnClickListener(this);
        }
    }

    @Override
    public void onDetachFromPlayerControlLayout(MediaPlayerControlLayout controlLayout) {
        super.onDetachFromPlayerControlLayout(controlLayout);
        activity=null;
    }
}


```