package com.happy.jinbailiang.lianghappylife;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.happy.jinbailiang.lianghappylife.base.BaseActivity;

/**
 * Created by yess on 2017-03-30.
 */

public class WellComeActivity extends BaseActivity {
    private ImageView iv_wellcome;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_wellcome);
        initView();
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                WellComeActivity.this.startActivity(new Intent(WellComeActivity.this,MainActivity.class));
                WellComeActivity.this.finish();
            }
        },2000);
    }

    private void initView() {
        iv_wellcome = (ImageView) findViewById(R.id.iv_wellcome);
    }
}
