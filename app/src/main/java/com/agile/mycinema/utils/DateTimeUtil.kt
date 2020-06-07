package com.agile.mycinema.utils

import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtil {
    companion object {
        private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        fun getNowDateTime(): String {
            return dateTimeFormat.format(Date())
        }
    }
}