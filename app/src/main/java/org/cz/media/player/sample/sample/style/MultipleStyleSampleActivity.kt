package org.cz.media.player.sample.sample.style

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_multiple_style_sample.*
import org.cz.media.player.base.MediaPlayerView
import org.cz.media.player.library.group.MediaPlayerGroup
import org.cz.media.player.sample.R

/**
 * @author Created by cz
 * @date 2020/8/6 9:54 AM
 * @email binigo110@126.com
 *
 * This example demonstrates two PlayerView has different styles and as a group.
 * You could be able to switch from one player to another.
 *
 */
@SampleSourceCode
@SampleDocument("multiple_style.md")
@Register(title="排版切换",desc = "演示切换不同样式,以及排版的Player控件")
class MultipleStyleSampleActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ExoplayerSample3"
    }
    private var currentPlayerView: MediaPlayerView?=null;
    private var player: SimpleExoPlayer?=null
    private var playWhenReady=true
    private var currentWindowIndex=0
    private var currentPosition=0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_style_sample)
        currentPlayerView=playerView1
    }

    private fun initializePlayer() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        player=mediaPlayer
        playerView1.player = mediaPlayer
        //1. Uri from url.
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
            .createMediaSource(Uri.parse("asset:///sample media.mp4"))
        mediaPlayer.playWhenReady=playWhenReady
        mediaPlayer.seekTo(currentWindowIndex,currentPosition)
        mediaPlayer.prepare(mediaSource)

        val mediaPlayerGroup = MediaPlayerGroup()
        mediaPlayerGroup.attachToHost(frameLayout)
        mediaPlayerGroup.show(R.id.playerView1)

        nextButton.setOnClickListener {
            mediaPlayerGroup.next()
        }
    }

    override fun onStart() {
        super.onStart()
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.N||null==player){
            initializePlayer()
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
        val mediaPlayer=player
        if(null!=mediaPlayer){
            playWhenReady=mediaPlayer.playWhenReady
            currentWindowIndex=mediaPlayer.currentWindowIndex
            currentPosition=mediaPlayer.currentPosition
            mediaPlayer.release()
            player=null
        }
    }

}