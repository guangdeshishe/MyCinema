package com.agile.mycinema

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.widget.*

class MediaPlayContentView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs),
    MediaPlayer.OnPreparedListener {

    var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var mMediaPlayer = VideoView(context, attrs)
    var mediaNameView = TextView(context, attrs)
    var mMediaController = MediaController(context)
    var isFullScreen = false
    var mediaName = ""

    private val DEFAULT_SPEED = 10 * 1000//默认快进速度10秒
    var speed = DEFAULT_SPEED

    init {

        setWillNotDraw(false)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.RED
        mediaNameView.setTextColor(Color.WHITE)
        var padding = 10
        mediaNameView.setPadding(padding, padding, padding, padding)

        addView(mMediaPlayer, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        addView(mediaNameView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))

        mMediaPlayer.setMediaController(mMediaController)
        mMediaPlayer.setOnPreparedListener(this);
        setOnClickListener {
            if (isFullScreen) {
                if (mMediaController.isShowing) {
                    if (mMediaPlayer.isPlaying) {
                        mMediaPlayer.pause()
                    } else {
                        mMediaPlayer.resume()
                    }
                } else {
                    mMediaController.show()

                }
            } else {
                switchFullScreen(true)
            }
        }

        post {
            switchFullScreen(false)
        }


    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    fun setVideoURI(url: String, title: String) {
        mMediaPlayer.setVideoURI(Uri.parse(url))
        var fullTitle = "$mediaName    $title"
        mediaNameView.text = fullTitle
    }

    fun stopPlayback() {
        mMediaPlayer.stopPlayback()
    }

    fun pause() {
        mMediaPlayer.pause()
    }

    fun resume() {
        mMediaPlayer.resume()
    }

    fun onBackPressed(): Boolean {
        if (isFullScreen) {
            switchFullScreen(false)
            return true
        }
        stopPlayback()
        return false
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        isSelected = gainFocus
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isSelected) {
            canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mMediaPlayer.start()
    }

    fun switchFullScreen(fullScreen: Boolean) {
        var targetWidth = Constant.SCREEN_WIDTH
        var targetHeight = Constant.SCREEN_HEIGHT
        if (!fullScreen) {
            targetWidth = Constant.SCREEN_WIDTH * 3 / 8
            targetHeight = Constant.SCREEN_HEIGHT * 3 / 8

        }
        var lp = layoutParams
        lp.width = targetWidth
        lp.height = targetHeight
        this.layoutParams = lp

        lp = mMediaPlayer.layoutParams
        lp.width = targetWidth
        lp.height = targetHeight
        mMediaPlayer.layoutParams = lp

        isFullScreen = fullScreen
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

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun log(message: String) {
        Log.d("agilelog", message)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        log("onKeyLongPress3:" + event?.keyCode + "," + event?.action + "," + event?.getRepeatCount())
        var keyCode = event?.keyCode
        if (isFullScreen) {
            if (keyCode == 22) {
                if (event?.action == KeyEvent.ACTION_DOWN) {

                    showToast("开始长按右键" + event?.getRepeatCount())
                    progressForward()
                } else if (event?.action == KeyEvent.ACTION_UP) {
                    showToast("结束长按右键")
                    resetSpeed()
                }
            } else if (keyCode == 21) {
                if (event?.action == KeyEvent.ACTION_DOWN) {

                    showToast("开始长按左键" + event?.getRepeatCount())
                    progressBack()
                } else if (event?.action == KeyEvent.ACTION_UP) {
                    showToast("结束长按左键")
                    resetSpeed()
                }
            } else if (keyCode == 19) {
                showToast("长按上键")
                return true
            } else if (keyCode == 20) {
                showToast("长按下键")
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

}