package org.cz.media.player.sample.sample.usage

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_usage_default_sample.*
import org.cz.media.player.library.extension.component.CompanionPlayButtonComponent
import org.cz.media.player.sample.R

/**
 * This is a default app usage.
 * It comes with a center button and the blue style.
 */
@Register(title="默认播放",desc = "演示常规的项目音频播放")
class DefaultSampleActivity : AppCompatActivity() {
    private var playWhenReady=true
    private var currentWindowIndex=0
    private var currentPosition=0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_default_sample)
    }

    private fun initializePlayerFromAssets() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        val playerModeButton=playerView.findViewById<View>(R.id.playerModeButton)
        playerModeButton.visibility=View.GONE
        playerView.registerPlayerComponent(CompanionPlayButtonComponent())
        playerView.player = mediaPlayer
        //Uri from assets.
        val url="http://vfx.mtime.cn/Video/2019/03/13/mp4/190313094901111138.mp4";
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(url))
        mediaPlayer.playWhenReady=playWhenReady
        mediaPlayer.seekTo(currentWindowIndex,currentPosition)
        mediaPlayer.prepare(mediaSource,false,false)
    }

    override fun onStart() {
        super.onStart()
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
            initializePlayerFromAssets()
        }
    }

    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.N||null==playerView.player){
            initializePlayerFromAssets()
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
        val player = playerView.player
        if(null!=player){
            playWhenReady=player.playWhenReady
            currentWindowIndex=player.currentWindowIndex
            currentPosition=player.currentPosition
            player.release()
        }
        playerView.player = null
    }

}