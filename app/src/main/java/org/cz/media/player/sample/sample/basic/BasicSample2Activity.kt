package org.cz.media.player.sample.sample.basic

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.component.document.SampleDocument
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_media_player_view.playerView
import org.cz.media.player.sample.R

/**
 * @author Created by cz
 * @date 2020/8/5 9:54 AM
 * @email binigo110@126.com
 *
 * Demonstrate how to customize the MediaPlayerControl layout.
 */
@SampleDocument("basic2.md")
@Register(title="常规使用2",desc = "演示常规控制面板按钮操作等")
class BasicSample2Activity : AppCompatActivity() {
    private var isPlayerPaused=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player_view)
    }

    private fun initializePlayer() {
        hideSystemUi()
        //Uri from assets
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
//        playerView.registerPlayerComponent(PlayerTimeBarComponent())
        mediaPlayer.playWhenReady = true
        playerView.player = mediaPlayer
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
            .createMediaSource(Uri.parse("asset:///sample media.mp4"))
        mediaPlayer.prepare(mediaSource,false,false)
    }

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


    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }


    override fun onDestroy() {
        super.onDestroy()
        playerView.player?.release()
    }


}