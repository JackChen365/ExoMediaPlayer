package org.cz.media.player.sample.sample.widget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.component.document.SampleDocument
import kotlinx.android.synthetic.main.activity_time_bar_sample.*
import org.cz.media.player.base.timebar.TimeBar
import org.cz.media.player.sample.R

/**
 * @author Created by cz
 * @date 2020/8/12 9:54 AM
 * @email binigo110@126.com
 *
 * This example shows you the DefaultTimeBar.
 *
 */
@SampleSourceCode
@SampleDocument("time_bar.md")
@Register(title="拖动条",desc = "演示拖动条各种设置功能等")
class TimeBarSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_bar_sample)
        timeBar1.duration = 60*1000
        timeBar1.setBufferedPosition(40*1000)

        timeBar2.duration = 60*1000
        timeBar2.setBufferedPosition(40*1000)

        timeBar3.duration = 60*1000
        timeBar3.setBufferedPosition(40*1000)

        timeBar1.addListener(object: TimeBar.OnScrubListener {
            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                scrubText.setText("onScrubMove:$position")
            }

            override fun onScrubStart(timeBar: TimeBar, position: Long) {
                scrubText.setText("onScrubStart:$position")
            }

            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                scrubText.setText("onScrubStop:$position canceled:$canceled")
            }

        })
        //Add advertisement group.
        val markGroupTimesMs= longArrayOf(10*1000,30*1000,40*1000)
        timeBar1.setMarkGroupTimesMs(markGroupTimesMs, booleanArrayOf(true,false,false),3)
        timeBar2.setMarkGroupTimesMs(markGroupTimesMs, booleanArrayOf(true,false,false),3)
        testButton.setOnClickListener {
//            timeBar.hideScrubber()
            timeBar1.isEnabled=!timeBar1.isEnabled
            timeBar2.isEnabled=!timeBar2.isEnabled
            timeBar3.isEnabled=!timeBar3.isEnabled
            testButton.text = if(testButton.isSelected) "Disable" else "Enable"
            testButton.isSelected=!testButton.isSelected
        }
    }
}