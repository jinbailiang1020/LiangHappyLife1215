package com.happy.jinbailiang.lianghappylife.service;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happy.jinbailiang.lianghappylife.R;

public class FloatViewService extends Service {

    private static final String TAG = "FloatViewService";
    //定义浮动窗口布局
    private FrameLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

    private TextView moveByHandView;
    private LinearLayout ll_float;
    private Handler handler;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        createFloatView();
    }

    @SuppressWarnings("static-access")
    @SuppressLint("InflateParams")
    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        //通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //设置window type
        wmParams.type = LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 152;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (FrameLayout) inflater.inflate(R.layout.float_layout, null);

        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮
        ll_float = (LinearLayout) mFloatLayout.findViewById(R.id.ll_float);
        moveByHandView = (TextView) mFloatLayout.findViewById(R.id.alert_window_imagebtn);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //设置监听浮动窗口的触摸移动

        ll_float.setOnTouchListener(new View.OnTouchListener() {

            boolean isClick;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        moveByHandView.setBackgroundResource(R.mipmap.ic_launcher);
                        isClick = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isClick = true;
                        // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        wmParams.x = (int) event.getRawX()
                                - moveByHandView.getMeasuredWidth() / 2;
                        // 减25为状态栏的高度
                        wmParams.y = (int) event.getRawY()
                                - moveByHandView.getMeasuredHeight() / 2 - 75;
                        // 刷新
                        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                        return true;
                    case MotionEvent.ACTION_UP:
//                        moveByHandView.setBackgroundResource(R.mipmap.ic_launcher);
//                        return isClick;// 此处返回false则属于移动事件，返回true则释放事件，可以出发点击否。
                        return true;

                    default:
                        break;
                }
                return false;
            }
        });


        ll_float.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FloatViewService.this, "一百块都不给我！", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatLayout != null) {
            //移除悬浮窗口
//            mWindowManager.removeView(mFloatLayout);//todo
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("bind", "onBind");
        return new MyBind();
    }

    public class MyBind extends Binder {
        public void logMessage(final String msg) {
            Log.e("testlog", msg);
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    moveByHandView.setText(msg);
                }
            });
        }
    }

    private Handler getHandler() {
        if (handler != null) {
        } else {
            handler = new Handler(getMainLooper());
        }
        return handler;
    }
}