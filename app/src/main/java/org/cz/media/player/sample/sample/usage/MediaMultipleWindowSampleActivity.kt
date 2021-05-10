package org.cz.media.player.sample.sample.usage

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_usage_media_multiple_window_sample.*
import org.cz.media.player.library.extension.component.CompanionPlayButtonComponent
import org.cz.media.player.library.group.MediaPlayerGroup
import org.cz.media.player.sample.R
import org.cz.media.player.sample.sample.scene.MultipleModeSampleActivity


/**
 * This is a multiple-window usage.
 * We have to different player view. Click the button:playerModeButton change to another player.
 */
@Register(title="多窗口模式",desc = "演示从一个播放器容器切换到另一个播放器")
class MediaMultipleWindowSampleActivity : AppCompatActivity() {
    private val mediaPlayerGroup = MediaPlayerGroup()
    private var eventListener= MultipleModeSampleActivity.OnPlaybackStatusChangeListener()
    private var playWhenReady=false
    private var currentWindowIndex=0
    private var currentPosition=0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_media_multiple_window_sample)
    }

    private fun initializePlayer() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView1.player = mediaPlayer
        playerView1.registerPlayerComponent(CompanionPlayButtonComponent())
        //1. Uri from url.
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(
            Uri.parse("asset:///sample media.mp4"))
        mediaPlayer.playWhenReady=playWhenReady
        mediaPlayer.seekTo(currentWindowIndex,currentPosition)
        mediaPlayer.addListener(eventListener)
        mediaPlayer.prepare(mediaSource)

        mediaPlayerGroup.setOnMediaPlayerViewInitialListener { playerView->
            val playerModeButton = playerView.findViewById<View>(R.id.playerModeButton)
            playerModeButton.setOnClickListener {
                mediaPlayerGroup.next()
            }
            if(R.id.playerView2 == playerView.id){
                val playerSpeedText = playerView.findViewById<TextView>(R.id.playerSpeedText)
                playerSpeedText.text="1.25x"
                val playerRevolutionText = playerView.findViewById<TextView>(R.id.playerRevolutionText)
                playerRevolutionText.text="高清"
            }
        }
        mediaPlayerGroup.attachToHost(frameLayout)
        mediaPlayerGroup.show(R.id.playerView1)

    }

    override fun onResume() {
        super.onResume()
        if(null==playerView1.player){
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onBackPressed() {
        val currentMediaPlayer = mediaPlayerGroup.currentMediaPlayer
        if(currentMediaPlayer.id==R.id.playerView2){
            mediaPlayerGroup.next()
        } else {
            super.onBackPressed()
        }
    }

    private fun releasePlayer() {
        val currentMediaPlayer = mediaPlayerGroup.currentMediaPlayer
        if(null!=currentMediaPlayer){
            val mediaPlayer = currentMediaPlayer.player
            if(null!=mediaPlayer){
                playWhenReady=mediaPlayer.playWhenReady
                currentWindowIndex=mediaPlayer.currentWindowIndex
                currentPosition=mediaPlayer.currentPosition
                mediaPlayer.removeListener(eventListener)
                mediaPlayer.release()
            }
        }
    }
}