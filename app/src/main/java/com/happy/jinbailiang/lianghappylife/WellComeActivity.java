package com.happy.jinbailiang.lianghappylife;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.happy.jinbailiang.lianghappylife.base.BaseActivity;

/**
 * Created by yess on 2017-03-30.
 */

public class WellComeActivity extends BaseActivity {
    private ImageView iv_wellcome;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);
        Toast.makeText(WellComeActivity.this, "startActivity", Toast.LENGTH_SHORT).show();
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WellComeActivity.this, "startActivity____", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WellComeActivity.this, MainActivity.class));
                finish();
            }
        }, 800);
    }


}
