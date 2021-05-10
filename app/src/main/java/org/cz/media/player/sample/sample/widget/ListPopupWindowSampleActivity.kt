package org.cz.media.player.sample.sample.widget

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.component.document.SampleDocument
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_list_popup_window_sample.*
import org.cz.media.player.sample.R
import org.cz.media.player.sample.extention.component.SpeedChangeComponent

/**
 * @author Created by cz
 * @date 2020/8/12 9:54 AM
 * @email binigo110@126.com
 *
 * This example shows you how to create your own component, and how to use it.
 * @see SpeedChangeComponent The speed component.
 *
 */
@SampleDocument("list_popup.md")
@Register(title="列表弹窗",desc="演示播放器列表弹窗,如加速,清晰度选择等")
class ListPopupWindowSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_popup_window_sample)
    }

    private fun initializePlayer() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.player = mediaPlayer
        val speedLevelList= listOf("0.50","0.75","Normal","1.25","1.50","1.75")
        playerView.registerPlayerComponent(SpeedChangeComponent(speedLevelList))
        //Uri from url.
        val url="http://vfx.mtime.cn/Video/2019/03/13/mp4/190313094901111138.mp4";
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(url))
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
        playerView.player?.release()
    }
}