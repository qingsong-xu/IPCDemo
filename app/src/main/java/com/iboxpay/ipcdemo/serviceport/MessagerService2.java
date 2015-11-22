package com.iboxpay.ipcdemo.serviceport;

import com.iboxpay.ipcdemo.Constants;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * 这是一个模拟Android进程间通信的机制Demo。IPC，这个类是服务端的类。
 * 服务端需要的：1、一个Service类
 * 2、在Service添加一个Handler对象类、一个Messenger送信人(通过这个Messenger类指定一个Handler,去handle其他现场的Messager)
 */
public class MessagerService2 extends Service {

    private static final String TAG = MessagerService2.class.getSimpleName();

    public MessagerService2() {
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
                    //这里只比第一种方式多加了一个发送功能
                    sendToClient(msg);
                    break;

                default:
                    super.handleMessage(msg);
                    break;

            }

        }

        private void sendToClient(Message msg) {
            //回复的话，要另外给一个新的Messenger,这个是从客户端的replayTo赋值过来的。
            Messenger client = msg.replyTo;
            Message serviceMsg = Message.obtain(null, Constants.FROM_SERVICE_MESSAGE);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.MSG_KEY_REPLAY, "孩纸，别为了一个不爱你的女孩子伤心了");

            serviceMsg.setData(bundle);

            try {
                //用从客户端赋值的replayTo得到的Messenger来发送消息，这样客户端才能接到。
                client.send(serviceMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


}
