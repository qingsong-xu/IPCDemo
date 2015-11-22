package com.iboxpay.ipcdemo.client;

import com.iboxpay.ipcdemo.Constants;
import com.iboxpay.ipcdemo.R;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 客户端：需要实现的有以下
 * 1、绑定服务。所以这里我们采用隐形启动
 * 隐式启动是为了更好的演示“多进程”，假设在两个进程中，就可以隐式启动另一个进程中的服务。
 *
 *
 * 2、启动后，点击时再发送
 *
 * 3、要获取服务端的消息，也需要Messenger.在ServiceConnection中new就好.Messenger是用来发消息的~。
 *
 * 这是第一个版本。这样的操作，只允许客户端向服务端放松消息。并没有服务端发送消息到客户端。
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mContentEt;

    private Button mSendBtn;

    private Messenger mMessenger;

    private Message msg;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //在这里new，是因为Messenger(参数)的构造方法，有两种构造方法，分别是：Messenger(Handler target)
//            Create a new Messenger pointing to the given Handler.Messenger(IBinder target)
//            Create a Messenger from a raw IBinder, which had previously been retrieved with getBinder().
            mMessenger = new Messenger(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentEt = (EditText) findViewById(R.id.et_content);
        mSendBtn = (Button) findViewById(R.id.btn_send);

        mSendBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent();
        intent.setAction("com.xuqingsong.hate");

        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send) {
            try {
                //定义一个消息
                msg = Message.obtain(null, Constants.FROM_CLIENT_MESSAGE);
                String msgStr = "This is from Client.哈哈";
                if (mContentEt != null) {
                    msgStr = mContentEt.getText().toString();
                }
                Bundle bundle = new Bundle();
                bundle.putString(Constants.MSG_KEY, msgStr);
                msg.setData(bundle);
                if (mMessenger != null) {
                    mMessenger.send(msg);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } finally {
            }
        }
    }
}
