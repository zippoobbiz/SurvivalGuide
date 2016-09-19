package com.assignment.xiaoduo.survivalguide.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.assignment.xiaoduo.survivalguide.activities.ReadArticleActivity;
import com.assignment.xiaoduo.survivalguide.activities.WriteReplyActivity;
import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.entities.ReplyItem;
import com.assignment.xiaoduo.survivalguide.entities.StaticResource;
import com.assignment.xiaoduo.survivalguide.helpers.ImageLoader;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReplyAdapter extends BaseAdapter {
    private ImageLoader mLoader;
    private List<ReplyItem> data;
    private Context context;
    private Map<String, ReplyItem> Replymap = new HashMap<>();

    public ReplyAdapter(Context context, List<ReplyItem> data) {
        this.context = context;
        this.data = data;
        Replymap.clear();
        for(ReplyItem r: data)
        {
            Replymap.put(r.getPostResponseID() + "", r);
        }
        mLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ReplyItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Replymap.clear();
        for(ReplyItem r: data)
        {
            Replymap.put(r.getPostResponseID() + "", r);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ReplyItem item = getItem(position);
        ViewHolder holder;
        LayoutInflater li = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = li.inflate(R.layout.list_item_reply, parent, false);
        holder = new ViewHolder();
        holder.tv_title = (TextView) convertView.findViewById(R.id.title);
        holder.tv_time = (TextView) convertView.findViewById(R.id.time);

        holder.ll_reply_from_layout = (LinearLayout) convertView
                .findViewById(R.id.reply_from_layout);
        holder.tv_reply_content = (TextView) convertView
                .findViewById(R.id.reply_content);
        holder.tv_reply_from = (TextView) convertView
                .findViewById(R.id.reply_from);
        holder.iv_portrait = (ImageView) convertView
                .findViewById(R.id.portrait);
        holder.tv_floor = (TextView) convertView.findViewById(R.id.floor);
        holder.tv_floor.setText("#"+(position+1));
        holder.reply_bt = (TextView) convertView.findViewById(R.id.reply_bt);
        String url = LocalConfiguration.getPicUrl() + LocalConfiguration.image_url + "user"
                + item.getUserID() + "/portrait";
        mLoader.DisplayImage(url, holder.iv_portrait);

        if(item.getUserID()== StaticResource.user.getUserID())
        {
            holder.reply_bt.setVisibility(View.INVISIBLE);
        }else
        {
            holder.reply_bt.setTag(item.getPostResponseID() + "");
            holder.reply_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(context, WriteReplyActivity.class);
                    mIntent.putExtra("ReplyItem", Replymap.get(v.getTag().toString()));
                    ((Activity)context).startActivityForResult(mIntent,
                            ReadArticleActivity.VIEW_REPLY);
                    ((Activity)context).overridePendingTransition(
                            R.anim.pull_in_right, R.anim.push_out_left);
                }
            });
        }

        if (!item.isMarked()) {
            holder.tv_reply_content.setTextColor(LocalConfiguration.currentTheme
                    .getPostContentColor());
        } else {
            holder.tv_reply_content.setTextColor(0xff70ABCE);
        }
        holder.tv_time.setText(item.getTime());
        holder.tv_reply_content.setText(item.getReplyContent());
        holder.tv_reply_from.setText(item.getReplyFrom());
        holder.ll_reply_from_layout
                .setVisibility(View.INVISIBLE);
        if (item.getReplyFrom() != null) {
            if (!item.getReplyFrom().equals("")) {
                holder.tv_title.setText(item.getAuthor() + " reply to " + item.getSponsor());
                holder.ll_reply_from_layout
                        .setVisibility(View.VISIBLE);
            } else {
                holder.tv_title.setText(item.getAuthor() + " said: ");
            }
        } else if (item.getTitle() != null) {
            holder.tv_title.setText(item.getAuthor() + " replied in \""+ item.getTitle()+"\"");
        } else if (item.getAuthor() != null) {
            holder.tv_title.setText(item.getAuthor() + " said: ");
        } else {

        }
        return convertView;
    }


    static class ViewHolder {
        TextView tv_time;
        TextView tv_title;
        TextView tv_reply_content;
        TextView tv_reply_from;
        TextView reply_bt;
        TextView tv_floor;
        LinearLayout ll_reply_from_layout;
        ImageView iv_portrait;
    }

}
