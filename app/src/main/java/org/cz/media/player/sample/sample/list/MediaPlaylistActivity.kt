package org.cz.media.player.sample.sample.list

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_media_playlist.*
import org.cz.media.player.sample.R
import kotlin.collections.ArrayList

/**
 * @author Created by cz
 * @date 2020/8/6 9:54 AM
 * @email binigo110@126.com
 *
 * This example shows you how to load a media list.
 * Noticed how we synchronize the position of the list and how to change the playback.
 */
@SampleSourceCode
@SampleDocument("media_list.md")
@Register(title="视频列表",desc = "演示视频列表播放,更新播放状态等")
class MediaPlaylistActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_playlist)
        recyclerView.layoutManager= GridLayoutManager(this,5)
        val videoListAdapter = PlaybackListAdapter(buildVideoList())
        videoListAdapter.setOnItemClickListener { _, position ->
            playerView?.player?.seekToDefaultPosition(position)
        }
        recyclerView.adapter=videoListAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
    }

    /**
     * Build a ConcatenatingMediaSource media source for the player.
     * See the video list.
     */
    private fun initializePlayer() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.player = mediaPlayer

        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSourceFactory = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
        val concatenatingMediaSource = ConcatenatingMediaSource()

        val adapter = recyclerView.adapter as PlaybackListAdapter
        val videoList = adapter.videoList
        for(videoItem in videoList){
            val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(Uri.parse(videoItem.url))
            concatenatingMediaSource.addMediaSource(mediaSource)
        }

        mediaPlayer.playWhenReady=true
        mediaPlayer.prepare(concatenatingMediaSource,false,false)
        mediaPlayer.seekToDefaultPosition()
        mediaPlayer.addListener(object : Player.EventListener{
            override fun onPositionDiscontinuity(reason: Int) {
                //Change the window index Dynamically
                if(Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED==reason){
                    adapter.setSelectPosition(mediaPlayer.currentWindowIndex)
                }
            }
        })
    }


    private fun buildVideoList():List<PlaybackItem> {
        val playbackItemList: MutableList<PlaybackItem> = ArrayList()
        playbackItemList.add(
            PlaybackItem(
                "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318214226685784.mp4",
                1000,
                562
            )
        )
        playbackItemList.add(
            PlaybackItem(
                "http://vfx.mtime.cn/Video/2019/03/17/mp4/190317150237409904.mp4",
                1000,
                562
            )
        )
        playbackItemList.add(
            PlaybackItem(
                "http://vfx.mtime.cn/Video/2019/03/13/mp4/190313094901111138.mp4",
                1000,
                416
            )
        )
        playbackItemList.add(
            PlaybackItem(
                "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319125415785691.mp4",
                1000,
                562
            )
        )
        playbackItemList.add(
            PlaybackItem(
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                1000,
                562
            )
        )
        playbackItemList.add(
            PlaybackItem(
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314102306987969.mp4",
                1000,
                562
            )
        )
        playbackItemList.add(
            PlaybackItem(
                "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4",
                1000,
                562
            )
        )
        playbackItemList.add(
            PlaybackItem(
                "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312083533415853.mp4",
                1000,
                562
            )
        )
        playbackItemList.add(
            PlaybackItem(
                "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319104618910544.mp4",
                1000,
                562
            )
        )
        playbackItemList.add(
            PlaybackItem(
                "http://vfx.mtime.cn/Video/2019/03/09/mp4/190309153658147087.mp4",
                1000,
                562
            )
        )
        return playbackItemList
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
        playerView.player?.release()
    }



}