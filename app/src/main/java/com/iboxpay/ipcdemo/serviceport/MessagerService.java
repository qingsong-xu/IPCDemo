package com.iboxpay.ipcdemo.serviceport;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MessagerService extends Service {

    public MessagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
