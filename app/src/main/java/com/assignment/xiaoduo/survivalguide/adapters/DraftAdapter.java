package com.assignment.xiaoduo.survivalguide.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.entities.Draft;
import com.assignment.xiaoduo.survivalguide.util.Util;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class DraftAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Draft> data;
    private HashMap<Integer, Boolean> mSelection = new HashMap<>();

    public DraftAdapter(Context context, ArrayList<Draft> drafts) {
        this.context = context;
        this.data = drafts;
    }

    public void setNewSelection(int position, boolean value) {
        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

//    public Set<Integer> getCurrentCheckedPosition() {
//        return mSelection.keySet();
//    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new HashMap<>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Draft getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView title;
        TextView content;
        TextView timeStamp;
        ImageView thumbnail;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = li.inflate(R.layout.item_draft, parent, false);
//        final TextView toString = (TextView) convertView.findViewById(R.id.draft_to_string);
        ViewHolder holder = new ViewHolder();
        holder.title = (TextView) convertView.findViewById(R.id.title);
        holder.content = (TextView) convertView.findViewById(R.id.content);
        holder.timeStamp = (TextView) convertView.findViewById(R.id.time_stamp);
        holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
        final Draft d = getItem(position);

        if (mSelection.get(position) != null) {
            convertView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it red
        }
//        if (toString != null) {
//            toString.setText(d.toString());
//        }
        holder.title.setText(d.getTitle());
        holder.timeStamp.setText(d.getAutoSavedTime());
        if (!d.getImagePaths().get(0).equals("")) {
            holder.content.setVisibility(View.GONE);
            int degree = Util.readPictureDegree(d.getImagePaths().get(0));
            Bitmap image = Util.rotateBitmap(Util.decodeFile(d.getImagePaths().get(0)), degree);
            holder.thumbnail.setImageBitmap(image);
        } else {
            String tempStr = d.getContent();
            Pattern p = Pattern.compile(LocalConfiguration.IMAGE_SYMBOL_START + "\\d+" + LocalConfiguration.IMAGE_SYMBOL_END);
            String[] str = p.split(tempStr);
            tempStr = "";
            for (String rmp : str) {
                tempStr += rmp;
            }
            tempStr = tempStr.replace(LocalConfiguration.IMAGE_TEXT_SYMBOL_START, " ").replace(LocalConfiguration.IMAGE_TEXT_SYMBOL_END, " ").replace("\n", " ");
            holder.content.setText(tempStr);
            holder.thumbnail.setVisibility(View.GONE);
        }
        return convertView;
    }

}
