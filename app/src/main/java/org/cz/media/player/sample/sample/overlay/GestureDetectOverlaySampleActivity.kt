package org.cz.media.player.sample.sample.overlay

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.android.sample.library.component.message.SampleMessage
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_overlay_sample.*
import org.cz.media.player.base.MediaPlayerView
import org.cz.media.player.base.controller.component.PlayerComponent
import org.cz.media.player.library.extension.PlayerGestureLayout
import org.cz.media.player.library.extension.ProgressStatusView
import org.cz.media.player.library.extension.detect.*
import org.cz.media.player.library.extension.detect.AcceleratePressGestureDetector.OnAccelerateEventChangeListener
import org.cz.media.player.library.overlay.MediaPlayerOverlay
import org.cz.media.player.sample.R

/**
 * @author Created by cz
 * @date 2020/8/6 9:54 AM
 * @email binigo110@126.com
 *
 * This example teaches you how to use {@link MediaPlayerGestureDetectOverlay}
 * The MediaPlayerGestureDetectOverlay always cooperates with those gesture detectors.
 *
 * @see AcceleratePressGestureDetector(长按加速)
 * @see BrightnessGestureDetector(亮度调节)
 * @see DoubleTapGestureDetector(双击)
 * @see LongTapGestureDetector(长按)
 * @see MultipleTapGestureDetector(多击行为,如双击,双次点击)
 * @see PlayerProgressGestureDetector(播放进度调节)
 * @see SingleTapGestureDetector(单击)
 * @see SoundGestureDetector(音量调节)
 */
@SampleMessage
@SampleDocument("gesture_overlay.md")
@Register(title="控制浮层",desc = "演示浮层扩展,主要为各种手势操作")
class GestureDetectOverlaySampleActivity : AppCompatActivity() {
    private var playWhenReady=false
    private var currentWindowIndex=0
    private var currentPosition=0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overlay_sample)

        //Attach the player overlay to the MediaPlayerView.
        //{@code MediaPlayerGestureDetectOverlay}
