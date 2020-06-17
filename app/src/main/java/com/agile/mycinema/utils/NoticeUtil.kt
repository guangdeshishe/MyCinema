package com.agile.mycinema.utils

import android.widget.Toast

class NoticeUtil {
    companion object {
        fun showToast(message: String) {
            Toast.makeText(Constant.context, message, Toast.LENGTH_LONG).show()
        }
    }
}