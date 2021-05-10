package org.cz.media.player.sample.sample.usage

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_usage_audio_sample.*
import org.cz.media.player.library.extension.component.CompanionPlayButtonComponent
import org.cz.media.player.library.overlay.MediaPlayerGestureDetectOverlay
import org.cz.media.player.sample.extention.overlay.GlideArtworkOverlay
import org.cz.media.player.sample.R
import java.util.*

/**
 * This sample shows how to play an audio with cover.
 */
@Register(title="音频播放",desc = "演示音频播放加外置封面")
class AudioSampleActivity : AppCompatActivity() {
    private val formatBuilder = java.lang.StringBuilder()
    private val formatter = Formatter(formatBuilder, Locale.getDefault())
    private var playWhenReady=true
    private var currentWindowIndex=0
    private var currentPosition=0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_audio_sample)

        //Attach the player overlay to the MediaPlayerView.
        val defaultMediaPlayerOverlay = GlideArtworkOverlay(this, "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2491915268,3538783058&fm=15&gp=0.jpg",false)
        defaultMediaPlayerOverlay.attachToPlayerView(this,playerView)

        //Attach the player overlay to the MediaPlayerView.
        //{@code MediaPlayerGestureDetectOverlay}
        val mediaPlayerGestureDetectOverlay = MediaPlayerGestureDetectOverlay(this)
        mediaPlayerGestureDetectOverlay.attachToPlayerView(this,playerView)
    }

    private fun initializePlayerFromAssets() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        //Hide the zoom button if we do not need.
        val playerModeButton = playerView.findViewById<View>(R.id.playerModeButton)
        playerModeButton.visibility = View.GONE
        playerView.registerPlayerComponent(CompanionPlayButtonComponent())
        playerView.player = mediaPlayer
        //Uri from assets.
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
            .createMediaSource(Uri.parse("asset:///Take_My_Hand.mp3"))
        val sharedPreferences = getSharedPreferences("test_player", Context.MODE_PRIVATE)
        currentPosition = sharedPreferences.getLong("currentPosition", 0L)
        if(0L != currentPosition){
            val positionText = getStringForTime(formatBuilder, formatter, currentPosition)
            val text = getString(R.string.restore_position_hint, positionText)
            AlertDialog.Builder(this).setMessage(text).setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    mediaPlayer.playWhenReady = playWhenReady
                    mediaPlayer.seekTo(currentWindowIndex, currentPosition)
                    mediaPlayer.prepare(mediaSource, false, false)
                }.setNegativeButton(android.R.string.cancel) { _, _ ->
                    mediaPlayer.playWhenReady = playWhenReady
                    mediaPlayer.seekTo(currentWindowIndex, 0)
                    mediaPlayer.prepare(mediaSource, false, false)
                }.show()
        } else {
            mediaPlayer.playWhenReady=playWhenReady
            mediaPlayer.seekTo(currentWindowIndex,currentPosition)
            mediaPlayer.prepare(mediaSource, false, false)
        }
    }

    /**
     * Returns the specified millisecond time formatted as a string.
     *
     * @param builder The builder that `formatter` will write to.
     * @param formatter The formatter.
     * @param timeMs The time to format as a string, in milliseconds.
     * @return The time formatted as a string.
     */
    private fun getStringForTime(builder: StringBuilder, formatter: Formatter, timeMs: Long): String? {
        var timeMs = timeMs
        val totalSeconds = (timeMs + 500) / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        builder.setLength(0)
        return if (hours > 0) formatter.format("%d:%02d:%02d", hours, minutes, seconds)
            .toString() else formatter.format("%02d:%02d", minutes, seconds).toString()
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

    private fun saveCurrentPosition(){
        val sharedPreferences = getSharedPreferences("test_player", Context.MODE_PRIVATE)
        sharedPreferences.edit().putLong("currentPosition",currentPosition).commit()
    }

    private fun releasePlayer() {
        val player = playerView.player
        if(null!=player){
            playWhenReady=player.playWhenReady
            currentWindowIndex=player.currentWindowIndex
            currentPosition=player.currentPosition
            saveCurrentPosition()
            player.release()
            playerView.player = null
        }
    }
}