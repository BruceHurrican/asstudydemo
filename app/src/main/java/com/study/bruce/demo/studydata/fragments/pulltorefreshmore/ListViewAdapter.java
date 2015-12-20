/*
 * Copyright (c) 2015.
 *   This document is Bruce's individual learning the android demo, wherein the use of the code from the Internet, only to use as a learning exchanges.
 *   And where any person can download and use, but not for commercial purposes.
 *   Author does not assume the resulting corresponding disputes.
 *   If you have good suggestions for the code, you can contact BurrceHurrican@foxmail.com
 *   本文件为Bruce's个人学习android的demo, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *   任和何人可以下载并使用, 但是不能用于商业用途。
 *   作者不承担由此带来的相应纠纷。
 *   如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.study.bruce.demo.studydata.fragments.pulltorefreshmore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.study.bruce.demo.DemoApplication;
import com.study.bruce.demo.R;
import com.study.bruce.demo.utils.VolleyBitmapCache;

import java.util.List;

/**
 * Created by BruceHurrican on 2015/12/20.
 */
public class ListViewAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<String> imageUrls;
    private ImageLoader imageLoader;
    private RequestQueue queue;

    public ListViewAdapter(Context context, List<String> imageUrls) {
        this.layoutInflater = LayoutInflater.from(context);
        this.imageUrls = imageUrls;
        queue = DemoApplication.getHttpQueues();
        imageLoader = new ImageLoader(queue, new VolleyBitmapCache());
    }

    @Override
    public int getCount() {
        return null == imageUrls ? 0 : imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        if (null == imageUrls || position > imageUrls.size() - 1) {
            return null;
        }
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = layoutInflater.inflate(R.layout.ptfm_view_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.niv_ptfm_item = (NetworkImageView) convertView.findViewById(R.id.niv_ptfm_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.niv_ptfm_item.setImageResource(R.mipmap.ic_launcher);
        viewHolder.niv_ptfm_item.setDefaultImageResId(R.mipmap.icon_demo);
        viewHolder.niv_ptfm_item.setErrorImageResId(R.mipmap.icon_workdemo);
        viewHolder.niv_ptfm_item.setImageUrl("", imageLoader);
        viewHolder.niv_ptfm_item.setImageUrl(imageUrls.get(position), imageLoader);
        return null;
    }

    static class ViewHolder {
        NetworkImageView niv_ptfm_item;
    }
}
