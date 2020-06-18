package com.agile.mycinema.detail

import android.content.Context
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter

class MyMediaController {
    lateinit var mMediaPlayer: SimpleExoPlayer
    lateinit var mController: PlayerView

    private val DEFAULT_SPEED = 10 * 1000//默认快进速度10秒
    var speed = DEFAULT_SPEED

    companion object {
        fun getExoPlayer(context: Context): SimpleExoPlayer {
            // 创建带宽
            var bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()

            // 创建轨道选择工厂
            var videoTrackSelectionFactory: TrackSelection.Factory =
                AdaptiveTrackSelection.Factory(bandwidthMeter)

            // 创建轨道选择器实例
            var trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            return SimpleExoPlayer.Builder(context).setTrackSelector(trackSelector).build()
        }
    }

    fun handlePause() {
        show()
        mMediaPlayer.playWhenReady = !mMediaPlayer.isPlaying
    }

    fun isShowing(): Boolean {
        return mController.isControllerVisible
    }

    fun show() {
        show(3 * 1000)
    }

    private fun show(timeout: Long) {
        mController.controllerShowTimeoutMs = timeout.toInt()
        mController.showController()
    }

    //前进
    fun progressForward() {
        show()
        val position = mMediaPlayer.currentPosition//当前时长
        val duration = mMediaPlayer.duration//总的时长
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
        show()
        val position = mMediaPlayer.currentPosition//当前时长
        var targetPosition = position - speed
        if (targetPosition < 0) {
            targetPosition = 0
        }
        mMediaPlayer.seekTo(targetPosition)
        speed += DEFAULT_SPEED
    }
}