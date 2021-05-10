package org.cz.media.player.sample.extention.overlay

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import org.cz.media.player.base.MediaPlayerView
import org.cz.media.player.library.overlay.MediaPlayerArtworkOverlay

/**
 * @author Created by cz
 * @date 2020/9/18 10:15 AM
 * @email bingo110@126.com
 */
class GlideArtworkOverlay @JvmOverloads constructor(
    private val context: Context,
    private val url: String,
    private val hideWhenPlay: Boolean = true
) : MediaPlayerArtworkOverlay() {

    override fun onAttachToPlayer(playerView: MediaPlayerView, contentView: View) {
        super.onAttachToPlayer(playerView, contentView)
        playerView.addOnPlayerChangeListener { newPlayer: Player, _: Player? ->
            newPlayer.addListener(object : Player.EventListener {
                override fun onPlayerError(error: ExoPlaybackException) {
                    contentView.visibility = View.GONE
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    Log.d("GlideArtworkOverlay","playbackState =$playbackState")
                    if (Player.STATE_READY == playbackState && playWhenReady && hideWhenPlay) {
                        contentView.visibility = View.GONE
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    Log.d("GlideArtworkOverlay","isPlaying =$isPlaying")
                    contentView.visibility=if (isPlaying) View.GONE else View.VISIBLE
                }
            })
        }
    }

    override fun onLoadImage(imageView: ImageView) {
        Glide.with(context).load(url).into(imageView)
    }

}