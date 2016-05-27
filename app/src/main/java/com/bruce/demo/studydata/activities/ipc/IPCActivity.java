package com.bruce.demo.studydata.activities.ipc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bruce.demo.base.BaseActivity;
import com.bruceutils.utils.LogUtils;

/**
 * 进程间通信
 * Created by hrk on 16/5/27.
 */
public class IPCActivity extends BaseActivity {
    private MyServiceAIDL myServiceAIDL;
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myServiceAIDL = MyServiceAIDL.Stub.asInterface(service);
            try {
                LogUtils.i("ipc execute");
                myServiceAIDL.showTxt("ipc ok");
                int result = myServiceAIDL.calculate(32, 45);
                LogUtils.i("" + result);
                Toast.makeText(IPCActivity.this, "result:" + result, Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public String getTAG() {
        return IPCActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        Button button = new Button(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setText("开启IPC服务");
        button.setGravity(Gravity.CENTER);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IPCActivity.this, MyIPCService.class);
                startService(intent);

                Intent intent1 = new Intent(IPCActivity.this, MyIPCService.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(MyIPCService.FLAG, true);
                intent1.putExtras(bundle);
                bindService(intent1, sc, BIND_AUTO_CREATE);
            }
        });

        linearLayout.addView(button);
        setContentView(linearLayout);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, MyIPCService.class));
        unbindService(sc);
        super.onDestroy();
    }
}
