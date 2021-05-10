## Performance

The consumption of initialize and release resources in our phone.

```
2020-07-30 14:56:32.385 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:72
2020-07-30 14:56:41.036 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:49 layoutPosition:0
2020-07-30 14:56:41.064 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:10
2020-07-30 14:56:44.071 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:87 layoutPosition:1
2020-07-30 14:56:44.079 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:8
2020-07-30 14:56:46.066 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:0 layoutPosition:1
2020-07-30 14:56:46.421 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:55 layoutPosition:2
2020-07-30 14:56:46.441 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:7
2020-07-30 14:56:49.414 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:58 layoutPosition:3
2020-07-30 14:56:49.438 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:7
2020-07-30 14:56:51.013 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:57 layoutPosition:4
2020-07-30 14:56:51.038 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:11
2020-07-30 14:56:53.145 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:63 layoutPosition:5
2020-07-30 14:56:53.159 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:8
2020-07-30 14:56:55.164 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:45 layoutPosition:6
2020-07-30 14:56:55.179 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:7
2020-07-30 14:56:56.694 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:70 layoutPosition:7
2020-07-30 14:56:56.721 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:7
2020-07-30 14:56:58.175 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:71 layoutPosition:6
2020-07-30 14:56:58.188 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:8
2020-07-30 14:56:59.249 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:53 layoutPosition:5
2020-07-30 14:56:59.275 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:12
2020-07-30 14:57:00.268 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:75 layoutPosition:4
2020-07-30 14:57:00.301 22006-22006/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:14
```

The exoplayer decoding the video metadata.

```
I/VideoListSampleActivity: time:818 currentVisiblePosition:0 width:1000 height:562 unappliedRotationDegrees:0 pixelWidthHeightRatio:1.0
I/VideoListSampleActivity: time:484 currentVisiblePosition:1 width:1000 height:562 unappliedRotationDegrees:0 pixelWidthHeightRatio:0.9991111
I/VideoListSampleActivity: time:395 currentVisiblePosition:2 width:1000 height:416 unappliedRotationDegrees:0 pixelWidthHeightRatio:1.0009023
I/VideoListSampleActivity: time:375 currentVisiblePosition:3 width:1000 height:562 unappliedRotationDegrees:0 pixelWidthHeightRatio:1.0
I/VideoListSampleActivity: time:328 currentVisiblePosition:4 width:1000 height:562 unappliedRotationDegrees:0 pixelWidthHeightRatio:1.0
I/VideoListSampleActivity: time:421 currentVisiblePosition:5 width:1000 height:562 unappliedRotationDegrees:0 pixelWidthHeightRatio:0.9991111
I/VideoListSampleActivity: time:375 currentVisiblePosition:6 width:1000 height:562 unappliedRotationDegrees:0 pixelWidthHeightRatio:1.0
I/VideoListSampleActivity: time:290 currentVisiblePosition:7 width:1000 height:562 unappliedRotationDegrees:0 pixelWidthHeightRatio:1.0
I/VideoListSampleActivity: time:352 currentVisiblePosition:8 width:1000 height:562 unappliedRotationDegrees:0 pixelWidthHeightRatio:1.0
I/VideoListSampleActivity: time:344 currentVisiblePosition:9 width:1000 height:562 unappliedRotationDegrees:0 pixelWidthHeightRatio:0.9991111



In other phone which is more faster than our pad.
2020-07-30 14:58:28.090 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:45 layoutPosition:0
2020-07-30 14:58:28.837 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:4
2020-07-30 14:58:35.684 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:50 layoutPosition:0
2020-07-30 14:58:35.688 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:4
2020-07-30 14:58:37.251 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:0 layoutPosition:0
2020-07-30 14:58:39.818 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:48 layoutPosition:1
2020-07-30 14:58:39.974 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:4
2020-07-30 14:58:42.605 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:0 layoutPosition:2
2020-07-30 14:58:42.895 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:46 layoutPosition:3
2020-07-30 14:58:42.898 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:3
2020-07-30 14:58:44.172 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:0 layoutPosition:3
2020-07-30 14:58:44.471 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:55 layoutPosition:4
2020-07-30 14:58:44.475 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:4
2020-07-30 14:58:46.673 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:0 layoutPosition:4
2020-07-30 14:58:46.909 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:47 layoutPosition:5
2020-07-30 14:58:46.912 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:3
2020-07-30 14:58:50.976 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:0 layoutPosition:5
2020-07-30 14:58:57.456 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:0 layoutPosition:9
2020-07-30 14:58:57.626 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:48 layoutPosition:6
2020-07-30 14:58:57.630 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:4
2020-07-30 14:58:59.869 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:0 layoutPosition:8
2020-07-30 14:59:00.042 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:62 layoutPosition:5
2020-07-30 14:59:00.046 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:4
2020-07-30 14:59:14.452 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:0 layoutPosition:7
2020-07-30 14:59:14.640 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: releasePlayer:66 layoutPosition:4
2020-07-30 14:59:14.645 21625-21625/org.cz.media.player.library.sample I/VideoListSampleActivity: initializePlayer:4

```