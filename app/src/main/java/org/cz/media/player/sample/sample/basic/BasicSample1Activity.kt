package org.cz.media.player.sample.sample.basic

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.component.document.SampleDocument
import com.cz.android.sample.library.function.permission.SamplePermission

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_basic_sample.*
import kotlinx.android.synthetic.main.activity_list_popup_window_sample.playerView
import org.cz.media.player.sample.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * @author Created by cz
 * @date 2020/8/4 9:54 AM
 * @email binigo110@126.com
 *
 * Demonstrate difference media source we can load.
 * 1. From local file.
 * 2. From asset file.
 * 3. From original server
 */
@SampleDocument("basic1.md")
@SamplePermission(Manifest.permission.READ_EXTERNAL_STORAGE)
@Register(title="常规使用1",desc="演示播放不同来源的视频")
class BasicSample1Activity : AppCompatActivity() {
    private val formatBuilder = java.lang.StringBuilder()
    private val formatter = Formatter(formatBuilder, Locale.getDefault())
    private var playWhenReady=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_basic_sample)
        radioButtonLayout.setOnCheckedChangeListener { _, checkedId ->
//            playerView.clearVideoSurface()
            releasePlayer()
            when(checkedId){
                0->initializePlayerFromUrl()
                1->initializePlayerFromDisk()
                2->initializePlayerFromAssets()
            }
        }

        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    val player = playerView.player
                    player?.seekTo(player.currentWindowIndex,progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun initializePlayerFromUrl() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.player = mediaPlayer
        updateSeekBar(mediaPlayer)
        //Uri from url.
        val url="http://vfx.mtime.cn/Video/2019/03/13/mp4/190313094901111138.mp4";
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(url))
        mediaPlayer.playWhenReady=true
        mediaPlayer.prepare(mediaSource,true,true)
    }

    private fun initializePlayerFromDisk() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.player = mediaPlayer
        updateSeekBar(mediaPlayer)
        val playbackList = queryPlaybacks(1)
        if(playbackList.isNotEmpty()){
            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(this@BasicSample1Activity, "test-player")
            val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
            val filePath = playbackList.first()
            val file = File(filePath)
            val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(Uri.fromFile(file))
            mediaPlayer.playWhenReady=true
            mediaPlayer.prepare(mediaSource,true,true)
        } else {
            val tempFile = copyTempFileAsset(this, "sample media.mp4")
            if(null!=tempFile){
                val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(this@BasicSample1Activity, "test")
                val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
                val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(Uri.fromFile(tempFile))
                mediaPlayer.playWhenReady=true
                mediaPlayer.prepare(mediaSource,true,true)
            }
        }
    }

    private fun initializePlayerFromAssets() {
        val mediaPlayer = SimpleExoPlayer.Builder(this).build()
        playerView.player = mediaPlayer
        updateSeekBar(mediaPlayer)
        //Uri from assets.
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "test")
        val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
            .createMediaSource(Uri.parse("asset:///sample media.mp4"))
        mediaPlayer.playWhenReady=playWhenReady
        mediaPlayer.prepare(mediaSource,true,true)
    }

    private fun queryPlaybacks(limit:Int):List<String>{
        val mediaList = mutableListOf<String>()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cr = contentResolver
        val columns = arrayOf(MediaStore.Audio.Media.DATA) //add which column you need here
        var cursor : Cursor?=null;
        try {
            cursor=cr.query(uri, columns, null, null, MediaStore.Audio.Media.DATA+" ASC  LIMIT $limit")
            if(null!=cursor){
                while(cursor.moveToNext()) {
                    //read your information here
                    val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    mediaList.add(data)
                }
            }
        } catch (e:IllegalArgumentException){
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return mediaList
    }

    private fun updateSeekBar(player: SimpleExoPlayer) {
        player.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                if (!timeline.isEmpty) {
                    var durationUs: Long = 0
                    val period = Timeline.Period()
                    val window = Timeline.Window()
                    val firstWindowIndex = 0
                    for (i in firstWindowIndex until timeline.windowCount) {
                        timeline.getWindow(i, window)
                        for (j in window.firstPeriodIndex..window.lastPeriodIndex) {
                            timeline.getPeriod(j, period)
                        }
                        durationUs += window.durationUs
                    }
                    seekBar.max=C.usToMs(durationUs).toInt()
                    val stringForTime = getStringForTime(formatBuilder, formatter, C.usToMs(durationUs))
                    durationText.text=stringForTime
                }
            }
        })
    }

    private fun copyTempFileAsset(context: Context, assetFileName: String): File? {
        var file: File? = null
        val assets = context.assets
        var inputStream: InputStream? = null
        try {
            inputStream = assets.open(assetFileName)
            val bytes = ByteArray(inputStream.available())
            inputStream.read(bytes)
            file = File.createTempFile("tmp", ".mp4")
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(bytes)
            file.deleteOnExit()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return file
    }

    /**
     * Returns the specified millisecond time formatted as a string.
     *
     * @param builder The builder that `formatter` will write to.
     * @param formatter The formatter.
     * @param timeMs The time to format as a string, in milliseconds.
     * @return The time formatted as a string.
     */
    fun getStringForTime(builder: StringBuilder, formatter: Formatter, timeMs: Long): String? {
        var timeMs = timeMs
        val totalSeconds = (timeMs + 500) / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        builder.setLength(0)
        return if (hours > 0) formatter.format("%d:%02d:%02d", hours, minutes, seconds)
            .toString() else formatter.format("%02d:%02d", minutes, seconds).toString()
    }

    override fun onResume() {
        super.onResume()
        if(null==playerView.player){
            initializePlayerFromUrl()
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    private fun releasePlayer() {
        val player = playerView.player
        if(null!=player){
            playWhenReady=player.playWhenReady
            player.release()
        }
        playerView.player = null
    }
}