package org.cz.media.player.sample.sample.overlay

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.component.document.SampleDocument
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import org.cz.media.player.sample.extention.overlay.GlideArtworkOverlay
import kotlinx.android.synthetic.main.activity_media_player_view.playerView
import org.cz.media.player.sample.R

/**
 * @author Created by cz
 * @date 2020/8/6 9:54 AM
 * @email binigo110@126.com
 *
 * This example teaches you how to add an overlay to the view to display the artwork for the video.
 */
@SampleDocument("artwork_overlay.md")
@Register(title="预览图层",desc = "演示预览图,在播放前展示预览图")
class ArtworkOverlaySampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overlay_sample)
        //Attach the player overlay to the MediaPlayerView.
        val defaultMediaPlayerOverlay = GlideArtworkOverlay(this, "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2491915268,3538783058&fm=15&gp=0.jpg")
        defaultMediaPlayerOverlay.attachToPlayerView(this,playerView)
    }

    private fun initializePlayerFromAssets() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.player = mediaPlayer
        //Uri from assets.
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
            .createMediaSource(Uri.parse("asset:///sample media.mp4"))
        mediaPlayer.playWhenReady=false
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