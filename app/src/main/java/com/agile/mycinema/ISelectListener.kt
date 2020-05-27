package com.agile.mycinema

import android.view.View

interface ISelectListener {
    fun onSelectChanged(select: Boolean)
    fun getContentView(): View
}