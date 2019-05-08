package com.jmr.nuist.neteasemusic.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jmr.nuist.neteasemusic.R;
import com.jmr.nuist.neteasemusic.Utils.HttpRequestUtil;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ImageButton back = this.findViewById(R.id.back);
        final Button btnLogin = this.findViewById(R.id.btnLogin);
        EditText phoneInput = this.findViewById(R.id.phone);
        EditText passwordInput = this.findViewById(R.id.password);
        progressBar = this.findViewById(R.id.progressBar);

        /**
         * 返回
         */
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(LoginActivity.this, WelcomeActivity.class);
                startActivity(backIntent);
                LoginActivity.this.finish();
            }
        });

        /**
         * 点击登录
         */
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneInput.getText().toString();
                String password = passwordInput.getText().toString();

                InputMethodManager manager = ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE));
                if (manager != null) manager.hideSoftInputFromWindow(v.getWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
                login(phone, password);
//                if (login(phone, password)) {
//                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(loginIntent);
//                    LoginActivity.this.finish();
//                } else {
//                    Toast.makeText(getApplicationContext(), "手机号或密码错误！", Toast.LENGTH_LONG).show();
//                }
            }
        });
    }


    public void login(String phone, String password) {
        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "手机号和密码不能为空！", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        String loginUrl = "http://www.jinminrui.cn:3000/login/cellphone?phone=" + phone + "&password=" + password;
        HttpRequestUtil httpRequestUtil = HttpRequestUtil.getInstance();
        httpRequestUtil.getDataAsyn(loginUrl, new HttpRequestUtil.MyCallback() {
            @Override
            public void success(Call call, Response response) throws IOException {
                Log.i("tag", "success");
                String responseData = response.body().string();
                Log.i("loginInfo", responseData);


                JSONObject loginInfo = JSONObject.parseObject(responseData);
                Integer code = loginInfo.getInteger("code");
                if (code != 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"用户名或密码错误！",Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }

                JSONObject userInfo = loginInfo.getJSONObject("profile");
                String userId = userInfo.getString("userId");
                String nickname = userInfo.getString("nickname");
                String avatarUrl = userInfo.getString("avatarUrl");


                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("userId", userId);
                editor.putString("nickname", nickname);
                editor.putString("avatarUrl", avatarUrl);
                editor.commit();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(loginIntent);
                        progressBar.setVisibility(View.GONE);
                        LoginActivity.this.finish();
                        Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void failed(Call call, IOException e) {
                Log.i("tag", "failed");
                Log.i("e", String.valueOf(e));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "登录失败，请检查网络或输入内容！", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

}
