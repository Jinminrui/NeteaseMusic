package com.jmr.nuist.neteasemusic.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.jmr.nuist.neteasemusic.Activity.MusicListActivity;
import com.jmr.nuist.neteasemusic.Activity.WelcomeActivity;
import com.jmr.nuist.neteasemusic.Adapters.MyPlaylistAdapter;
import com.jmr.nuist.neteasemusic.R;
import com.jmr.nuist.neteasemusic.Utils.HttpRequestUtil;
import com.jmr.nuist.neteasemusic.Utils.ImageLoaderUtil;
import com.jmr.nuist.neteasemusic.entity.MyPlaylistItemEntity;
import com.jmr.nuist.neteasemusic.extend.CircleImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment {
    private ImageLoaderUtil imageLoaderUtil;
    private String userId;

    private ArrayList<MyPlaylistItemEntity> list;
    private ListView listView;
    private MyPlaylistAdapter myPlaylistAdapter;


    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * 头部信息异步处理
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Map<String, Object> detials = (Map<String, Object>) msg.obj;
            if (detials.get("userLevel") != null) {
                String userLevel = detials.get("userLevel").toString();
                String backgroundUrl = detials.get("backgroundUrl").toString();
                String followedsCount = detials.get("followedsCount").toString();
                String followsCount = detials.get("followsCount").toString();

                TextView followeds = getActivity().findViewById(R.id.followeds);
                followeds.setText("粉丝 " + followedsCount);
                TextView follows = getActivity().findViewById(R.id.follows);
                follows.setText("关注 " + followsCount + "   |   ");
//                TextView levelView = getActivity().findViewById(R.id.level);
//                levelView.setText("Lv." + userLevel + " ");

                initBackground(backgroundUrl);
            }
        }
    };

    /**
     * 我的歌单列表异步处理
     */
    private Handler playListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONArray myPlaylist = (JSONArray) msg.obj;
            list = new ArrayList<>();
            for (int i = 0; i < myPlaylist.size(); i++) {
                String id = myPlaylist.getJSONObject(i).getString("id"); // 歌单id
                String name = myPlaylist.getJSONObject(i).getString("name"); // 歌单名称
                String imgUrl = myPlaylist.getJSONObject(i).getString("coverImgUrl"); // 歌单图片
                String songCount = myPlaylist.getJSONObject(i).getString("trackCount"); // 歌单中歌曲数量
                String playCount = myPlaylist.getJSONObject(i).getString("playCount"); // 歌单播放次数
                String creator = myPlaylist.getJSONObject(i).getJSONObject("creator").getString("name"); // 歌单创建者
                MyPlaylistItemEntity item = new MyPlaylistItemEntity(id, name, imgUrl,
                        songCount + "首",
                        "by " + creator,
                        "播放" + playCount + "次");
                list.add(item);
            }
            myPlaylistAdapter = new MyPlaylistAdapter(list, getActivity().getApplicationContext());
            listView = getActivity().findViewById(R.id.mine_play_list);
            listView.setAdapter(myPlaylistAdapter);
            setListItemListener();

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        initAvatar(view);
        if (userId != ""){
            getUserDetails();
            getMyPlaylist();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        TextView logout = getActivity().findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void initAvatar(View view) {
        SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", 0);
        userId = settings.getString("userId", "");
        String avatarUrl = settings.getString("avatarUrl", "");
        String nickname = settings.getString("nickname", "未登录");
        CircleImageView avatar = view.findViewById(R.id.avatar);
        TextView nicknameView = view.findViewById(R.id.nickname);
        nicknameView.setText(nickname);
        imageLoaderUtil = new ImageLoaderUtil();
        Glide.with(getActivity()).load(avatarUrl).into(avatar);
    }

    private void getUserDetails() {
        if (userId != "") {
            HttpRequestUtil httpRequestUtil = HttpRequestUtil.getInstance();
            String getUsertDetailsApi = "http://www.jinminrui.cn:3000/user/detail?uid=" + userId;
            httpRequestUtil.getDataAsyn(getUsertDetailsApi, new HttpRequestUtil.MyCallback() {
                @Override
                public void success(Call call, Response response) throws IOException {
                    Log.i("tag", "success");
                    String responseData = response.body().string();
                    Log.i("userDetails", responseData);

                    JSONObject userDetails = JSONObject.parseObject(responseData);
                    String userLevel = userDetails.getString("level");
                    String backgroundUrl = userDetails.getJSONObject("profile").getString("backgroundUrl");
                    String followsCount = userDetails.getJSONObject("profile").getString("follows");
                    String followedsCount = userDetails.getJSONObject("profile").getString("followeds");
                    Map<String, Object> details = new HashMap<>();
                    details.put("userLevel", userLevel);
                    details.put("backgroundUrl", backgroundUrl);
                    details.put("followsCount", followsCount);
                    details.put("followedsCount", followedsCount);
                    Message message = handler.obtainMessage();
                    message.obj = details;
                    handler.sendMessage(message);
                }

                @Override
                public void failed(Call call, IOException e) {
                }
            });
        }
    }

    private void getMyPlaylist() {
        if (userId != "") {
            String url = "http://www.jinminrui.cn:3000/user/playlist?uid=" + userId;
            HttpRequestUtil httpRequestUtil = HttpRequestUtil.getInstance();
            httpRequestUtil.getDataAsyn(url, new HttpRequestUtil.MyCallback() {
                @Override
                public void success(Call call, Response response) throws IOException {
                    Log.i("tag", "success");
                    String responseData = response.body().string();
                    Log.i("myplaylist", responseData);

                    JSONObject res = JSONObject.parseObject(responseData);
                    JSONArray myPlaylist = res.getJSONArray("playlist");
                    Message message = playListHandler.obtainMessage();
                    message.obj = myPlaylist;
                    playListHandler.sendMessage(message);
                }

                @Override
                public void failed(Call call, IOException e) {

                }
            });
        }
    }


    private void setListItemListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Adapter adapter = parent.getAdapter();
                MyPlaylistItemEntity item = (MyPlaylistItemEntity) adapter.getItem(position);
                String itemId = item.getId();
                Intent intent = new Intent(getActivity(), MusicListActivity.class);
                intent.putExtra("id", itemId);
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化背景图片
     *
     * @param url
     */
    private void initBackground(String url) {
        Handler bgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int scaleRatio = 2;
//                int blurRadius = 6;
                Bitmap bitmap = (Bitmap) msg.obj;
                Bitmap scaledBitmap = bitmap.createScaledBitmap(bitmap,
                        bitmap.getWidth() / scaleRatio,
                        bitmap.getHeight() / scaleRatio,
                        false);
//                Bitmap blurBitmap = FastBlurUtil.doBlur(scaledBitmap, blurRadius, true);
                ImageView bg = getActivity().findViewById(R.id.background_img);
                bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                bg.setImageBitmap(scaledBitmap);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap;
                    bitmap = Glide.with(getActivity().getApplicationContext()).asBitmap().load(url).submit().get();
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

}
