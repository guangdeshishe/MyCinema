package com.agile.mycinema

import android.content.Context
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.VideoView
import kotlinx.android.synthetic.main.my_media_controller_view.view.*
import java.lang.ref.WeakReference
import java.util.*

class MyMediaController(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs),
    SeekBar.OnSeekBarChangeListener {
    lateinit var mMediaPlayer: VideoView

    private val DEFAULT_SPEED = 10 * 1000//默认快进速度10秒
    var speed = DEFAULT_SPEED

    //将长度转换为时间
    var mFormatBuilder = StringBuilder()
    var mFormatter: Formatter = Formatter(mFormatBuilder, Locale.getDefault())

    val mainHandler = MyHandler(this)

    companion object {
        class MyHandler(controller: MyMediaController) :
            android.os.Handler(Looper.getMainLooper()) {
            var weakReference = WeakReference<MyMediaController>(controller)
            override fun handleMessage(msg: Message) {
                var controller = weakReference.get()
                controller?.updateProgress()
            }
        }
    }

    init {
        inflate(context, R.layout.my_media_controller_view, this)
        mPlayProgressView.setOnSeekBarChangeListener(this)
        mPlayControllerPause.setOnClickListener {
            handlePause()
        }
        mPlayControllerFastForward.setOnClickListener {
            progressForward()
        }
        mPlayControllerFastRewind.setOnClickListener {
            progressBack()
        }

    }

    fun handlePause() {
        if (mMediaPlayer.isPlaying) {
            (mPlayControllerPause as ImageView).setImageResource(R.drawable.ic_media_controller_play)
            mMediaPlayer.pause()
        } else {
            (mPlayControllerPause as ImageView).setImageResource(R.drawable.ic_media_controller_pause)
            mMediaPlayer.start()
        }
    }

    fun isShowing(): Boolean {
        return visibility == View.VISIBLE
    }

    fun show() {
        show(3 * 1000)
    }

    fun show(timeout: Long) {
        visibility = View.VISIBLE
        updateProgress()
        object : Thread() {
            override fun run() {
                try {
                    while (mMediaPlayer.isPlaying && isShowing()) {
                        // 如果正在播放，没0.5.毫秒更新一次进度条
                        mainHandler.sendEmptyMessage(0)
                        sleep(500)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
        postDelayed({
            hide()
        }, timeout)
    }

    fun updateProgress() {
        mCurrentTime.text = stringForTime(mMediaPlayer.currentPosition)
        mEndTime.text = stringForTime(mMediaPlayer.duration)
        mPlayProgressView.max = mMediaPlayer.duration
        mPlayProgressView.progress = mMediaPlayer.currentPosition
    }

    fun hide() {
        visibility = View.GONE
    }

    //将长度转换为时间
    private fun stringForTime(timeMs: Int): String? {
        val totalSeconds = timeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        mFormatBuilder.setLength(0)
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            mMediaPlayer.seekTo(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    //前进
    fun progressForward() {
        val position: Int = mMediaPlayer.currentPosition//当前时长
        val duration: Int = mMediaPlayer.duration//总的时长
        var targetPosition = position + speed
        if (targetPosition > duration) {
            targetPosition = duration
        }
        mMediaPlayer.seekTo(targetPosition)
        speed += DEFAULT_SPEED
    }

    fun resetSpeed() {
        speed = DEFAULT_SPEED
    }

    //后退
    fun progressBack() {
        val position: Int = mMediaPlayer.currentPosition//当前时长
        val duration: Int = mMediaPlayer.duration//总的时长
        var targetPosition = position - speed
        if (targetPosition < 0) {
            targetPosition = 0
        }
        mMediaPlayer.seekTo(targetPosition)
        speed += DEFAULT_SPEED
    }
}