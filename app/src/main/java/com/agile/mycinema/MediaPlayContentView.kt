package com.agile.mycinema

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import com.agile.mycinema.utils.Constant
import com.agile.mycinema.utils.PaintUtil
import kotlinx.android.synthetic.main.media_player_content_view.view.*

class MediaPlayContentView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs),
    MediaPlayer.OnPreparedListener {

    var paintUtil = PaintUtil(this)

    //    var mMediaPlayer = VideoView(context, attrs)
//    var mediaNameView = TextView(context, attrs)
//    var mMediaController = MediaController(context)
    var isFullScreen = false
    var mediaName = ""


    init {
        inflate(context, R.layout.media_player_content_view, this)
        isFocusable = true
//        mediaNameView.setTextColor(Color.WHITE)
//        var padding = 10
//        mediaNameView.setPadding(padding, padding, padding, padding)

//        addView(mMediaPlayer, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
//        addView(mediaNameView, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))

//        mMediaPlayer.setMediaController(mMediaController)

        mMyMediaController.mMediaPlayer = mMediaPlayer
        mMediaPlayer.setOnPreparedListener(this);
        setOnClickListener {
            if (isFullScreen) {
                if (mMyMediaController.isShowing()) {
                    mMyMediaController.handlePause()
                } else {
                    mMyMediaController.show()

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
        mMediaPlayer.start()
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
            paintUtil.onDraw(canvas)
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mMediaPlayer.start()
        mMyMediaController.show()
    }

    fun switchFullScreen(fullScreen: Boolean) {
        var targetWidth = Constant.SCREEN_WIDTH
        var targetHeight = Constant.SCREEN_HEIGHT
        var margin = 0
        if (!fullScreen) {
            targetWidth = Constant.SCREEN_WIDTH * 3 / 8
            targetHeight = Constant.SCREEN_HEIGHT * 3 / 8
            setPadding(
                Constant.MEDIA_PADDING,
                Constant.MEDIA_PADDING,
                Constant.MEDIA_PADDING,
                Constant.MEDIA_PADDING
            )
            if (isFocused) {
                isSelected = true
            }
            margin = Constant.CONTENT_MARGIN
        } else {
            setPadding(
                0,
                0,
                0,
                0
            )
            isSelected = false
            margin = 0
        }
        var lp = layoutParams
        lp.width = targetWidth
        lp.height = targetHeight
        lp = lp as LinearLayout.LayoutParams
        lp.setMargins(margin, margin, margin, margin)
        this.layoutParams = lp

        lp = mMediaPlayer.layoutParams
        lp.width = targetWidth
        lp.height = targetHeight
        mMediaPlayer.layoutParams = lp

        isFullScreen = fullScreen

//        invalidate()
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

//                    showToast("开始长按右键" + event?.getRepeatCount())
                    mMyMediaController.progressForward()
                } else if (event?.action == KeyEvent.ACTION_UP) {
//                    showToast("结束长按右键")
                    mMyMediaController.resetSpeed()
                }
                return true
            } else if (keyCode == 21) {
                if (event?.action == KeyEvent.ACTION_DOWN) {

//                    showToast("开始长按左键" + event?.getRepeatCount())
                    mMyMediaController.progressBack()
                } else if (event?.action == KeyEvent.ACTION_UP) {
//                    showToast("结束长按左键")
                    mMyMediaController.resetSpeed()
                }
                return true
            } else if (keyCode == 19) {
//                showToast("长按上键")
                return true
            } else if (keyCode == 20) {
//                showToast("长按下键")
                return true
            }
//            return true
        }
        return super.dispatchKeyEvent(event)
    }

}