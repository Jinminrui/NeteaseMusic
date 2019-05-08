package com.jmr.nuist.neteasemusic.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jmr.nuist.neteasemusic.Activity.MusicListActivity;
import com.jmr.nuist.neteasemusic.Adapters.PlayListAdapter;
import com.jmr.nuist.neteasemusic.R;
import com.jmr.nuist.neteasemusic.Utils.HttpRequestUtil;
import com.jmr.nuist.neteasemusic.Utils.ImageLoaderUtil;
import com.jmr.nuist.neteasemusic.entity.PlayListItemEntity;
import com.jmr.nuist.neteasemusic.extend.ILoadListener;
import com.jmr.nuist.neteasemusic.extend.LoadListView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendFragment extends Fragment implements OnBannerListener {

    private Banner banner;
    private ImageLoaderUtil imageLoaderUtil;
    private ArrayList<String> imagePath;
    private ArrayList<String> imageLink;

    private ArrayList<PlayListItemEntity> playList;
    private PlayListAdapter playListAdapter;
    private LoadListView loadListView;

    private int fetchLimit = 10;
    private String before = "";


    public RecommendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        initBanner(view);
        initListView(view);
        return view;
    }


    /**
     * 初始化banner 异步并渲染数据
     *
     * @param view
     */
    private void initBanner(View view) {
        imagePath = new ArrayList<>();
        imageLink = new ArrayList<>();

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONArray images = (JSONArray) msg.obj;
                for (int i = 0; i < images.size(); i++) {
                    JSONObject img = images.getJSONObject(i);
                    imagePath.add(img.getString("imageUrl"));
                    imageLink.add(img.getString("url"));
                }
                renderBanner(view);
            }
        };

        String imgApiUrl = "http://www.jinminrui.cn:3000/banner";
        HttpRequestUtil httpRequestUtil = HttpRequestUtil.getInstance();
        httpRequestUtil.getDataAsyn(imgApiUrl, new HttpRequestUtil.MyCallback() {
            @Override
            public void success(Call call, Response response) throws IOException {
                Log.i("tag", "success");
                String responseData = response.body().string();
                Log.i("banner", responseData);

                JSONObject info = JSONObject.parseObject(responseData);
                JSONArray images = info.getJSONArray("banners");
                Message message = handler.obtainMessage();
                message.obj = images;
                handler.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
            }
        });
    }

    private void renderBanner(View view) {
        imageLoaderUtil = new ImageLoaderUtil();
        banner = view.findViewById(R.id.banner);


        /**
         * 轮播图大小自适应屏幕，因为图片大小固定
         * 所以就直接计算了。
         */
        WindowManager manager = getActivity().getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int screenwidth = outMetrics.widthPixels; //获取屏幕的宽度
        ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();//获取banner组件的参数
        layoutParams.width = screenwidth;
        layoutParams.height = screenwidth * 400 / 1080;

        banner.setLayoutParams(layoutParams); //设置参数
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setImageLoader(imageLoaderUtil);
        banner.setBannerAnimation(Transformer.ZoomOutSlide);
        banner.setBannerTitles(imageLink);
        //设置轮播间隔时间
        banner.setDelayTime(6000);
        //设置是否为自动轮播，默认是true
        banner.isAutoPlay(true);
        //设置指示器的位置，小点点，居中显示
        banner.setIndicatorGravity(BannerConfig.CENTER);

        //设置图片加载地址
        banner.setImages(imagePath)
                //轮播图的监听
                .setOnBannerListener(this)
                //开始调用的方法，启动轮播图。
                .start();
    }


    /**
     * 加载歌单列表
     * 请求歌单数据，利用上一页最后一项的updateTime 请求下一页数据。
     *
     * @param view
     */
    private void initListView(View view) {
        playList = new ArrayList<>();
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONArray list = (JSONArray) msg.obj;
                for (int i = 0; i < list.size(); i++) {
                    String id = list.getJSONObject(i).getString("id");
                    String title = list.getJSONObject(i).getString("name");
                    String desc = list.getJSONObject(i).getString("description");
                    String imageUrl = list.getJSONObject(i).getString("coverImgUrl");
                    String updateTime = list.getJSONObject(i).getString("updateTime");
                    PlayListItemEntity item = new PlayListItemEntity(id, title, imageUrl, desc, updateTime);
                    playList.add(item);
                    if (i == list.size() - 1) {
                        before = item.getUpdateTime();
                    }
                }
                ProgressBar progressBar = getActivity().findViewById(R.id.listload);
                progressBar.setVisibility(View.GONE);
                loadListView = (LoadListView) getActivity().findViewById(R.id.playlistview);
                playListAdapter = new PlayListAdapter(playList, getActivity().getApplicationContext());
                loadListView.setAdapter(playListAdapter);
                scrollLoad();
                setListItemListener();
            }
        };

        String playListApi = "http://www.jinminrui.cn:3000/top/playlist/highquality?" + "limit=" + fetchLimit;
        HttpRequestUtil httpRequestUtil = HttpRequestUtil.getInstance();
        httpRequestUtil.getDataAsyn(playListApi, new HttpRequestUtil.MyCallback() {
            @Override
            public void success(Call call, Response response) throws IOException {
                Log.i("tag", "success");
                String responseData = response.body().string();
                Log.i("playlist", responseData);

                JSONObject info = JSONObject.parseObject(responseData);
                JSONArray playlist = info.getJSONArray("playlists");
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
     * 上拉加载的逻辑
     */
    private void scrollLoad() {
        Handler scrollHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONArray list = (JSONArray) msg.obj;
                for (int i = 0; i < list.size(); i++) {
                    String id = list.getJSONObject(i).getString("id");
                    String title = list.getJSONObject(i).getString("name");
                    String desc = list.getJSONObject(i).getString("description");
                    String imageUrl = list.getJSONObject(i).getString("coverImgUrl");
                    String updateTime = list.getJSONObject(i).getString("updateTime");
                    PlayListItemEntity item = new PlayListItemEntity(id, title, imageUrl, desc, updateTime);
                    playList.add(item);
                    if (i == list.size() - 1) {
                        before = item.getUpdateTime();
                    }
                }
                playListAdapter.notifyDataSetChanged();
                loadListView.loadComplete();
            }
        };
        /**
         * 更加上一页最后一项的updateTime再次发起请求
         */
        loadListView.setInterface(new ILoadListener() {
            @Override
            public void onLoad() {
                String playListApi = "http://www.jinminrui.cn:3000/top/playlist/highquality?before=" + before + "&limit=" + fetchLimit;
//                Log.i("api", playListApi);
                HttpRequestUtil httpRequestUtil = HttpRequestUtil.getInstance();
                httpRequestUtil.getDataAsyn(playListApi, new HttpRequestUtil.MyCallback() {
                    @Override
                    public void success(Call call, Response response) throws IOException {
                        Log.i("tag", "success");
                        String responseData = response.body().string();
                        Log.i("newplaylist", responseData);

                        JSONObject info = JSONObject.parseObject(responseData);
                        JSONArray playlist = info.getJSONArray("playlists");
                        Message message = scrollHandler.obtainMessage();
                        message.obj = playlist;
                        scrollHandler.sendMessageDelayed(message, 1200);
                    }

                    @Override
                    public void failed(Call call, IOException e) {

                    }
                });
            }
        });
    }


    private void setListItemListener() {
        loadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Adapter adapter = parent.getAdapter();
                PlayListItemEntity item = (PlayListItemEntity)adapter.getItem(position);
                String itemId = item.getId();
                Intent intent = new Intent(getActivity(),MusicListActivity.class);
                intent.putExtra("id",itemId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void OnBannerClick(int position) {

    }
}
