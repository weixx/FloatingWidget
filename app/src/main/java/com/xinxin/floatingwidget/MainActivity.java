package com.xinxin.floatingwidget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private FloatingWidget mFloatingWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFloatingWidget = (FloatingWidget) findViewById(R.id.mFloatingWidget);
        mFloatingWidget.setMovingRange(200,200).setSpeed(10).start();
        mFloatingWidget.setActivity(this).setSpeed(10).start();
    }

    @Override
    protected void onDestroy() {
        mFloatingWidget.stop();
        super.onDestroy();
    }
}
