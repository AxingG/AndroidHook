package com.dailyyoga.androidhook;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author: ZhaoJiaXing@gmail.com
 * @created on: 2019/12/5 10:51
 * @description:
 */
public class TargetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
    }
}