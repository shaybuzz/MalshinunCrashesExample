package com.sw.malshinuncrashesexample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.other_activity.*
import java.lang.IllegalStateException
import java.lang.RuntimeException


class OtherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.other_activity)

        btnThrowException.setOnClickListener {
            throw IllegalStateException("some invalid state exception")
        }

        btnThrow2.setOnClickListener {
            throw RuntimeException("some run time exception")
        }

        btnThrow3.setOnClickListener {
            throw NullPointerException("some null pointer exception")
        }

        btnCatchException.setOnClickListener {
            try {
                throw IllegalArgumentException("no argument")
            }catch (exception:IllegalArgumentException){
                Log.e("OtherActivity", "caught exception ${exception.message} $exception")
            }
        }
    }
}