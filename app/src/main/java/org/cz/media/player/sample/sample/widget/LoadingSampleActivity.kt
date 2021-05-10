package org.cz.media.player.sample.sample.widget

import android.graphics.drawable.Animatable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.cz.android.sample.api.Register
import kotlinx.android.synthetic.main.activity_loading_sample.*
import org.cz.media.player.sample.R

@Register(title="加载动画",desc = "演示视频缓冲加载状态")
class LoadingSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_sample)

        val drawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.player_loading_anim)
        imageView.setImageDrawable(drawableCompat)

        imageView.setOnClickListener {
            val animatable=imageView.drawable as Animatable
            if(!animatable.isRunning){
                animatable.start()
            } else {
                animatable.stop()
            }
        }

        startButton.setOnClickListener {
            val animatable=imageView.drawable as Animatable
            animatable.start()
        }

        stopButton.setOnClickListener {
            val animatable=imageView.drawable as Animatable
            animatable.stop()
        }

    }
}