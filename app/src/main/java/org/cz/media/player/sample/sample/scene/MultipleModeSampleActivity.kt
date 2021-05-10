package org.cz.media.player.sample.sample.scene

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.component.document.SampleDocument
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_multiple_mode_sample.*
import org.cz.media.player.library.extension.component.CompanionPlayButtonComponent
import org.cz.media.player.library.group.MediaPlayerGroup
import org.cz.media.player.sample.R

/**
 * @author Created by cz
 * @date 2020/8/6 9:54 AM
 * @email binigo110@126.com
 *
 * This example have multiple MediaPlayers. It shows you how to switch from one player to another.
 */
@SampleDocument("multiple_mode.md")
@Register(title="窗体切换",desc = "演示切换不同 Player控件")
class MultipleModeSampleActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ExoplayerSample3"
    }
    private val mediaPlayerGroup = MediaPlayerGroup()
    private var eventListener= OnPlaybackStatusChangeListener()
    private var playWhenReady=false
    private var currentWindowIndex=0
    private var currentPosition=0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_mode_sample)
    }

    private fun initializePlayer() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView1.player = mediaPlayer
        //1. Uri from url.
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse("asset:///sample media.mp4"))
        mediaPlayer.playWhenReady=playWhenReady
        mediaPlayer.seekTo(currentWindowIndex,currentPosition)
        mediaPlayer.addListener(eventListener)
        mediaPlayer.prepare(mediaSource)
        mediaPlayerGroup.attachToHost(frameLayout)
        mediaPlayerGroup.show(R.id.playerView1)
        mediaPlayerGroup.setOnMediaPlayerViewInitialListener { playerView->
            if(R.id.playerView2==playerView.id){
                playerView.registerPlayerComponent(CompanionPlayButtonComponent())
            }
        }

        nextButton.setOnClickListener {
            mediaPlayerGroup.next()
        }
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

    class OnPlaybackStatusChangeListener : Player.EventListener {
        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            Log.i(TAG, "onTimelineChanged:$timeline")
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            Log.i(TAG, "onTracksChanged:$trackSelections")
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            Log.i(TAG, "onLoadingChanged:$isLoading")
        }

        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "onPlayerStateChanged changed state to " + stateString + " playWhenReady: " + playWhenReady)
        }

        override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
            Log.i(TAG, "onPlaybackSuppressionReasonChanged:$playbackSuppressionReason")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Log.i(TAG, "onIsPlayingChanged:$isPlaying")
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            Log.i(TAG, "onRepeatModeChanged:$repeatMode")
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            Log.i(TAG, "onShuffleModeEnabledChanged:$shuffleModeEnabled")
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            Log.i(TAG, "onPlayerError:$error")
        }

        override fun onPositionDiscontinuity(reason: Int) {
            Log.i(TAG, "onPositionDiscontinuity:$reason")
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            Log.i(TAG, "onPlaybackParametersChanged:$playbackParameters")
        }

        override fun onSeekProcessed() {
            Log.i(TAG, "onSeekProcessed:")
        }
    }

}