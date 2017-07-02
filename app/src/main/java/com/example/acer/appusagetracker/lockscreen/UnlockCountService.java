package com.example.acer.appusagetracker.lockscreen;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;


public class UnlockCountService extends Service{

    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        BroadcastReceiver mReceiver = new UnlockReciever();
        registerReceiver(mReceiver, filter);
    }
}
