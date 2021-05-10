package org.cz.media.player.sample.sample.scene

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.android.sample.library.component.message.SampleMessage
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.video.VideoListener
import kotlinx.android.synthetic.main.activity_orientation_sample2.*
import org.cz.media.player.sample.R
import org.cz.media.player.sample.extention.component.FillScreenComponent

/**
 * @author Created by cz
 * @date 2020/8/6 9:54 AM
 * @email binigo110@126.com
 *
 * This example teaches you how to change the orientation of the Activity.
 * For this one it will re-create the activity. And it has the different layout on land scape mode.
 *
 */
@SampleMessage
@SampleDocument("orientation2.md")
@Register(title="屏幕方法切换2",desc = "手动切换方向并重建 Activity")
class OrientationSample2Activity : AppCompatActivity() {
    private var playWhenReady=true
    private var currentWindowIndex=0
    private var currentPosition=0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orientation_sample2)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checking the orientation of the screen
        if (newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE) {
            val params = playerView.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            playerView.requestLayout()
        } else if (newConfig.orientation === Configuration.ORIENTATION_PORTRAIT) {
            val params = playerView.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            playerView.requestLayout()
        }
    }

    private fun initializePlayer() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.registerPlayerComponent(FillScreenComponent(this,R.id.playerFillScreenButton))
        playerView.player = mediaPlayer
        //Uri from assets.
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
            .createMediaSource(Uri.parse("asset:///sample media.mp4"))
        mediaPlayer.playWhenReady=false
        mediaPlayer.seekTo(currentWindowIndex,currentPosition)
        mediaPlayer.prepare(mediaSource,false,false)

        mediaPlayer.addVideoListener(object : VideoListener {
            override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
                if(playWhenReady){
                    mediaPlayer.playWhenReady = playWhenReady
                }

            }
        })
    }

    override fun onResume() {
        super.onResume()
        initializePlayer()
        println("Activity:${hashCode()} onResume\n")
    }


    override fun onPause() {
        super.onPause()
        releasePlayer()
        println("Activity:${hashCode()} onPause\n")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        playWhenReady=savedInstanceState.getBoolean("playWhenReady")
        currentWindowIndex=savedInstanceState.getInt("currentWindowIndex")
        currentPosition=savedInstanceState.getLong("currentPosition")
        val message=savedInstanceState.getString("message")
        val output=StringBuilder()
        output.append(message)
        println("Activity:${hashCode()} onRestoreInstanceState\n")
        val sampleMessageText=findViewById<TextView>(R.id.sampleMessageText)
        if(null!=sampleMessageText){
            sampleMessageText.text = output
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //Because if we restart this activity. It will re-create the fragment by FragmentManagerState
        outState.remove("android:support:fragments")
        val output=StringBuilder()
        val sampleMessageText=findViewById<TextView>(R.id.sampleMessageText)
        output.append(sampleMessageText.text.toString())
        output.append("Activity:${hashCode()} onSaveInstanceState\n")
        outState.putString("message",output.toString())
        outState.putBoolean("playWhenReady",playWhenReady)
        outState.putInt("currentWindowIndex",currentWindowIndex)
        outState.putLong("currentPosition",currentPosition)
    }

    private fun releasePlayer() {
        val mediaPlayer=playerView.player
        if(null!=mediaPlayer){
            playWhenReady=mediaPlayer.playWhenReady
            currentWindowIndex=mediaPlayer.currentWindowIndex
            currentPosition=mediaPlayer.currentPosition
            mediaPlayer.release()
        }
    }
}