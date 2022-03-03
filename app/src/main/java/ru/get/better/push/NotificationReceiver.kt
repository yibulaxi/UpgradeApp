package ru.get.better.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {



    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("keke", "receiver")
        val intent1 = Intent(context, NotificationService::class.java)
        context.startService(intent1)
    }
}