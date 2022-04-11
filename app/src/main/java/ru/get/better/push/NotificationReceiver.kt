package ru.get.better.push

import android.content.*
import android.os.Build
import android.os.IBinder
import android.util.Log


class NotificationReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("keke", "receiver")

        kotlin.runCatching {
            val intent1 = Intent(context, NotificationService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent1)
            } else {
                context.startService(intent1)
            }


            context.bindService(intent1, object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    //retrieve an instance of the service here from the IBinder returned
                    //from the onBind method to communicate with
                }

                override fun onServiceDisconnected(name: ComponentName) {}
            }, Context.BIND_AUTO_CREATE)
        }.onFailure {
            Log.d("keke", it.message.toString())
        }
    }
}