package com.agile.mycinema


class MyUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {

        e.printStackTrace()
    }
}