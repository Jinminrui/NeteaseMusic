package com.jmr.nuist.neteasemusic.Activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.jmr.nuist.neteasemusic.R;
import com.jmr.nuist.neteasemusic.Services.MediaService;
import com.jmr.nuist.neteasemusic.Utils.FastBlurUtil;
import com.jmr.nuist.neteasemusic.Utils.HttpRequestUtil;
import com.jmr.nuist.neteasemusic.extend.CircleImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Response;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, Runnable, ServiceConnection, SeekBar.OnSeekBarChangeListener {
    private String songId;
    private String songName;
    private String songAuthors;

    private TextView songNameView;
    private TextView songArtistsView;
    private ImageView backView;
    private CircleImageView disc;
    private ObjectAnimator discAnimation;


    private boolean isPlaying = true;//0,1 判断是否处于播放状态
    private boolean isPlayable = true;
    //声明服务
    private static final String TAG = MainActivity.class.getSimpleName();
    private MediaService.MusicController mMusicController;
    //使用方法：mMusicController.play();播放   mMusicController.pause();暂停
    private boolean running;
    private SeekBar mSeekBar;
    private ServiceConnection serviceConnection;
    private ImageView playOrPauseIcon;
    private ImageView previousIcon;
    private ImageView nextIcon;


    private TextSwitcher currentTime;
    private TextView totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
        songId = intent.getStringExtra("id");
        songName = intent.getStringExtra("songName");
        songAuthors = intent.getStringExtra("songAuthors");

        backView = findViewById(R.id.back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /**
         * 初始换背景
         */
        initImage();

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(this);

        playOrPauseIcon = findViewById(R.id.play_or_pause);
        previousIcon = findViewById(R.id.prevoius_icon);
        nextIcon = findViewById(R.id.next_icon);
        playOrPauseIcon.setOnClickListener(this);
        previousIcon.setOnClickListener(this);
        nextIcon.setOnClickListener(this);

        totalTime = findViewById(R.id.totalTime);
        serviceConnection = this;
        getSongUrl();
    }


    /**
     * 初始化背景
     */
    private void initImage() {
        Handler bgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONObject songDetails = (JSONObject) msg.obj;
                String bgImgUrl = songDetails.getJSONObject("al").getString("picUrl");
                renderBgImg(bgImgUrl);
                songNameView = findViewById(R.id.songName);
                songArtistsView = findViewById(R.id.songArtists);
                disc = findViewById(R.id.disc);

                songNameView.setText(songName);
                songArtistsView.setText(songAuthors);
                Glide.with(getApplicationContext()).load(bgImgUrl).into(disc);
                setAnimations();
            }
        };
        String url = "http://www.jinminrui.cn:3000/song/detail?ids=" + songId;
        HttpRequestUtil httpRequestUtil = HttpRequestUtil.getInstance();
        httpRequestUtil.getDataAsyn(url, new HttpRequestUtil.MyCallback() {
            @Override
            public void success(Call call, Response response) throws IOException {
                Log.i("tag", "success");
                String responseData = response.body().string();
                Log.i("songDetail", responseData);

                JSONObject res = JSONObject.parseObject(responseData);
                JSONObject songDetails = (JSONObject) res.getJSONArray("songs").get(0);
                Message message = bgHandler.obtainMessage();
                message.obj = songDetails;
                bgHandler.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "请检查网络！", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    /**
     * 渲染处理背景图片（高斯模糊）
     *
     * @param url
     */
    private void renderBgImg(String url) {
        Handler bgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int scaleRatio = 40;
                int blurRadius = 8;
                Bitmap bitmap = (Bitmap) msg.obj;
                Bitmap scaledBitmap = bitmap.createScaledBitmap(bitmap,
                        bitmap.getWidth() / scaleRatio,
                        bitmap.getHeight() / scaleRatio,
                        false);
                Bitmap blurBitmap = FastBlurUtil.doBlur(scaledBitmap, blurRadius, true);
                ImageView bg = findViewById(R.id.bgImgView);
                bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                bg.setImageBitmap(blurBitmap);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap;
                    WindowManager manager = getWindowManager();
                    DisplayMetrics outMetrics = new DisplayMetrics();
                    manager.getDefaultDisplay().getMetrics(outMetrics);
                    int screenWidth = outMetrics.widthPixels; //获取屏幕的宽度
                    int screenHeight = outMetrics.heightPixels; //获取屏幕的高度
                    bitmap = Glide.with(getApplicationContext()).asBitmap().load(url).submit(screenWidth, screenHeight).get();
                    Message message = bgHandler.obtainMessage();
                    message.obj = bitmap;
                    bgHandler.sendMessage(message);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 获取歌曲url并做播放处理
     */
    private void getSongUrl() {
        Handler songUrlHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String songUrl = (String) msg.obj;
                if (songUrl != null && songUrl != "") {
                    Intent mediaIntent = new Intent(getApplicationContext(), MediaService.class);
                    mediaIntent.putExtra("songUrl", songUrl);
                    currentTime = findViewById(R.id.currentTime);
                    startService(mediaIntent); // 增加StartService，来增加后台播放功能

                    // 绑定服务，使用context来绑定
                    // 那个界面需要绑定 就用哪个 Activity
                    // 参数1：Intent               代表需要绑定哪一个Service
                    // 参数2：ServiceConnection    回调接口，可以接收到Service连接成功和断开的回调，成功就可以取到对象。
                    // 绑定服务 参数2就是服务和指定的对象绑定在一起
                    bindService(mediaIntent, serviceConnection, BIND_AUTO_CREATE);
                } else {
                    Toast.makeText(getApplicationContext(),"无版权无法播放",Toast.LENGTH_LONG).show();
                    playOrPauseIcon.setClickable(false);
                    previousIcon.setClickable(false);
                    previousIcon.setClickable(false);
                    isPlayable = false;
                }
            }
        };
        String url = "http://www.jinminrui.cn:3000/song/url?id=" + songId;
        HttpRequestUtil httpRequestUtil = HttpRequestUtil.getInstance();
        httpRequestUtil.getDataAsyn(url, new HttpRequestUtil.MyCallback() {
            @Override
            public void success(Call call, Response response) throws IOException {
                Log.i("tag", "success");
                String responseData = response.body().string();
                Log.i("songUrl", responseData);

                JSONObject res = JSONObject.parseObject(responseData);
                String songUrl = res.getJSONArray("data").getJSONObject(0).getString("url");
                Message message = songUrlHandler.obtainMessage();
                message.obj = songUrl;
                songUrlHandler.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {

            }
        });
    }


    private void setAnimations() {
        discAnimation = ObjectAnimator.ofFloat(disc,"rotation",0,360);
        discAnimation.setDuration(20000);
        discAnimation.setInterpolator(new LinearInterpolator());
        discAnimation.setRepeatCount(ValueAnimator.INFINITE);
        discAnimation.start();
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.play_or_pause:
                if (isPlaying) {
                    playing();
                } else {
                    if (discAnimation != null && discAnimation.isRunning()){
                        discAnimation.cancel();
                        mMusicController.pause();
                        float valueAvatar = (float) discAnimation.getAnimatedValue();
                        discAnimation.setFloatValues(valueAvatar, 360f + valueAvatar);
                    }
                    playOrPauseIcon.setImageResource(R.drawable.timeout);
                    mMusicController.pause();
                    isPlaying = true;
                }
                break;
            default:
                break;
        }
    }

    //播放时动画设置和图片切换
    private void playing() {
        discAnimation.start();
        playOrPauseIcon.setImageResource(R.drawable.play);
        mMusicController.play();//播放
        isPlaying = false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMusicController.setPosition(seekBar.getProgress());
    }

    @Override
    public void run() {
        running = true;
        try {
            while (running) {
                if (mMusicController != null) {
                    long musicDuration = mMusicController.getMusicDuration();
                    final long position = mMusicController.getPosition();
                    final Date dateTotal = new Date(musicDuration);
                    final SimpleDateFormat sb = new SimpleDateFormat("mm:ss");
                    mSeekBar.setMax((int) musicDuration);
                    mSeekBar.setProgress((int) position);
                    currentTime.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    Date date = new Date(position);
                                    String time = sb.format(date);
                                    currentTime.setCurrentText(time);
                                    totalTime.setText(sb.format(dateTotal));
                                }
                            }
                    );
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    protected void onStop() {
        running = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unbindService(this);
        super.onDestroy();
    }


    //-----------------------------
    //服务绑定与解除绑定的回调

    /**
     * 当服务与当前绑定对象，绑定成功，服务onBind方法调用并且返回之后
     * 回调给这个方法
     *
     * @param name
     * @param service IBinder 就是服务 onBind 返回的对象
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mMusicController = ((MediaService.MusicController) service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mMusicController = null;
    }
}
