package com.xinxin.floatingwidget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by 魏欣欣 on 2018/3/14  0014.
 * WeChat : xin10050903
 * Email  : obstinate.coder@foxmail.com
 * Role   : 窗口漂浮控件
 */

public class FloatingWidget extends TextView {

    private int mWidth;
    private int mHeight;

    private int windowWidth;
    private int windowHeight;
    private Handler handler = new Handler(Looper.getMainLooper());

    private boolean xIsReduce;
    private boolean yIsReduce;

    private boolean isStop;

    private int speed = 10;
    private int stepping = 1;//步进，每次移动1px，这是最小的值了，因为不支持float类型
    private ThreadPoolExecutor threadPoolExecutor;

    public FloatingWidget(Context context) {
        this(context, null);
    }

    public FloatingWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
        setVisibility(GONE);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
                isStop = true;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = dp2px(100);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = dp2px(30);
        }
        setMeasuredDimension(mWidth,mHeight);
    }

    public int dp2px(int dp){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics());
    }
    public int sp2px(int sp){
        return (int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                getResources().getDisplayMetrics());
    }

    public void start() {
        if(isStop) return;
        setVisibility(VISIBLE);

        if (windowHeight == 0 || windowWidth == 0) {
            layout(0, 0, mWidth, mHeight);
        } else {
            int l;
            int t;
            int r;
            int b;
            int right = getRight();
            int bottom = getBottom();
            if (xIsReduce) {
                if (right - stepping < mWidth) {
                    r = mWidth;
                    xIsReduce = false;
                } else {
                    r = right - stepping;
                }
            } else {
                if (right + stepping > windowWidth) {
                    r = windowWidth;
                    xIsReduce = true;
                } else {
                    r = right + stepping;
                }
            }

            if (yIsReduce) {
                if (bottom - stepping < mHeight) {
                    b = mHeight;
                    yIsReduce = false;
                } else {
                    b = bottom - stepping;
                }
            } else {
                if (bottom + stepping > windowHeight) {
                    b = windowHeight;
                    yIsReduce = true;
                } else {
                    b = bottom + stepping;
                }
            }

            l = r - mWidth;
            t = b - mHeight;
            layout(l,t,r,b);
        }
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.e("TAG", Thread.currentThread().getName());
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FloatingWidget.this.start();
                    }
                });
            }
        });
    }

    /**
     * 以屏幕分辨率为漂浮范围
     * @param activity 漂浮在的页面，用于获取屏幕分辨率
     */
    public FloatingWidget setActivity(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        windowWidth = metrics.widthPixels;
        windowHeight = metrics.heightPixels;
        return this;
    }

    /**
     * 设置漂浮范围，范围为一个矩形
     * @param widthPixels 矩形的宽度，单位px
     * @param heightPixels 矩形的高度，单位px
     */
    public FloatingWidget setMovingRange(int widthPixels, int heightPixels) {
        windowWidth = widthPixels;
        windowHeight = heightPixels;
        return this;
    }

    /**
     * 设置漂浮移动的速度
     * @param speed 参数范围（1 - 10），数值越大移动速度越快，
     *              建议不要低于5，值过小时，会出现抖动视感
     */
    public FloatingWidget setSpeed(int speed) {
        this.speed *= (11 - speed);
        return this;
    }

    /**
     * 停止
     */
    public void stop(){
        isStop = true;
    }
}
