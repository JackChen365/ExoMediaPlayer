package org.cz.media.player.sample.sample.list

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import org.cz.media.player.library.AppCompatMediaPlayerView
import org.cz.media.player.base.timebar.DefaultTimeBar
import org.cz.media.player.sample.R
import org.cz.media.player.sample.extention.overlay.GlideArtworkOverlay
import org.cz.media.player.sample.sample.list.component.MiniTimeBarComponent
import java.util.*

/**
 * @author Created by cz
 * @date 2020/9/9 4:16 PM
 * @email binigo110@126.com
 */
class RecycleListAdapter (var context: Context,var videoList: List<VideoItem> = ArrayList()) : RecyclerView.Adapter<RecycleListAdapter.ViewHolder>(),
    MediaPlayerCallback {
    private var simpleGestureDetectOverlay = SimpleGestureDetectOverlay()
    private var recyclerView: RecyclerView? = null
    private var isPlayerPausing = false

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val scrollListener = object : RecyclerView.OnScrollListener() {
            private var lastPosition=RecyclerView.NO_POSITION;
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val adapter = recyclerView.adapter
                    if (null != adapter && adapter is RecycleListAdapter) {
                        val layoutManager = recyclerView.layoutManager
                        if (null != layoutManager && layoutManager is LinearLayoutManager) {
                            val position = layoutManager.findFirstVisibleItemPosition()
                            if(lastPosition != position){
                                val viewHolder = recyclerView.findViewHolderForLayoutPosition(position)
                                if (null != viewHolder && viewHolder is ViewHolder) {
                                    //Record the last position.
                                    lastPosition = position
                                    //Release the player
                                    releaseMediaPlayer(viewHolder)

                                    //Initial the new player.
                                    val newPlayer = SimpleExoPlayer.Builder(context).build()
                                    val miniTimeBarComponent = MiniTimeBarComponent(viewHolder.playerTimeBar)

                                    //Attach the gesture layout.
                                    viewHolder.mediaPlayer.overlayFrameLayout?.removeAllViews()
                                    simpleGestureDetectOverlay.attachToPlayerView(context,viewHolder.mediaPlayer)
                                    //Attach the artwork overlay.
                                    val defaultMediaPlayerOverlay = GlideArtworkOverlay(context, videoList[position].thumbnail)
                                    defaultMediaPlayerOverlay.attachToPlayerView(context, viewHolder.mediaPlayer)

                                    viewHolder.mediaPlayer.registerPlayerComponent(miniTimeBarComponent)
                                    viewHolder.mediaPlayer.player = newPlayer
                                    val item = adapter.getItem(position)
                                    val defaultDataSourceFactory = DefaultDataSourceFactory(context, "test")
                                    val mediaSourceFactory = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                                    val mediaSource = mediaSourceFactory.createMediaSource(MediaItem.fromUri(item.url))
                                    newPlayer.playWhenReady=false
                                    newPlayer.prepare(mediaSource,false,false)
                                }
                            }
                        }
                    }
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
        recyclerView.post {
            scrollListener.onScrollStateChanged(recyclerView,RecyclerView.SCROLL_STATE_IDLE)
        }
        this.recyclerView = recyclerView
    }

    private fun findCurrentVisibleViewHolder(): ViewHolder?{
        val recyclerView=recyclerView?:return null
        val layoutManager = recyclerView.layoutManager
        if (null != layoutManager && layoutManager is LinearLayoutManager) {
            val position = layoutManager.findFirstVisibleItemPosition()
            val viewHolder = recyclerView.findViewHolderForLayoutPosition(position)
            if (null != viewHolder && viewHolder is ViewHolder) {
                //Release the player
                return viewHolder;
            }
        }
        return null
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        val viewHolder = findCurrentVisibleViewHolder()
        //Release the player
        if(null!=viewHolder){
            releaseMediaPlayer(viewHolder)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val parentView = layoutInflater.inflate(R.layout.video_recycle_list_layout, parent, false)
        return ViewHolder(parentView)
    }

    override fun pause() {
        val viewHolder = findCurrentVisibleViewHolder()
        if(null!=viewHolder){
            val player = viewHolder.mediaPlayer.player
            if(null!=player){
                isPlayerPausing = player.isPlaying
                player.playWhenReady = false
            }
        }
    }

    override fun resume() {
        val viewHolder = findCurrentVisibleViewHolder()
        if(null!=viewHolder && isPlayerPausing){
            viewHolder.mediaPlayer.player?.playWhenReady = true
        }
    }

    override fun release() {
        val viewHolder = findCurrentVisibleViewHolder()
        if(null!=viewHolder){
            releaseMediaPlayer(viewHolder)
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        //When the view detach from the window, We should always release the player resources.
        releaseMediaPlayer(holder)
    }

    private fun releaseMediaPlayer(holder: ViewHolder) {
        val mediaPlayer = holder.mediaPlayer
        if (null != mediaPlayer) {
            mediaPlayer.player?.release()
            mediaPlayer.player=null
        }
    }

    fun getItem(position: Int): VideoItem {
        return videoList[position]
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mediaPlayer: AppCompatMediaPlayerView = itemView.findViewById(R.id.mediaPlayer)
        var playerTimeBar: DefaultTimeBar = itemView.findViewById(R.id.playerMiniTimeBar)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }
}