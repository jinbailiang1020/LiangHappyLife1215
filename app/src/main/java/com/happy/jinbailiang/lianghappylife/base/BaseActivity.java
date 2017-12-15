package com.happy.jinbailiang.lianghappylife.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.happy.jinbailiang.lianghappylife.R;

/**
 * Created by yess on 2017-03-30.
 */

public abstract  class BaseActivity extends AppCompatActivity implements  BaseMVPView {

    private ProgressDialog dialog ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAbstract();
    }


    private void initAbstract() {
        dialog = new ProgressDialog(this);

    }


    @Override
    public void showDialog(String msg) {
        if(msg == null){
            dialog.setMessage(getString(R.string.wait));
        }else{
            dialog.setMessage(msg);
        }
        dialog.show();
    }

    @Override
    public void hideDialog() {
        dialog.dismiss();
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
