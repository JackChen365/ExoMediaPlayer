## Research

Start Data: 2020/7/27.
Author:Jack chen.

### About Threading

```
A note on threading
ExoPlayer instances must be accessed from a single application thread. For the vast majority of cases this should be the application’s main thread. Using the application’s main thread is a requirement when using ExoPlayer’s UI components or the IMA extension.

The thread on which an ExoPlayer instance must be accessed can be explicitly specified by passing a Looper when creating the player. If no Looper is specified, then the Looper of the thread that the player is created on is used, or if that thread does not have a Looper, the Looper of the application’s main thread is used. In all cases the Looper of the thread from which the player must be accessed can be queried using Player.getApplicationLooper.

If you see “Player is accessed on the wrong thread” warnings, some code in your app is accessing a SimpleExoPlayer instance on the wrong thread (the logged stack trace shows you where!). This is not safe and may result in unexpected or obscure errors.

For more information about ExoPlayer’s treading model, see the “Threading model” section of the ExoPlayer Javadoc.
```

Take a look at [Threading model](https://exoplayer.dev/doc/reference/com/google/android/exoplayer2/ExoPlayer.html#Threading%20model)


### The differences between TextureView and SurfaceView

```
If the view is for regular video playback then surface_view or texture_view should be used. SurfaceView has a number of benefits over TextureView for video playback:

Significantly lower power consumption on many devices.
More accurate frame timing, resulting in smoother video playback.
Support for secure output when playing DRM protected content.
The ability to render video content at the full resolution of the display on Android TV devices that upscale the UI layer.

SurfaceView should therefore be preferred over TextureView where possible. TextureView should be used only if SurfaceView does not meet your needs. One example is where smooth animations or scrolling of the video surface is required prior to Android N, as described below. For this case, it’s preferable to use TextureView only when SDK_INT is less than 24 (Android N) and SurfaceView otherwise.
```

Take a look at [ui-components](https://exoplayer.dev/ui-components.html#Choosing%20a%20surface%20type)


### About android 10 read permission denied.

```
2020-07-29 14:47:48.475 6295-6414/org.cz.media.player.library.sample E/ExoPlayerImplInternal: Source error
      com.google.android.exoplayer2.upstream.FileDataSource$FileDataSourceException: com.google.android.exoplayer2.upstream.FileDataSource$FileDataSourceException: java.io.FileNotFoundException: /storage/emulated/0/12530/download/Salt-Ava Max.mp3: open failed: EACCES (Permission denied)
        at com.google.android.exoplayer2.upstream.FileDataSource.open(FileDataSource.java:97)
        at com.google.android.exoplayer2.upstream.DefaultDataSource.open(DefaultDataSource.java:177)
        at com.google.android.exoplayer2.upstream.StatsDataSource.open(StatsDataSource.java:83)
        at com.google.android.exoplayer2.source.ProgressiveMediaPeriod$ExtractingLoadable.load(ProgressiveMediaPeriod.java:962)
        at com.google.android.exoplayer2.upstream.Loader$LoadTask.run(Loader.java:415)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
        at java.lang.Thread.run(Thread.java:929)
     Caused by: com.google.android.exoplayer2.upstream.FileDataSource$FileDataSourceException: java.io.FileNotFoundException: /storage/emulated/0/12530/download/Salt-Ava Max.mp3: open failed: EACCES (Permission denied)
        at com.google.android.exoplayer2.upstream.FileDataSource.openLocalFile(FileDataSource.java:119)
        at com.google.android.exoplayer2.upstream.FileDataSource.open(FileDataSource.java:88)
        at com.google.android.exoplayer2.upstream.DefaultDataSource.open(DefaultDataSource.java:177) 
        at com.google.android.exoplayer2.upstream.StatsDataSource.open(StatsDataSource.java:83) 
        at com.google.android.exoplayer2.source.ProgressiveMediaPeriod$ExtractingLoadable.load(ProgressiveMediaPeriod.java:962) 
        at com.google.android.exoplayer2.upstream.Loader$LoadTask.run(Loader.java:415) 
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167) 
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641) 
        at java.lang.Thread.run(Thread.java:929) 
     Caused by: java.io.FileNotFoundException: /storage/emulated/0/12530/download/Salt-Ava Max.mp3: open failed: EACCES (Permission denied)
        at libcore.io.IoBridge.open(IoBridge.java:496)
        at java.io.RandomAccessFile.<init>(RandomAccessFile.java:289)
        at java.io.RandomAccessFile.<init>(RandomAccessFile.java:152)
        at com.google.android.exoplayer2.upstream.FileDataSource.openLocalFile(FileDataSource.java:108)
        at com.google.android.exoplayer2.upstream.FileDataSource.open(FileDataSource.java:88) 
        at com.google.android.exoplayer2.upstream.DefaultDataSource.open(DefaultDataSource.java:177) 
        at com.google.android.exoplayer2.upstream.StatsDataSource.open(StatsDataSource.java:83) 
        at com.google.android.exoplayer2.source.ProgressiveMediaPeriod$ExtractingLoadable.load(ProgressiveMediaPeriod.java:962) 
        at com.google.android.exoplayer2.upstream.Loader$LoadTask.run(Loader.java:415) 
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167) 
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641) 
        at java.lang.Thread.run(Thread.java:929) 
     Caused by: android.system.ErrnoException: open failed: EACCES (Permission denied)
        at libcore.io.Linux.open(Native Method)
        at libcore.io.ForwardingOs.open(ForwardingOs.java:167)
        at libcore.io.BlockGuardOs.open(BlockGuardOs.java:252)
        at libcore.io.ForwardingOs.open(ForwardingOs.java:167)
        at android.app.ActivityThread$AndroidOs.open(ActivityThread.java:8198)
        at libcore.io.IoBridge.open(IoBridge.java:482)
        at java.io.RandomAccessFile.<init>(RandomAccessFile.java:289) 
        at java.io.RandomAccessFile.<init>(RandomAccessFile.java:152) 
        at com.google.android.exoplayer2.upstream.FileDataSource.openLocalFile(FileDataSource.java:108) 
        at com.google.android.exoplayer2.upstream.FileDataSource.open(FileDataSource.java:88) 
        at com.google.android.exoplayer2.upstream.DefaultDataSource.open(DefaultDataSource.java:177) 
        at com.google.android.exoplayer2.upstream.StatsDataSource.open(StatsDataSource.java:83) 
        at com.google.android.exoplayer2.source.ProgressiveMediaPeriod$ExtractingLoadable.load(ProgressiveMediaPeriod.java:962) 
        at com.google.android.exoplayer2.upstream.Loader$LoadTask.run(Loader.java:415) 
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167) 
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641) 
        at java.lang.Thread.run(Thread.java:929) 
```

```
<manifest ... >
  <application android:requestLegacyExternalStorage="true" ... >
    ...
  </application>
</manifest>
```

* About subtitle

```
2020-07-31 15:07:24.029 20940-21215/org.cz.media.player.library.sample W/SubripDecoder: Skipping invalid index: Let’s count out the points
2020-07-31 15:07:24.030 20940-21215/org.cz.media.player.library.sample W/SubripDecoder: Skipping invalid timing: 00:04:23.1 --> 00:04:24.62
2020-07-31 15:07:24.030 20940-21215/org.cz.media.player.library.sample W/SubripDecoder: Skipping invalid index: And the winner is….
2020-07-31 15:07:24.030 20940-21215/org.cz.media.player.library.sample W/SubripDecoder: Skipping invalid timing: 00:04:24.62 --> 00:04:27.64
2020-07-31 15:07:24.030 20940-21215/org.cz.media.player.library.sample W/SubripDecoder: Skipping invalid index: Sarah with 31 points
2020-07-31 15:07:24.030 20940-21215/org.cz.media.player.library.sample W/SubripDecoder: Skipping invalid timing: 00:04:27.64 --> 00:04:30.92
2020-07-31 15:07:24.030 20940-21215/org.cz.media.player.library.sample W/SubripDecoder: Skipping invalid index: Well done! Here’s what you’ve won!
2020-07-31 15:07:24.031 20940-21215/org.cz.media.player.library.sample W/SubripDecoder: Skipping invalid timing: 00:04:30.92 --> 00:04:33.5

```


Because of the regex pattern

```
private static final String SUBRIP_TIMECODE = "(?:(\\d+):)?(\\d+):(\\d+)(?:,(\\d+))?";
    private static final Pattern SUBRIP_TIMING_LINE =
            Pattern.compile("\\s*(" + SUBRIP_TIMECODE + ")\\s*-->\\s*(" + SUBRIP_TIMECODE + ")\\s*");


when we are trying to match the file like this:
00:04:23.1 --> 00:04:24.62

It guarded this as an invalid timing.
```
[android-10-open-failed-eacces-permission-denied](https://medium.com/@sriramaripirala/android-10-open-failed-eacces-permission-denied-da8b630a89df)

### References

* [ExoPlayer supported-devices](https://exoplayer.dev/supported-devices.html)
* [ExoPlayer supported-formats](https://exoplayer.dev/supported-formats.html)
* [load-error-handling-in-exoplayer](https://medium.com/google-exoplayer/load-error-handling-in-exoplayer-488ab6908137)
* [CodeLab play streaming use Exoplayer](https://codelabs.developers.google.com/codelabs/exoplayer-intro/#0)
* [Default formats that Android deconder supports](https://developer.android.com/guide/topics/media/media-formats#core)
* [playing-video-by-exoplayer](https://medium.com/fungjai/playing-video-by-exoplayer-b97903be0b33)
* [troubleshooting](https://exoplayer.dev/troubleshooting.html)

About subtitle
* [Showing Subtitles/Lyrics on Android Exoplayer](https://medium.com/@codenextgen/showing-subtitles-lyrics-on-android-exoplayer-98a88a7b53b2)

About mp3
* [mp3](http://mpgedit.org/mpgedit/mp3.pdf)

* [jmf2_0-guide](http://wwwinfo.deis.unical.it/fortino/teaching/gdmi0708/materiale/jmf2_0-guide.pdf)



[android-exoplayer-starters-guide](https://android.jlelse.eu/android-exoplayer-starters-guide-6350433f256c)