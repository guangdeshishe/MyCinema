package com.agile.mycinema

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agile.mycinema.database.MediaDataHelper

open class BaseActivity : AppCompatActivity() {
    lateinit var mMediaDataHelper: MediaDataHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMediaDataHelper = MediaDataHelper.getInstance(this)
    }

    fun log(message: String) {
        Log.d("agilelog", message)
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}