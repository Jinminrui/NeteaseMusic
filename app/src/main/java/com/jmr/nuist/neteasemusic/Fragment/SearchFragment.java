package com.jmr.nuist.neteasemusic.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jmr.nuist.neteasemusic.Activity.PlayerActivity;
import com.jmr.nuist.neteasemusic.Adapters.SearchResultAdapter;
import com.jmr.nuist.neteasemusic.R;
import com.jmr.nuist.neteasemusic.Utils.HttpRequestUtil;
import com.jmr.nuist.neteasemusic.entity.SearchResultListItemEntity;
import com.jmr.nuist.neteasemusic.extend.ILoadListener;
import com.jmr.nuist.neteasemusic.extend.LoadListView;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements TextWatcher {
    private ImageView cancel;
    private EditText searchInput;
    private ArrayList<SearchResultListItemEntity> resultList;
    private SearchResultAdapter searchResultAdapter;
    private LoadListView loadListView;
    private ProgressBar progressBar;

    private int offset;
    private String query = "";


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONArray songs = (JSONArray)msg.obj;
            resultList = new ArrayList<>();

            for (int i = 0;i < songs.size(); i++) {
                String songName = songs.getJSONObject(i).getString("name");
                String id = songs.getJSONObject(i).getString("id");
                JSONArray artistArray = songs.getJSONObject(i).getJSONArray("artists");
                String[] artistNames = new String[artistArray.size()];
                for (int j = 0; j<artistArray.size();j++){
                    artistNames[j] = artistArray.getJSONObject(j).getString("name");
                }
                String artists = StringUtils.join(artistNames,"/");

                SearchResultListItemEntity item = new SearchResultListItemEntity(id,songName,artists);
                resultList.add(item);
            }
            loadListView = getActivity().findViewById(R.id.search_result_list);
            searchResultAdapter = new SearchResultAdapter(resultList,getActivity().getApplicationContext());
            loadListView.setAdapter(searchResultAdapter);
            progressBar.setVisibility(View.GONE);
            loadListView.setVisibility(View.VISIBLE);
            scrollLoad();
            setListItemListener();
        }
    };


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cancel = getActivity().findViewById(R.id.search_cancel);
        searchInput = getActivity().findViewById(R.id.search_input);
        progressBar = getActivity().findViewById(R.id.progressBar);


        searchInput.addTextChangedListener(this);

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                query = v.getText().toString();
                if (query.equals("")) {
                    return false;
                }
                search();
                progressBar.setVisibility(View.VISIBLE);
                InputMethodManager manager = ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
                if (manager != null)
                    manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
        });

        /**
         * 取消输入
         */
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setText("");
                loadListView.setVisibility(View.GONE);
                InputMethodManager manager = ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
                if (manager != null)
                    manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }


    /**
     * 第一次搜索
     */
    private void search() {
        HttpRequestUtil httpRequestUtil = HttpRequestUtil.getInstance();
        offset = 0;
        String url = "http://www.jinminrui.cn:3000/search?keywords=" + query + "&offset=" + offset;
        httpRequestUtil.getDataAsyn(url, new HttpRequestUtil.MyCallback() {
            @Override
            public void success(Call call, Response response) throws IOException {
                Log.i("tag", "success");
                String responseData = response.body().string();
                Log.i("searchInfo", responseData);

                JSONObject res = JSONObject.parseObject(responseData);
                JSONArray resultSongs = res.getJSONObject("result").getJSONArray("songs");

                Message message = handler.obtainMessage();
                message.obj = resultSongs;
                handler.sendMessage(message);

            }
            @Override
            public void failed(Call call, IOException e) {

            }
        });
    }

    /**
     * 上拉加载后续结果
     */
    private void scrollLoad(){
        Handler scrollHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONArray songs = (JSONArray)msg.obj;
                for (int i = 0;i < songs.size(); i++) {
                    String songName = songs.getJSONObject(i).getString("name");
                    String id = songs.getJSONObject(i).getString("id");
                    JSONArray artistArray = songs.getJSONObject(i).getJSONArray("artists");
                    String[] artistNames = new String[artistArray.size()];
                    for (int j = 0; j<artistArray.size();j++){
                        artistNames[j] = artistArray.getJSONObject(j).getString("name");
                    }
                    String artists = StringUtils.join(artistNames,"/");

                    SearchResultListItemEntity item = new SearchResultListItemEntity(id,songName,artists);
                    resultList.add(item);
                }
                searchResultAdapter.notifyDataSetChanged();
                loadListView.loadComplete();
            }
        };
        loadListView.setInterface(new ILoadListener() {
            @Override
            public void onLoad() {
                ++offset;
                String url = "http://www.jinminrui.cn:3000/search?keywords=" + query + "&offset=" + offset;
                HttpRequestUtil httpRequestUtil = HttpRequestUtil.getInstance();
                httpRequestUtil.getDataAsyn(url, new HttpRequestUtil.MyCallback() {
                    @Override
                    public void success(Call call, Response response) throws IOException {
                        Log.i("tag", "success");
                        String responseData = response.body().string();
                        Log.i("newsearchlist", responseData);

                        JSONObject res = JSONObject.parseObject(responseData);
                        JSONArray resultSongs = res.getJSONObject("result").getJSONArray("songs");

                        Message message = scrollHandle.obtainMessage();
                        message.obj = resultSongs;
                        scrollHandle.sendMessageDelayed(message,1200);

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
                SearchResultListItemEntity item = (SearchResultListItemEntity)adapter.getItem(position);
                String songId = item.getSongId();
                String songName = item.getSongName();
                String songAuthors = item.getArtists();
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra("id",songId);
                intent.putExtra("songName",songName);
                intent.putExtra("songAuthors",songAuthors);
                startActivity(intent);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (searchInput.length() != 0) {
            cancel.setVisibility(View.VISIBLE);
        } else {
            cancel.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
