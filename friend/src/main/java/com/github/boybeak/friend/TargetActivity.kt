package com.github.boybeak.friend

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.synthetic.main.activity_target.*

class TargetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target)

        val user = intent.getParcelableExtra<User>("user")
        targetText.text = user.name

    }
}
