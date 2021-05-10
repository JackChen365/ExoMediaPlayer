package org.cz.media.player.sample.sample.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import kotlinx.android.synthetic.main.activity_media_playlist.*
import org.cz.media.player.sample.R

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
@Register(title="Recycle视频列表",desc = "演示视频列表播放,更新播放状态等")
class MediaPlayRecyclerListActivity : AppCompatActivity() {
    private var callback: MediaPlayerCallback?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_recycle_playlist)
        //Initial the recycler list.
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(recyclerView)
        val recycleListAdapter = RecycleListAdapter(this, buildVideoList())
        callback=recycleListAdapter
        recyclerView.adapter = recycleListAdapter
    }

    override fun onStart() {
        super.onStart()
        callback?.resume()
    }

    override fun onStop() {
        super.onStop()
        callback?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        callback?.release()
    }

    private fun buildVideoList():List<VideoItem> {
        val videoItemList: MutableList<VideoItem> = ArrayList()
        videoItemList.add(
            VideoItem(
                "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318214226685784.mp4",
                "https://rv.okjiaoyu.cn/rv_1np7Sm2fho4.low.h264.mp4?vframe/jpg/offset/3.5/w/1280/h/800",
                1000,
                562
            )
        )
        videoItemList.add(
            VideoItem(
                "http://vfx.mtime.cn/Video/2019/03/17/mp4/190317150237409904.mp4",
                "https://rv.okjiaoyu.cn/rv_1np7Sm2fho4.low.h264.mp4?vframe/jpg/offset/3.5/w/1280/h/800",
                1000,
                562
            )
        )
        videoItemList.add(
            VideoItem(
                "http://vfx.mtime.cn/Video/2019/03/13/mp4/190313094901111138.mp4",
                "http://rv.okjiaoyu.cn/rv_1np7Sm363Ic.low.h264.mp4?vframe/jpg/offset/3.5/w/1280/h/800",
                1000,
                416
            )
        )
        videoItemList.add(
            VideoItem(
                "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319125415785691.mp4",
                "http://rv.okjiaoyu.cn/rv_1np7Sm363Ic.low.h264.mp4?vframe/jpg/offset/3.5/w/1280/h/800",
                1000,
                562
            )
        )
        videoItemList.add(
            VideoItem(
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
                "https://rv.okjiaoyu.cn/rv_1np7Sm2fho4.low.h264.mp4?vframe/jpg/offset/3.5/w/1280/h/800",
                1000,
                562
            )
        )
        videoItemList.add(
            VideoItem(
                "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314102306987969.mp4",
                "https://rv.okjiaoyu.cn/rv_1np7Sm2fho4.low.h264.mp4?vframe/jpg/offset/3.5/w/1280/h/800",
                1000,
                562
            )
        )
        videoItemList.add(
            VideoItem(
                "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4",
                "https://rv.okjiaoyu.cn/rv_1np7Sm2fho4.low.h264.mp4?vframe/jpg/offset/3.5/w/1280/h/800",
                1000,
                562
            )
        )
        videoItemList.add(
            VideoItem(
                "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312083533415853.mp4",
                "https://rv.okjiaoyu.cn/rv_1np7Sm2fho4.low.h264.mp4?vframe/jpg/offset/3.5/w/1280/h/800",
                1000,
                562
            )
        )
        videoItemList.add(
            VideoItem(
                "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319104618910544.mp4",
                "https://rv.okjiaoyu.cn/rv_1np7Sm2fho4.low.h264.mp4?vframe/jpg/offset/3.5/w/1280/h/800",
                1000,
                562
            )
        )
        videoItemList.add(
            VideoItem(
                "http://vfx.mtime.cn/Video/2019/03/09/mp4/190309153658147087.mp4",
                "https://rv.okjiaoyu.cn/rv_1np7Sm2fho4.low.h264.mp4?vframe/jpg/offset/3.5/w/1280/h/800",
                1000,
                562
            )
        )
        return videoItemList
    }


}