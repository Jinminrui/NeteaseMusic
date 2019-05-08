package com.jmr.nuist.neteasemusic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jmr.nuist.neteasemusic.R;
import com.jmr.nuist.neteasemusic.entity.MusicListItemEntity;

import java.util.ArrayList;

public class MusicListAdapter extends BaseAdapter {
    private ArrayList<MusicListItemEntity> musicList;
    private LayoutInflater inflater;
    private Context context;


    public MusicListAdapter(ArrayList<MusicListItemEntity> musicList, Context context) {
        this.musicList = musicList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MusicListItemEntity item = musicList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.musiclistitem_layout,null);
            viewHolder.musicIdTextView = (TextView)convertView.findViewById(R.id.music_id);
            viewHolder.musicNameTextView = (TextView) convertView.findViewById(R.id.music_name);
            viewHolder.musicAuthorTextView = (TextView) convertView.findViewById(R.id.author);
            viewHolder.musicAlbumTextView = (TextView) convertView.findViewById(R.id.album);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.musicIdTextView.setText(item.getCount());
        viewHolder.musicNameTextView.setText(item.getMusicName());
        viewHolder.musicAuthorTextView.setText(item.getAuthor());
        viewHolder.musicAlbumTextView.setText(item.getAlbum());
        return convertView;

    }


    class ViewHolder {
        TextView musicNameTextView;
        TextView musicIdTextView;
        TextView musicAuthorTextView;
        TextView musicAlbumTextView;
    }
}
