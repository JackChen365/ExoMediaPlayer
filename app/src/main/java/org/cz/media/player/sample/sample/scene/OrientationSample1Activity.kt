package org.cz.media.player.sample.sample.scene

import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_orientation_sample1.*
import org.cz.media.player.sample.R
import org.cz.media.player.sample.extention.component.FillScreenComponent

/**
 * @author Created by cz
 * @date 2020/8/6 9:54 AM
 * @email binigo110@126.com
 *
 * This example teaches you how to change the orientation of the Activity.
 * For this one it won't re-create the activity.
 *
 */
@SampleSourceCode
@SampleDocument("orientation1.md")
@Register(title="屏幕方向切换1",desc = "手动切换方向不重建 Activity")
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
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //First Hide other objects (listview or recyclerview), better hide them using Gone.
            val params = playerView.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            playerView.requestLayout()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //unhide your objects here.
            val params = playerView.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            playerView.requestLayout()
        }
    }

    private fun initializePlayer() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.registerPlayerComponent(FillScreenComponent(this, R.id.playerFillScreenButton))
        playerView.player = mediaPlayer
        //Uri from assets.
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse("asset:///sample media.mp4"))
        mediaPlayer.playWhenReady=playWhenReady
        mediaPlayer.seekTo(currentWindowIndex,currentPosition)
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