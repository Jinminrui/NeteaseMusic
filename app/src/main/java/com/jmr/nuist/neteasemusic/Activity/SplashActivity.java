package com.jmr.nuist.neteasemusic.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.jmr.nuist.neteasemusic.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        startMainActivity();
    }

    private void startMainActivity() {
        TimerTask delayTask = new TimerTask() {
            @Override
            public void run() {
                SharedPreferences settings = getSharedPreferences("UserInfo",0);
                Intent intent;
                if(settings.getString("userId","") == ""){
                    intent = new Intent(SplashActivity.this,WelcomeActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }

            }
        };
        Timer timer = new Timer();
        timer.schedule(delayTask,2000);//延时两秒执行 run 里面的操作
    }
}
