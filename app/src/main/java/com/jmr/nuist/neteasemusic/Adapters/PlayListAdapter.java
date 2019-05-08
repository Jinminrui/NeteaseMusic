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
import com.jmr.nuist.neteasemusic.entity.PlayListItemEntity;

import java.util.ArrayList;

public class PlayListAdapter extends BaseAdapter {
    private ArrayList<PlayListItemEntity> playList;
    private LayoutInflater inflater;
    private ImageLoaderUtil imageLoaderUtil;
    private Context context;

    public PlayListAdapter(ArrayList<PlayListItemEntity> playList, Context context) {
        this.playList = playList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }



    @Override
    public int getCount() {
        return playList.size();
    }

    @Override
    public Object getItem(int position) {
        return playList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlayListItemEntity item = playList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.playlistitem_layout, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.desc = (TextView) convertView.findViewById(R.id.item_desc);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.item_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(item.getTitle());
        viewHolder.desc.setText(item.getDesc());
        imageLoaderUtil = new ImageLoaderUtil();
        imageLoaderUtil.displayImage(context, item.getImageUrl(),viewHolder.image);

        return convertView;
    }


    class ViewHolder {
        TextView title;
        TextView desc;
        ImageView image;
    }

}
