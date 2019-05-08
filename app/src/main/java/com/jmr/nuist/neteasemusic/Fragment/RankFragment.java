package com.jmr.nuist.neteasemusic.Fragment;


import android.content.Intent;
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
import android.widget.ListView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jmr.nuist.neteasemusic.Activity.MusicListActivity;
import com.jmr.nuist.neteasemusic.Adapters.PlayListAdapter;
import com.jmr.nuist.neteasemusic.R;
import com.jmr.nuist.neteasemusic.Utils.HttpRequestUtil;
import com.jmr.nuist.neteasemusic.entity.PlayListItemEntity;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankFragment extends Fragment {


    private ArrayList<PlayListItemEntity> playList;
    private PlayListAdapter playListAdapter;
    private ListView listView;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONArray list = (JSONArray)msg.obj;
            playList = new ArrayList<>();
            for (int i = 0; i < list.size();i++){
                String itemId = list.getJSONObject(i).getString("id");
                String rankName = list.getJSONObject(i).getString("name");
                String desc = list.getJSONObject(i).getString("description");
                String coverImgUrl = list.getJSONObject(i).getString("coverImgUrl");
                PlayListItemEntity item = new PlayListItemEntity(itemId,rankName,coverImgUrl,desc);
                playList.add(item);
            }
            playListAdapter = new PlayListAdapter(playList,getActivity().getApplicationContext());
            listView = getActivity().findViewById(R.id.ranklistview);
            listView.setAdapter(playListAdapter);
            setListItemListener();

            ProgressBar pb = getActivity().findViewById(R.id.rank_pb);
            pb.setVisibility(View.GONE);
        }
    };


    public RankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank, container, false);
        fetchTopList();
        return view;
    }


    /**
     * 请求所有排行榜数据
     */
    private void fetchTopList() {
        String url = "http://www.jinminrui.cn:3000/toplist";
        HttpRequestUtil httpRequestUtil = HttpRequestUtil.getInstance();
        httpRequestUtil.getDataAsyn(url, new HttpRequestUtil.MyCallback() {
            @Override
            public void success(Call call, Response response) throws IOException {
                Log.i("tag", "success");
                String responseData = response.body().string();
                Log.i("toplist", responseData);

                JSONObject resJson = JSONObject.parseObject(responseData);
                JSONArray list = resJson.getJSONArray("list");
                Message message = handler.obtainMessage();
                message.obj = list;
                handler.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {

            }
        });
    }

    private void setListItemListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


}
