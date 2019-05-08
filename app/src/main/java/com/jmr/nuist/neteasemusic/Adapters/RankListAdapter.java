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
import com.jmr.nuist.neteasemusic.entity.RankListItemEntity;

import java.util.ArrayList;

public class RankListAdapter extends BaseAdapter {
    private ArrayList<RankListItemEntity> rankList;
    private LayoutInflater inflater;
    private ImageLoaderUtil imageLoaderUtil;
    private Context context;

    public RankListAdapter(ArrayList<RankListItemEntity> rankList, Context context) {
        this.rankList = rankList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }


    @Override
    public int getCount() {
        return rankList.size();
    }

    @Override
    public Object getItem(int position) {
        return rankList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RankListItemEntity item = rankList.get(position);
        ViewHolder viewHolder;
        if (convertView == null ){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.ranklistitem_layout,null);
            viewHolder.rankImageView = (ImageView) convertView.findViewById(R.id.rank_image);
            viewHolder.firstTextView = (TextView) convertView.findViewById(R.id.first);
            viewHolder.secondTextView = (TextView) convertView.findViewById(R.id.second);
            viewHolder.thirdTextView = (TextView) convertView.findViewById(R.id.third);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.firstTextView.setText(item.getFirst());
        viewHolder.secondTextView.setText(item.getSecond());
        viewHolder.thirdTextView.setText(item.getThird());
        imageLoaderUtil = new ImageLoaderUtil();
        imageLoaderUtil.displayImage(context,item.getCoverImgUrl(),viewHolder.rankImageView);
        return convertView;
    }

    class ViewHolder{
        ImageView rankImageView;
        TextView firstTextView;
        TextView secondTextView;
        TextView thirdTextView;
    }
}
