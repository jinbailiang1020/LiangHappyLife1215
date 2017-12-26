package com.happy.jinbailiang.lianghappylife;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.happy.jinbailiang.lianghappylife.service.FloatViewService;
import com.happy.jinbailiang.lianghappylife.voice.VoiceMainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Toast.makeText(this, "mainActivity", Toast.LENGTH_LONG).show();
        requestDrawOverLays();
    }

    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    @TargetApi(Build.VERSION_CODES.M)
    public void requestDrawOverLays() {
        try {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Toast.makeText(this, "can not DrawOverlays", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + MainActivity.this.getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            } else {
                // Already hold the SYSTEM_ALERT_WINDOW permission, do addview or something.
            }
        }catch (NoSuchMethodError e){
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // SYSTEM_ALERT_WINDOW permission not granted...
                Toast.makeText(this, "Permission Denieddd by user.Please Check it in Settings", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Allowed", Toast.LENGTH_SHORT).show();
                // Already hold the SYSTEM_ALERT_WINDOW permission, do addview or something.
            }
        }
    }


    private FloatViewService.MyBind myBind;

    public void showFloatView(View view) {
        Toast.makeText(this, "showFloatView", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this, FloatViewService.class);
        //启动FloatViewService
//        startService(intent);
        ServiceConnection conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myBind = ((FloatViewService.MyBind) service);
                myThread.start();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
//                goWhile = false;
            }
        };
        bindService(intent, conn, BIND_AUTO_CREATE);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    private boolean goWhile = true;
    private Thread myThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (goWhile) {
                if (myBind != null) {
                    myBind.logMessage(simpleDateFormat.format(new Date(System.currentTimeMillis())));
                    try {
                        Thread.sleep(990);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    @Override
    protected void onStop() {
        // 销毁悬浮窗
 /*       Intent intent = new Intent(MainActivity.this, FloatViewService.class);
        //终止FloatViewService
        stopService(intent);*/
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        goWhile = false;
        super.onDestroy();

    }

    public void toVoice(View view) {
        startActivity(new Intent(this,VoiceMainActivity.class));
    }
}