//        val mediaPlayerGestureDetectOverlay = MediaPlayerGestureDetectOverlay(this)
//        mediaPlayerGestureDetectOverlay.attachToPlayerView(this,playerView)
        val defaultMediaPlayerOverlay = MessageGestureDetectOverlay(this)
        defaultMediaPlayerOverlay.attachToPlayerView(this,playerView)
    }

    private fun initializePlayer() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.player = mediaPlayer
        // Uri from assets
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
            .createMediaSource(Uri.parse("asset:///sample media.mp4"))
        mediaPlayer.playWhenReady=playWhenReady
        mediaPlayer.seekTo(currentWindowIndex,currentPosition)
        mediaPlayer.prepare(mediaSource,false,false)
    }

    // Activity input
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        // See whether the player view wants to handle media or DPAD keys events.
        return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event)
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
        val mediaPlayer = playerView.player
        if(null!=mediaPlayer){
            playWhenReady=mediaPlayer.playWhenReady
            currentWindowIndex=mediaPlayer.currentWindowIndex
            currentPosition=mediaPlayer.currentPosition
            mediaPlayer.release()
        }
    }

    class MessageGestureDetectOverlay(private val activity: Activity) :
        MediaPlayerOverlay() {
        override fun onCreateOverlay(context: Context, parent: ViewGroup): View {
            return LayoutInflater.from(context).inflate(R.layout.media_player_gesture_overlay_layout, parent, false)
        }

        override fun onAttachToPlayer(playerView: MediaPlayerView, contentView: View) {
            super.onAttachToPlayer(playerView, contentView)
            val context = contentView.context
            //Replace the view.
            val layoutInflater = LayoutInflater.from(context)
            val parentView = playerView.parent as ViewGroup
            val layout = layoutInflater.inflate(org.cz.media.player.library.R.layout.media_player_gesture_progress_overlay_layout, parentView, false) as ViewGroup
            replaceChildWithView(playerView, layout)
            layout.addView(playerView,0)

            //Initialize all the gesture detectors.
            val playerGestureLayout: PlayerGestureLayout = contentView.findViewById(R.id.playerGestureLayout)
            val brightnessStatusView: ProgressStatusView = contentView.findViewById(R.id.brightnessStatusView)
            brightnessStatusView.setOnProgressChangeListener { _: ProgressStatusView?, progress: Int, max: Int ->
                val window = activity.window
                val attributes = window.attributes
                attributes.screenBrightness = progress * 1f / max
                window.attributes = attributes
            }
            val brightnessLayout = contentView.findViewById<View>(R.id.brightnessLayout)
            val brightnessImage = contentView.findViewById<ImageView>(R.id.brightnessImage)
            val brightnessGestureDetector = BrightnessGestureDetector(context, brightnessLayout, brightnessStatusView, brightnessImage)
            playerGestureLayout.addGestureDetector(brightnessGestureDetector)

            val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val soundStatusView: ProgressStatusView = playerGestureLayout.findViewById(R.id.soundStatusView)
            soundStatusView.setOnProgressChangeListener { _: ProgressStatusView?, progress: Int, max: Int ->
                val volume = (progress * 1f / max * MAX_VOLUME_VALUE).toInt()
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI)
            }
            val soundLayout = layout.findViewById<View>(R.id.soundLayout)
            val soundImage = layout.findViewById<ImageView>(R.id.soundImage)
            val soundGestureDetector = SoundGestureDetector(context, soundLayout, soundStatusView, soundImage)
            playerGestureLayout.addGestureDetector(soundGestureDetector)
            val progressText = layout.findViewById<TextView>(R.id.progressText)
            val playerProgressGestureDetector = PlayerProgressGestureDetector(context, playerView, progressText)
            playerGestureLayout.addGestureDetector(playerProgressGestureDetector)

            val singleTapGestureDetector = SingleTapGestureDetector(playerGestureLayout)
            singleTapGestureDetector.setAbortWhenTimeout(true)
            singleTapGestureDetector.setOnSingleTapListener {
                println("Single tap.")
                playerView.performClick()
            }
            playerGestureLayout.addGestureDetector(singleTapGestureDetector)
            val doubleTapGestureDetector = DoubleTapGestureDetector(playerGestureLayout)
            doubleTapGestureDetector.setOnTapListener {
                println("Double tap.")
                val playerComponent =
                    playerView.getPlayerComponent<PlayerComponent>(PlayerComponent.COMPONENT_PLAY_PAUSE)
                playerComponent?.execute()
            }
            playerGestureLayout.addGestureDetector(doubleTapGestureDetector)

//        MultipleTapGestureDetector multipleTapTimesGestureDetector = new MultipleTapGestureDetector(playerGestureLayout, ViewConfiguration.getDoubleTapTimeout(),2);
//        multipleTapTimesGestureDetector.setOnTapListener(v->System.out.println("Multiple Tap."));
//        playerGestureLayout.addDetectGestureListener(multipleTapTimesGestureDetector);
            val accelerateText = layout.findViewById<TextView>(R.id.accelerateText)
            val acceleratePressGestureDetector =
                AcceleratePressGestureDetector(playerGestureLayout)
            acceleratePressGestureDetector.setAccelerateDuration(2000L)
            acceleratePressGestureDetector.setOnAccelerateEventChangeListener(object :
                OnAccelerateEventChangeListener {
                private var counter = 0
                override fun onAccelerateStarted() {
                    accelerateText.visibility = View.VISIBLE
                    counter = 0
                    accelerateText.text = counter.toString()
                    println("Accelerate long press.")
                }

                override fun onTick() {
                    counter++
                    accelerateText.text = counter.toString()
                }

                override fun onAccelerateStopped() {
                    accelerateText.visibility = View.GONE
                }
            })
            playerGestureLayout.addGestureDetector(acceleratePressGestureDetector)
            val longTapGestureDetector =
                LongTapGestureDetector(playerGestureLayout)
            longTapGestureDetector.setOnLongClickListener { println("Long Tap.") }
            playerGestureLayout.addGestureDetector(longTapGestureDetector)
            playerGestureLayout.attachToPlayer(playerView)
        }

        private fun replaceChildWithView(childView: View, replaceView: View) {
            val viewParent = childView.parent
            if (null != viewParent && viewParent is ViewGroup) {
                val index = viewParent.indexOfChild(childView)
                viewParent.removeViewInLayout(childView)
                val layoutParams = childView.layoutParams
                if (layoutParams != null) {
                    viewParent.addView(replaceView, index, layoutParams)
                } else {
                    viewParent.addView(replaceView, index)
                }
            }
        }

        companion object {
            private const val MAX_VOLUME_VALUE = 15
        }

    }

}