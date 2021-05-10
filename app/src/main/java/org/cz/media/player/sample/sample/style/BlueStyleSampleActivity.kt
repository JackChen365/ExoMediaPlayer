package org.cz.media.player.sample.sample.style

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.component.document.SampleDocument
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_blue_style_sample.*
import org.cz.media.player.sample.R

/**
 * @author Created by cz
 * @date 2020/8/6 9:54 AM
 * @email binigo110@126.com
 *
 * This example help you customize the style of MediaPlayerView.
 * It has a different style. see the file: values/player-style.xml know more details.
 *
 */
@SampleDocument("blue_style.md")
@Register(title="样式演示",desc = "演示不同定制样式")
class BlueStyleSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blue_style_sample)
    }

    private fun initializePlayerFromAssets() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.player = mediaPlayer
        //Uri from assets.
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
            .createMediaSource(Uri.parse("asset:///sample media.mp4"))
        mediaPlayer.playWhenReady=true
        mediaPlayer.prepare(mediaSource,false,false)
    }

    override fun onResume() {
        super.onResume()
        if(null==playerView.player){
            initializePlayerFromAssets()
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    private fun releasePlayer() {
        playerView.player?.release()
    }

}