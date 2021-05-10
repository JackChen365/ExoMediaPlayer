* NetworkReceiver

```
2020-09-18 17:19:21.089 1102-1102/org.cz.media.player.library.sample E/ActivityThread: Activity org.cz.media.player.library.sample.sample.scene.MultipleModeSampleActivity has leaked IntentReceiver org.cz.media.player.library.status.trigger.PlayerErrorTrigger$NetworkStatusReceiver@a0512d2 that was originally registered here. Are you missing a call to unregisterReceiver()?
    android.app.IntentReceiverLeaked: Activity org.cz.media.player.library.sample.sample.scene.MultipleModeSampleActivity has leaked IntentReceiver org.cz.media.player.library.status.trigger.PlayerErrorTrigger$NetworkStatusReceiver@a0512d2 that was originally registered here. Are you missing a call to unregisterReceiver()?
        at android.app.LoadedApk$ReceiverDispatcher.<init>(LoadedApk.java:1669)
        at android.app.LoadedApk.getReceiverDispatcher(LoadedApk.java:1446)
        at android.app.ContextImpl.registerReceiverInternal(ContextImpl.java:1614)
        at android.app.ContextImpl.registerReceiver(ContextImpl.java:1587)
        at android.app.ContextImpl.registerReceiver(ContextImpl.java:1575)
        at android.content.ContextWrapper.registerReceiver(ContextWrapper.java:627)
        at android.content.ContextWrapper.registerReceiver(ContextWrapper.java:627)
        at org.cz.media.player.library.status.trigger.PlayerErrorTrigger.<init>(PlayerErrorTrigger.java:33)
        at org.cz.media.player.library.AppCompatMediaPlayerView.onGenerateSurfaceView(AppCompatMediaPlayerView.java:94)
        ...
```


* 关于外围控制播放面板显示与隐藏的问题

因为扩展过多,导致控制的显示状态无法简单的根据某个状态同步,始终会受到,如桢状态触发器.或者异常逻辑引起的动态关闭播放控制面板操作.

如下面的代码片断[PlayerEventTrigger] 在视频加载前,就会触发缓冲状态.此时正常逻辑应该屏蔽播放面板.禁用播放面板操作.<br>
但是此操作始终会触发隐藏面板操作.所以此处应该根据具体逻辑.选择何时隐藏.

多种扩展会使面板的显示与隐藏很难统一.所以外围扩展控制,需要考虑清楚操作对内部的影响.

```
if(showBufferingSpinner){
    //When the MediaPlayer starts to prepare it will always be buffering.
    //So if here we hide the control layout. that will make the show_auto invalid.
    long currentPosition = player.getCurrentPosition();
    if(0 != currentPosition){
        playerView.hideController();
        playerView.setControllerEnable(false);
    }
    //change the frame.
    trigger(PlayerViewWrapper.FRAME_BUFFERING);
} else {
    @Nullable ExoPlaybackException error = null != player ? player.getPlaybackError() : null;
    if(null != error){
        playerView.hideController();
        playerView.setControllerEnable(false);
        trigger(PlayerViewWrapper.FRAME_ERROR);
    } else {
        playerView.setControllerEnable(true);
        if(playerView.shouldShowControllerIndefinitely()){
            playerView.showController();
        }
        trigger(PlayerViewWrapper.FRAME_CONTAINER);
    }
}
```

* SurfaceView 与 TextureView 不同

使用 SurfaceView会有默认的黑色背景一闪而过.视加载速度而定.TextureView 性能略低,但兼容 API24以下的动画等属性.性能对比见下表

<table>
  <thead>
    <tr>
      <th>&nbsp;</th>
      <th style="text-align: center">MediaPlayer</th>
      <th style="text-align: left">ExoPlayer</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>SurfaceView 1080p</td>
      <td style="text-align: center">202 mAh</td>
      <td style="text-align: left">214 mAh</td>
    </tr>
    <tr>
      <td>TextureView 1080p</td>
      <td style="text-align: center">219 mAh</td>
      <td style="text-align: left">221 mAh</td>
    </tr>
    <tr>
      <td>SurfaceView 480p</td>
      <td style="text-align: center">194 mAh</td>
      <td style="text-align: left">207 mAh</td>
    </tr>
    <tr>
      <td>TextureView 480p</td>
      <td style="text-align: center">212 mAh</td>
      <td style="text-align: left">215 mAh</td>
    </tr>
  </tbody>
</table>