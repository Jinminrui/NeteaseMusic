package com.jmr.nuist.neteasemusic.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

public class MediaService extends Service {

    private MediaPlayer mPlayer;
    private String songUrl;

    /*
     * 绑定服务的实现流程：
     * 1.服务 onCreate， onBind， onDestroy 方法
     * 2.onBind 方法需要返回一个 IBinder 对象
     * 3.如果 Activity 绑定，Activity 就可以取到 IBinder 对象，可以直接调用对象的方法
     */
    // 相同应用内部不同组件绑定，可以使用内部类以及Binder对象来返回。
    public class MusicController extends Binder {
        public void play() {
            mPlayer.start();//开启音乐
        }

        public void pause() {
            mPlayer.pause();//暂停音乐
        }

        public long getMusicDuration() {
            return mPlayer.getDuration();//获取文件的总长度
        }

        public long getPosition() {
            return mPlayer.getCurrentPosition();//获取当前播放进度
        }

        public void setPosition (int position) {
            mPlayer.seekTo(position);//重新设定播放进度
        }
    }


    /**
     * 当绑定服务的时候，自动回调这个方法
     * 返回的对象可以直接操作Service内部的内容
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicController();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        songUrl = intent.getStringExtra("songUrl");
        if (songUrl != null && songUrl != ""){
            try {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(songUrl);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 任意一次unbindService()方法，都会触发这个方法
     * 用于释放一些绑定时使用的资源
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
        mPlayer = null;
        super.onDestroy();
    }
}
