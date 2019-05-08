package com.jmr.nuist.neteasemusic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmr.nuist.neteasemusic.R;
import com.jmr.nuist.neteasemusic.Utils.ImageLoaderUtil;
import com.jmr.nuist.neteasemusic.entity.MyPlaylistItemEntity;

import java.util.ArrayList;

public class MyPlaylistAdapter extends BaseAdapter {
    ArrayList<MyPlaylistItemEntity> list;
    private LayoutInflater inflater;
    private ImageLoaderUtil imageLoaderUtil;
    private Context context;

    public MyPlaylistAdapter(ArrayList<MyPlaylistItemEntity> list, Context context) {
        this.list = list;
        this.context = context;
        this.inflater =  LayoutInflater.from(context);;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyPlaylistItemEntity item = list.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.myplaylistitem_layout, null);
            viewHolder.nameView = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.songNumView = (TextView) convertView.findViewById(R.id.songNums);
            viewHolder.creatorView = (TextView) convertView.findViewById(R.id.creator);
            viewHolder.playCountView = (TextView)convertView.findViewById(R.id.playCount);
            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.item_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.nameView.setText(item.getName());
        viewHolder.songNumView.setText(item.getSongNums());
        viewHolder.creatorView.setText(item.getCreator());
        viewHolder.playCountView.setText(item.getPlayCount());
        imageLoaderUtil = new ImageLoaderUtil();
        imageLoaderUtil.displayImage(context, item.getImageUrl(),viewHolder.imageView);

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView nameView;
        TextView songNumView;
        TextView creatorView;
        TextView playCountView;
    }
}
