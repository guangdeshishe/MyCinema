package com.agile.mycinema

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import android.widget.TextView

class AnimatorProUtil {
    companion object {
        fun startTextSizeAnimator(view: TextView, from: Float, to: Float) {
            var animator = ValueAnimator.ofFloat(from * 1000, to * 1000)
            animator.addUpdateListener {
                var diff = (it.animatedValue) as Float / 1000f
                view.textSize = diff
            }
            animator.duration = 300
            animator.interpolator = LinearInterpolator()
            animator.start()
        }
    }
}