package com.assignment.xiaoduo.survivalguide.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.assignment.xiaoduo.survivalguide.adapters.ReplyAdapter;
import com.assignment.xiaoduo.survivalguide.entities.ReplyItem;
import com.assignment.xiaoduo.survivalguide.entities.StaticResource;
import com.assignment.xiaoduo.survivalguide.helpers.HttpHelper;
import com.assignment.xiaoduo.survivalguide.util.Util;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReplyFragment extends Fragment {

    ViewHolder holder;
    private List<ReplyItem> data;
    private ReplyAdapter adapter;
    private View mContentView;

    public static ReplyFragment newInstance() {
        return new ReplyFragment();
    }

    public interface ReplyFragmentCallBacks
    {
        public void afterRefresh();
    }

    ReplyFragmentCallBacks mCallBacks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_read_reply,
                container, false);
        refresh("");


        data = new ArrayList<>();
        adapter = new ReplyAdapter(getActivity(), data);
        holder = new ViewHolder();
        holder.lv_replies = (ListView) mContentView.findViewById(R.id.replies);
        holder.lv_replies.setAdapter(adapter);
        setListViewHeightBasedOnChildren(holder.lv_replies);


        LinearLayout ll = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        lp.setMargins(0, Util.convertDpToPixel(10, getActivity()), 0,
                Util.convertDpToPixel(10, getActivity()));
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv = new TextView(getActivity());
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setMinimumHeight(Util.convertDpToPixel(120, getActivity()));
        tv.setHint("  There is no reply!");
        tv.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(getActivity());
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                Util.convertDpToPixel(135, getActivity()),
                Util.convertDpToPixel(100, getActivity()));
        imageView.setLayoutParams(lp1);
        imageView.setBackgroundResource(R.drawable.empty_box);
        imageView.setAdjustViewBounds(true);
        ll.addView(imageView);
        ll.addView(tv);

        holder.empty_Overlay = (FrameLayout) mContentView.findViewById(R.id.empty_layout);
        holder.empty_Overlay.addView(ll, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        holder.empty_Overlay.setVisibility(View.INVISIBLE);
        holder.lv_replies.setEmptyView(holder.empty_Overlay);
//        holder.lv_replies.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                    long arg3) {
//
//            }
//        });

        return mContentView;
    }

    public void refresh(String task)
    {
        DisplayReplyTask display = new DisplayReplyTask(task);
        display.execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallBacks = (ReplyFragmentCallBacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement ReplyFragmentCallBacks.");
        }


    }

    private class ViewHolder {
        ListView lv_replies;
        FrameLayout empty_Overlay;
    }

    public class DisplayReplyTask extends
            AsyncTask<String, String, List<ReplyItem>> {
        JSONArray resultArray = null;
        String task;
        public DisplayReplyTask(String task)
        {
            this.task = task;
        }
        protected List<ReplyItem> doInBackground(String... args) {

            resultArray = HttpHelper.getPostResponse(StaticResource.item
                    .getPostId());
            JSONObject jo;
            ReplyItem content;
            try {
                ArrayList<ReplyItem> shortList = new ArrayList<>();
                for (int i = 0; i < resultArray.length(); i++) {
                    content = new ReplyItem();
                    jo = resultArray.getJSONObject(i);
                    content.setReplyContent(jo.get("content").toString());//
                    content.setTime(Util.dateFormTransfer(
                            jo.get("postResponseDate").toString(), getActivity()));
                    content.setAuthor(jo.getJSONObject("userID").getString(
                            "userName"));
                    if (jo.getJSONObject("userID").has("userID")) {
                        content.setUserID(jo.getJSONObject("userID").getString(
                                "userID"));
                    }
                    if (jo.has("postLastResponseID")) {
                        content.setPostLastResponseID(jo
                                .getInt("postLastResponseID"));
                    }
                    content.setPostResponseID(jo.getInt("postResponseID"));
                    if (jo.has("marked")) {
                        if (jo.getInt("marked") == 0) {
                            content.setMarked(false);
                        } else {
                            content.setMarked(true);
                        }

                    }
                    shortList.add(content);
                }
                for (ReplyItem item : shortList) {
                    if (item.getPostLastResponseID() != 0) {
                        for (ReplyItem it : shortList) {
                            if (item.getPostLastResponseID() == it
                                    .getPostResponseID()) {
                                item.setReplyFrom(it.getReplyContent());
                                item.setSponsor(it.getAuthor());
                            }
                        }
                    }
                }

                return shortList;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(List<ReplyItem> result) {
            if (result != null) {
                Collections.reverse(result);
                data.clear();
                data.addAll(result);
                adapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(holder.lv_replies);
//                setContentShown(true);
                if("refresh".equals(task))
                {
                    mCallBacks.afterRefresh();
                }
            } else {
//                setContentEmpty(true);
//                setContentShown(true);
            }
        }
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
