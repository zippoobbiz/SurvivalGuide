/*
 * Copyright (C) 2013 47 Degrees, LLC
 *  http://47deg.com
 *  hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.assignment.xiaoduo.survivalguide.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.entities.PackageItem;
import com.assignment.xiaoduo.survivalguide.helpers.ImageLoader;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import java.util.List;


public class PackageAdapter extends BaseAdapter {

    private List<PackageItem> data;
    private Context context;
    private ImageLoader mLoader;

    public PackageAdapter(Context context, List<PackageItem> data) {
        this.context = context;
        this.data = data;
        mLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public PackageItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    @Override
//    public boolean isEnabled(int position) {
//        if (position == 2) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PackageItem item = getItem(position);
        ViewHolder holder = new ViewHolder();
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = li.inflate(R.layout.item_article, parent, false);
        holder.ll_catalogntags = (TextView) convertView.findViewById(R.id.tags);
//            holder.rl_background.setBackgroundColor(colors[colorPos]);
        holder.tv_title = (TextView) convertView.findViewById(R.id.title);
        holder.tv_time = (TextView) convertView.findViewById(R.id.time);
        holder.tv_author = (TextView) convertView.findViewById(R.id.author);
        holder.tv_abstract = (TextView) convertView.findViewById(R.id.abstracts);
        holder.iv_potrait = (ImageView) convertView.findViewById(R.id.potrait);

        String url = LocalConfiguration.getPicUrl() + LocalConfiguration.image_url + "user"
                + item.getAuthorID() + "/portrait";
        mLoader.DisplayImage(url, holder.iv_potrait);
//        initColor(holder);
        if (item.isMarked()) {
            //when a item has been seen
            holder.tv_title.setAlpha((float) 0.6);
        } else {
//            holder.tv_title.setTextColor(LocalConfiguration.currentTheme.getPostTitleColor());
//            holder.tv_abstract.setTextColor(LocalConfiguration.currentTheme.getPostContentColor());
        }



        holder.tv_title.setText(item.getTitle());
        holder.tv_time.setText(""+item.getTime()+"  "+item.getReply()+"  "+item.getThumb()+"  "+item.getViewed()+"");
        holder.tv_author.setText(item.getAuthor());
        holder.tv_abstract.setText(item.getAbstracts());
        holder.ll_catalogntags.setText(item.getCatelog() + item.getSub_title());

        return convertView;
    }

//    public void initColor(ViewHolder holder) {
//        holder.tv_reply.setTextColor(LocalConfiguration.currentTheme.getPostReplyColor());
//
//        holder.tv_time.setTextColor(LocalConfiguration.currentTheme.getPostTimeColor());
//        holder.tv_author.setTextColor(LocalConfiguration.currentTheme.getPostAuthorColor());
//        holder.tv_thumb.setTextColor(LocalConfiguration.currentTheme.getPostThumbColor());
//        holder.tv_viewed.setTextColor(LocalConfiguration.currentTheme.getPostThumbColor());
//
//        holder.ll_bottom.setBackgroundColor(LocalConfiguration.currentTheme.getPostButtomBackground());
//        holder.ll_item_background.setBackgroundColor(LocalConfiguration.currentTheme.getPostItemColor());
//        holder.fl_list_background.setBackgroundColor(LocalConfiguration.currentTheme.getPostListBackground());
//    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    static class ViewHolder {
        TextView tv_title;
        TextView tv_time;
        TextView tv_author;
        TextView tv_abstract;
        TextView ll_catalogntags;
        ImageView iv_potrait;
    }
}
