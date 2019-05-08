package com.jmr.nuist.neteasemusic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jmr.nuist.neteasemusic.R;
import com.jmr.nuist.neteasemusic.entity.SearchResultListItemEntity;

import java.util.ArrayList;

public class SearchResultAdapter extends BaseAdapter {
    private ArrayList<SearchResultListItemEntity> resultList;
    private LayoutInflater inflater;
    private Context context;

    public SearchResultAdapter(ArrayList<SearchResultListItemEntity> resultList, Context context) {
        this.resultList = resultList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Object getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchResultListItemEntity item = resultList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.searchlistitem_layout,null);
            viewHolder.songName = (TextView)convertView.findViewById(R.id.songName);
            viewHolder.artists = (TextView)convertView.findViewById(R.id.artists);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.songName.setText(item.getSongName());
        viewHolder.artists.setText(item.getArtists());
        return convertView;
    }

    class ViewHolder {
        TextView songName;
        TextView artists;
    }
}
