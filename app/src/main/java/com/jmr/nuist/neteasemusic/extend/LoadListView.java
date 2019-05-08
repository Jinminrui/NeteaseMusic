package com.jmr.nuist.neteasemusic.extend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.jmr.nuist.neteasemusic.R;

public class LoadListView extends ListView implements AbsListView.OnScrollListener {
    View footer;// 底部布局；
    int totalItemCount;// 总数量；
    int lastVisibleItem;// 最后一个可见的item；
    boolean isLoading;// 正在加载数据；
    ILoadListener iLoadListener;


    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        footer = inflater.inflate(R.layout.footer_layout, null);
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
        this.addFooterView(footer);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        totalItemCount == lastVisibleItem相等时说明滑到了底部
        if (totalItemCount == lastVisibleItem && scrollState == SCROLL_STATE_IDLE) {
            if (!isLoading) {
                isLoading = true;
                footer.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
                if(iLoadListener!=null) {
                    // 加载更多
                    iLoadListener.onLoad();
                }

            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }


    /**
     * 加载完毕
     */
    public void loadComplete(){
        isLoading = false;
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
    }

    /**
     * 加载更多
     */
    public void setInterface(ILoadListener iLoadListener){
        this.iLoadListener = iLoadListener;
    }

}
