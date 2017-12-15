package com.happy.jinbailiang.lianghappylife.weight;

/**
 * Created by yess on 2017-02-15.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.happy.jinbailiang.lianghappylife.R;


//import com.yihuacomputer.android.caze2.R;


/**
 * 2017-02-15
 *
 * @author jinbailiang
 */
public class MoveByHandView extends View {

    private float downX, downY;//屏幕坐标
    private Context context;
    private int phoneWidth, phoneHeight;
    private FrameLayout.LayoutParams mParams;
    private int statusBarHeight;//手机状态栏高度
    private int viewWidth, viewHeight;
    private int maxLeftMargin, maxTopMargin;//view的左上角点的坐标
    //	private int minLeftMargin,minTopMargin;
    private float parentViewHeight;
    private int leftMargin;
    private int topMargin;
    //	private boolean needInterceptClick;
    private float downRy;
    private float downRx;
    private Canvas canvas;
    private Paint paint;

    public MoveByHandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public MoveByHandView(Context context) {
        super(context);
        this.context = context;
    }

    public MoveByHandView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.context = context;
    }

    @SuppressWarnings("deprecation")
    private void getWindowValues() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        phoneWidth = wm.getDefaultDisplay().getWidth();
        phoneHeight = wm.getDefaultDisplay().getHeight();
        maxLeftMargin = phoneWidth - viewWidth;
//		minLeftMargin = 0;
        maxTopMargin = (int) (phoneHeight - statusBarHeight - viewHeight - parentViewHeight);
//		minTopMargin = 0;
        getStatus_bar_height();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float rx = event.getRawX();
        float ry = event.getRawY();
        parentViewHeight = ry - mParams.topMargin - y - statusBarHeight;//如果parentView上面还有布局，计算该面布局高度
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                downRy = event.getRawY();
                downRx = event.getRawX();
                Log.i("TAG1", "ACTION_DOWN 滑动的坐标点：[" + mParams.leftMargin + "," + mParams.topMargin + "]");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("TAG1", "ACTION_MOVE 滑动的坐标点：[" + mParams.leftMargin + "," + mParams.topMargin + "]");
                leftMargin = (int) (rx - downX);
                topMargin = (int) (ry - downY - statusBarHeight);//jinbailiang margin是针对布局的，所以要把屏幕坐标的距离减去状态栏的距离
                mParams.leftMargin = leftMargin > maxLeftMargin ? maxLeftMargin : leftMargin;
                mParams.topMargin = (int) (topMargin > maxTopMargin ? maxTopMargin : topMargin - parentViewHeight);//jinbailiang - phoneHeight/2
                setLayoutParams(mParams);
                break;

            case MotionEvent.ACTION_UP:
                Log.i("TAG1", "ACTION_UP 滑动的坐标点：[" + mParams.leftMargin + "," + mParams.topMargin + "]");
                mParams.leftMargin = mParams.leftMargin + viewWidth / 2 < phoneWidth / 2 ? 0 : maxLeftMargin;
                mParams.topMargin = mParams.topMargin < (phoneHeight) * 1 / 5 ? (phoneHeight) * 1 / 5 : mParams.topMargin;
                mParams.topMargin = mParams.topMargin > (phoneHeight) * 2 / 3 ? (phoneHeight) * 2 / 3 : mParams.topMargin;
                setLayoutParams(mParams);
                if (needInterceptClick(downRx, event.getRawX(), downRy, event.getRawY())) {
                    return true;//这里返回true,阻止点击事件，up返回super。会发生点击事件
                }
                break;

            default:
                break;
        }
//		return super.onTouchEvent(event);
        return true;
    }

    private boolean needInterceptClick(float downX2, float x, float downY2, float y) {
//		Log.i("TAG1", "x-- = "+(downX2 - x));
        Log.i("TAG1", "needInterceptClick" + (Math.abs(downX2 - x) > 30 || Math.abs(downY2 - y) > 30));
//		Log.i("TAG1", "downX2 = "+downX2+",,x = "+x);
        return Math.abs(downX2 - x) > 30 || Math.abs(downY2 - y) > 30;
    }

    private boolean needIntercept(int leftMargin, int leftMargin2, int topMargin, int topMargin2) {
        return Math.abs(leftMargin - leftMargin2) > 30 || Math.abs(topMargin - topMargin2) > 30;
    }

/*	private boolean needInterceptClick(int leftMargin, int leftMargin2, int topMargin, int topMargin2) {
        return needInterceptClick = Math.abs(leftMargin - leftMargin2)>500 || Math.abs(topMargin - topMargin2)>500;
	}*/

    private void getStatus_bar_height() {
        /**
         * 获取状态栏高度——方法1
         * */
        statusBarHeight = 0;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.e("WangJ", "状态栏-方法1:" + statusBarHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        setBackgroundResource(R.mipmap.ic_launcher);
//		setBackgroundResource(R.drawable.dtc_enginer2);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setText(String s) {
        if (canvas == null || s == null || paint == null) return;
        canvas.drawText(s, 200, 200, paint);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight = h;
        viewWidth = w;
        getWindowValues();
    }

    /*	public void setTopViewHeight(int topViewHeight){
            this.topViewHeight = topViewHeight;
        }
        */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        mParams = (FrameLayout.LayoutParams) getLayoutParams();//注意情景修改
        mParams.leftMargin = maxLeftMargin;
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        paint = new Paint();
        paint.setColor(Color.RED);
    }
}
