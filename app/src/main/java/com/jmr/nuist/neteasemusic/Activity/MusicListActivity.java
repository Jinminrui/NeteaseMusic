package com.jmr.nuist.neteasemusic.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.jmr.nuist.neteasemusic.Adapters.MusicListAdapter;
import com.jmr.nuist.neteasemusic.R;
import com.jmr.nuist.neteasemusic.Utils.FastBlurUtil;
import com.jmr.nuist.neteasemusic.Utils.HttpRequestUtil;
import com.jmr.nuist.neteasemusic.Utils.ImageLoaderUtil;
import com.jmr.nuist.neteasemusic.entity.MusicListItemEntity;
import com.jmr.nuist.neteasemusic.extend.CircleImageView;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Response;

public class MusicListActivity extends AppCompatActivity {
    private String id;
    ImageLoaderUtil imageLoaderUtil;

    private ImageView coverImgView;
    private TextView playListNameView;
    private CircleImageView creatorAvatarView;
    private TextView creatorNameView;

    private ArrayList<MusicListItemEntity> musicList;
    private MusicListAdapter musicListAdapter;
    private ListView listView;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject playlist = (JSONObject) msg.obj;


            // 提取数据
            String bgurl = playlist.getString("coverImgUrl");
            String playListName = playlist.getString("name");
            String creatorAvatarUrl = playlist.getJSONObject("creator").getString("avatarUrl");
            String creatorName = playlist.getJSONObject("creator").getString("nickname");
            JSONArray tracks = playlist.getJSONArray("tracks");



            initBackground(bgurl);


            // 获取所需要控件
            coverImgView = findViewById(R.id.cover_img);
            playListNameView = findViewById(R.id.playlist_name);
            creatorAvatarView = findViewById(R.id.creator_avatar);
            creatorNameView = findViewById(R.id.creator_name);
            listView = findViewById(R.id.musiclistview);



            imageLoaderUtil = new ImageLoaderUtil();

            // 为控件赋值
            imageLoaderUtil.displayImage(getApplicationContext(),bgurl,coverImgView);
            playListNameView.setText(playListName);
            imageLoaderUtil.displayImage(getApplicationContext(),creatorAvatarUrl,creatorAvatarView);
            creatorNameView.setText(creatorName + "  〉");


            initMusicListView(tracks);
            setListItemListener();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        fetchData();
    }

    private void initMusicListView(JSONArray tracks) {
        musicList = new ArrayList<>();
        for(int i = 0; i < tracks.size(); i++) {
            String musicName = tracks.getJSONObject(i).getString("name");
            String musicId = tracks.getJSONObject(i).getString("id");
            JSONArray authors = tracks.getJSONObject(i).getJSONArray("ar");
            String[] authorNames = new String[authors.size()];
            for (int j = 0; j<authors.size(); j++) {
                authorNames[j] = authors.getJSONObject(j).getString("name");
            }
            String authorContact = StringUtils.join(authorNames,'/');
            String album = tracks.getJSONObject(i).getJSONObject("al").getString("name");
            String alPicUrl = tracks.getJSONObject(i).getJSONObject("al").getString("picUrl");
            int count = i + 1;
            MusicListItemEntity item = new MusicListItemEntity(musicId,musicName, authorContact,album,count+"",alPicUrl);
            musicList.add(item);
        }
        musicListAdapter = new MusicListAdapter(musicList,getApplicationContext());
        listView.setAdapter(musicListAdapter);
    }

    /**
     * 初始化背景图片
     * @param url
     */
    private void initBackground(String url) {
        Handler bgHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int scaleRatio = 10;
                int blurRadius = 6;
                Bitmap bitmap = (Bitmap)msg.obj;
                Bitmap scaledBitmap = bitmap.createScaledBitmap(bitmap,
                        bitmap.getWidth() / scaleRatio,
                        bitmap.getHeight() / scaleRatio,
                        false);
                Bitmap blurBitmap = FastBlurUtil.doBlur(scaledBitmap, blurRadius, true);
                ImageView bg = findViewById(R.id.header_bg);
                bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                bg.setImageBitmap(blurBitmap);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap;
                    bitmap = Glide.with(getApplicationContext()).asBitmap().load(url).submit().get();
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
     * 根据歌单id获取歌单详情
     */
    private void fetchData() {
        String url = "http://www.jinminrui.cn:3000/playlist/detail?id=" + id;
        HttpRequestUtil httpRequestUtil = HttpRequestUtil.getInstance();
        httpRequestUtil.getDataAsyn(url, new HttpRequestUtil.MyCallback() {
            @Override
            public void success(Call call, Response response) throws IOException {
                Log.i("tag", "success");
                String responseData = response.body().string();
                Log.i("detail", responseData);

                JSONObject info = JSONObject.parseObject(responseData);
                JSONObject playlist = info.getJSONObject("playlist");
                Message message = handler.obtainMessage();
                message.obj = playlist;
                handler.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {

            }
        });
    }

    /**
     * 点击单曲跳转到播放页面
     * 鉴于网易的坑人版权问题
     * 点击的时候调用一下判断音乐是否可以播放的接口检查一下。
     */
    private void setListItemListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Adapter adapter = parent.getAdapter();
                MusicListItemEntity item = (MusicListItemEntity)adapter.getItem(position);
                String songId = item.getId();
                String songName = item.getMusicName();
                String songAuthors = item.getAuthor();
                Handler checkHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        JSONObject checkRes = (JSONObject)msg.obj;
                        Log.i("check", checkRes.toJSONString());
                        if (checkRes.getString("success")=="true"){
                            Intent intent = new Intent(MusicListActivity.this, PlayerActivity.class);
                            intent.putExtra("id",songId);
                            intent.putExtra("songName",songName);
                            intent.putExtra("songAuthors",songAuthors);
                            startActivity(intent);

                        } else {

                            Toast.makeText(getApplicationContext(),checkRes.getString("message"),Toast.LENGTH_LONG).show();

                        }

                    }
                };
                String url = "http://www.jinminrui.cn:3000/check/music?id=" + songId;
                HttpRequestUtil httpRequestUtil = HttpRequestUtil.getInstance();
                httpRequestUtil.getDataAsyn(url, new HttpRequestUtil.MyCallback() {
                    @Override
                    public void success(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        JSONObject res = JSONObject.parseObject(responseData);
                        Message message = checkHandler.obtainMessage();
                        message.obj = res;
                        checkHandler.sendMessage(message);
                    }

                    @Override
                    public void failed(Call call, IOException e) {

                    }
                });
//                String alPicUrl = item.getAlPicUrl();
            }
        });
    }
}
