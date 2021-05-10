package org.cz.media.player.sample.sample.widget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.Register
import com.cz.android.sample.library.component.document.SampleDocument
import kotlinx.android.synthetic.main.activity_media_controller_sample.*
import org.cz.media.player.sample.R

/**
 * @author Created by cz
 * @date 2020/8/12 9:54 AM
 * @email binigo110@126.com
 *
 * This example shows you the MediaPlayerControlView.
 *
 */
@SampleDocument("control_layout.md")
@Register(title="控制面板",desc = "演示控制面板设计,以及动画")
class MediaControllerSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_controller_sample)

        testButton.setOnClickListener {
            if(controllerLayout.isVisible){
                controllerLayout.hide()
            } else {
                controllerLayout.show()
            }
        }
    }
}