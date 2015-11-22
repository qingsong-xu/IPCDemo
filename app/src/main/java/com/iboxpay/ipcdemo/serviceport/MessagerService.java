package com.iboxpay.ipcdemo.serviceport;

import com.iboxpay.ipcdemo.Constants;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

/**
 * 这是一个模拟Android进程间通信的机制Demo。IPC，这个类是服务端的类。
 * 服务端需要的：1、一个Service类
 * 2、在Service添加一个Handler对象类、一个Messenger送信人(通过这个Messenger类指定一个Handler,去handle其他现场的Messager)
 */
public class MessagerService extends Service {

    private static final String TAG = MessagerService.class.getSimpleName();

    public MessagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    //    定义一个Messenger
    private Messenger mMessenger = new Messenger(new MessageHandler());


    private static class MessageHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //捕获消息
            switch (msg.what) {
                case Constants.FROM_CLIENT_MESSAGE:
                    Log.e(TAG, msg.getData().getString("msg"));
                    break;

                default:
                    super.handleMessage(msg);
                    break;

            }

        }
    }


}
