package com.jmr.nuist.neteasemusic.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jmr.nuist.neteasemusic.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ImageButton qqBtn = this.findViewById(R.id.qqBtn);
        ImageButton wxBtn = this.findViewById(R.id.wxBtn);

        Button toLogin = this.findViewById(R.id.toLogin);

        TextView visiter = this.findViewById(R.id.visiterLogin);

        /**
         * 监听触摸时间，实现按钮的active效果
         */
        qqBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    ((ImageButton)v).setImageDrawable(getDrawable(R.drawable.qq_active));
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    ((ImageButton)v).setImageDrawable(getDrawable(R.drawable.qq));
                }
                return false;
            }
        });
        wxBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    ((ImageButton)v).setImageDrawable(getDrawable(R.drawable.wx_active));
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    ((ImageButton)v).setImageDrawable(getDrawable(R.drawable.wx));
                }
                return false;
            }
        });


        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLoginIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(toLoginIntent);
                WelcomeActivity.this.finish();
            }
        });

        visiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(toMain);
                WelcomeActivity.this.finish();
            }
        });



    }
}
