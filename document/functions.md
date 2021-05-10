## Functions

1. 关于屏幕常亮

app/Basic/常规使用1
```
在Window设置flag
getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
这种方式不需要申请权限，也是官方推荐的做法
方式三：在界面布局xml中顶层添加属性
可以再界面xml文件中的顶层布局添加属性即可：
android:keepScreenOn="true"
```


2. 关于中途退出暂停播放,回来继续的逻辑.

app/Basic/常规使用2
```
override fun onResume() {
        super.onResume()
        val player = playerView.player
        if(null==player){
            initializePlayer()
        } else if(isPlayerPaused){
            player.playWhenReady = true
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    private fun pausePlayer() {
        val player = playerView.player
        if(null!=player&&player.isPlaying){
            isPlayerPaused = true
            player.playWhenReady = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerView.player?.release()
    }

```

3. 关于熄屏后再次进入界面无响应的问题
因为熄屏后释放了资源,再次进入时.检测到player不为空,但MediaSource已经释放,所以无响应.代码需要手动将Player释放,或者参考功能2,作中途退出暂停状态.

app/scene/屏幕方向切换1

```
class OrientationSample1Activity : AppCompatActivity() {
    private var playWhenReady=true
    private var currentWindowIndex=0
    private var currentPosition=0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orientation_sample1)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checking the orientation of the screen
        [...]
    }

    private fun initializePlayer() {
        [...]
        mediaPlayer.playWhenReady=playWhenReady
        mediaPlayer.seekTo(currentWindowIndex,currentPosition)
        [...]
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
        val player = playerView.player
        if(null!=player){
            playWhenReady=player.playWhenReady
            currentWindowIndex=player.currentWindowIndex
            currentPosition=player.currentPosition
            player.release()
            playerView.player = null
        }
    }
}
```

4. 关于开屏后因为尺寸问题重新调整导致的画面调整问题

该问题是因为视频有一个尺寸加载过程,而在这个过程中,如果视频加载过快,界面尺寸调整与视频画面渲染同时进行.导致的画面从大图到具体尺寸的调整问题.

    4.1 解决方案为:仅在视频尺寸调整完之后再设置自动加载

```
mediaPlayer.addVideoListener(object : VideoListener {
    override fun onSurfaceSizeChanged(width: Int, height: Int) {
        if(playWhenReady){
            mediaPlayer.playWhenReady = playWhenReady
        }
    }
})
```
    4.2 提前设置视频尺寸比例
    
    
5. 关于开屏,或者重新进入时.在部分手机上播放视频框会停留较长一段时间的问题

原因是因为在低版本手机上使用SurfaceView,window并不随Activity走导致的销毁问题.是SurfaceView引起的体验问题

如果感觉体验比较差,可以替换为使用textureView.但性能会下降30%左右...

