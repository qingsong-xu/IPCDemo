package com.iboxpay.ipcdemo.client;

import com.iboxpay.ipcdemo.Constants;
import com.iboxpay.ipcdemo.R;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
 * 这是第二个版本。这个版本允许客户端向服务端发送消息，同时也允许服务端向客户端回复消息。接收消息是需要Handler去捕获的
 * 所以我们想客户端接收服务端的消息，就要在客户端也声明一个Handler，并且注意使用 msg的replyTo属性值：Optional Messenger where replies to
 * this message can be sent。
 * 是用于指定这个消息被那个Messenger接收。这事可以用这个messengersend消息回客户端。
 *
 * 下面请先去看服务端的修改。Messenger不再是用以前声明的那个Messenger了。
 */
public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    private EditText mContentEt;

    private Button mSendBtn;

    private Messenger mMessenger;

    private Message msg;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.FROM_SERVICE_MESSAGE:

                    Toast.makeText(MainActivity2.this,
                            msg.getData().getString(Constants.MSG_KEY_REPLAY), Toast.LENGTH_LONG)
                            .show();
                    break;

                default:
                    super.handleMessage(msg);
                    break;

            }
        }
    };

    //Messenger即有发送的功能，也有被标示是否另一个Messenger发送过来的消息由此Messenger的Hanlder来处理。
    private Messenger mGetReplayMessenger = new Messenger(mHandler);

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
        setContentView(R.layout.activity_main2);

        mContentEt = (EditText) findViewById(R.id.et_content2);
        mSendBtn = (Button) findViewById(R.id.btn_send2);

        mSendBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent();
        intent.setAction("com.xuqingsong.hate2");

        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑服务
        unbindService(mServiceConnection);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send2) {
            try {
                //定义一个消息
                msg = Message.obtain(null, Constants.FROM_CLIENT_MESSAGE);
                String msgStr = "第二种方式  --- This is from Client.哈哈";
                if (mContentEt != null) {
                    msgStr = mContentEt.getText().toString();
                }
                Bundle bundle = new Bundle();
                bundle.putString(Constants.MSG_KEY, msgStr);
                msg.setData(bundle);
//这句话很重要。在这里赋值给了msg.replyTo,在服务端才能获取到该Messenger，并且通过Messenger.send回客户端。
                msg.replyTo
                        = mGetReplayMessenger;//特别注意，这个Messenger是指明服务端发回的messager是由这个Messenger对应的Handler进行处理。
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
