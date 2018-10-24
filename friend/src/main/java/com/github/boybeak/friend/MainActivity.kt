package com.github.boybeak.friend

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.boybeak.router.AppRouter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun gotoTarget(view: View) {
        val user = User(1, "a", "b")
//        AppRouter.getInstance().gotoTarget(this, user)
    }

}