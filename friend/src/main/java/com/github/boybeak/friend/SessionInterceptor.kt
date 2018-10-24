package com.github.boybeak.friend

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import com.github.boybeak.router.Interceptor
import java.util.*

class SessionInterceptor : Interceptor() {
    override fun onIntercept(context: Context, it: Intent): Boolean {
        val sp = context.getSharedPreferences("account", Context.MODE_PRIVATE)
        val account = sp.getString("account", "");
        if (TextUtils.isEmpty(account)) {
            val account = "${Random().nextInt()}"
            AlertDialog.Builder(context)
                .setTitle("Add Account")
                .setMessage("make account $account")
                .setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        sp.edit().putString("account", account).apply()
                    }
                })
                .show()
            return true
        }
        return false
    }
}