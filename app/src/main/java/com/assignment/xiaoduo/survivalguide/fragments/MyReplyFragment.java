package com.assignment.xiaoduo.survivalguide.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.assignment.xiaoduo.survivalguide.activities.ReadArticleActivity;
import com.assignment.xiaoduo.survivalguide.adapters.ReplyAdapter;
import com.assignment.xiaoduo.survivalguide.entities.PackageItem;
import com.assignment.xiaoduo.survivalguide.entities.ReplyItem;
import com.assignment.xiaoduo.survivalguide.entities.StaticResource;
import com.assignment.xiaoduo.survivalguide.helpers.HttpHelper;
import com.assignment.xiaoduo.survivalguide.util.Util;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyReplyFragment extends ProgressFragmentNew {

    ViewHolder holder;
    private List<ReplyItem> data;
    private ReplyAdapter adapter;
    private static int userPosition = 0;
    private View mContentView;

    public static MyReplyFragment newInstance() {
        return new MyReplyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_my_reply,
                container, false);

        DisplayReplyTask display = new DisplayReplyTask();
        display.execute();

        data = new ArrayList<>();
        adapter = new ReplyAdapter(getActivity(), data);
        holder = new ViewHolder();
        holder.lv_replies = (ListView) mContentView.findViewById(R.id.replies);
        holder.lv_replies.setAdapter(adapter);
        holder.lv_replies.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                userPosition = arg2;
                GetPostByIdTask getPost = new GetPostByIdTask();
                getPost.execute(data.get(userPosition).getPostId());

            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup content view
        setContentView(mContentView);
        // Setup text for empty content
        setEmptyText(R.string.empty);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    private class ViewHolder {
        ListView lv_replies;
    }

    public class DisplayReplyTask extends
            AsyncTask<String, String, List<ReplyItem>> {

        JSONArray resultArray = null;

        protected List<ReplyItem> doInBackground(String... args) {
            resultArray = HttpHelper.getMyPostResponse(StaticResource.user.getUserID());
            JSONObject jo;
            ReplyItem content;
            try {
                ArrayList<ReplyItem> shortList = new ArrayList<>();
                for (int i = 0; i < resultArray.length(); i++) {
                    content = new ReplyItem();
                    jo = resultArray.getJSONObject(i);

                    content.setReplyContent(jo.get("content").toString());
                    content.setTime(Util.dateFormTransfer(
                            jo.get("postResponseDate").toString(),
                            getActivity()));
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
                    content.setPostId(jo.getJSONObject("postID").getString(
                            "postID"));
                    content.setTitle(jo.getJSONObject("postID").getString(
                            "title"));
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
                data.clear();
                data.addAll(result);
                adapter.notifyDataSetChanged();
                setContentShown(true);
            } else {
                setContentEmpty(true);
                setContentShown(true);
            }
            SetAsMarkedTask set = new SetAsMarkedTask();
            set.execute();
        }
    }

    public class SetAsMarkedTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            StaticResource.numberOfUnReadThreshold = 0;
            return HttpHelper.setAsMarked(StaticResource.user.getUserID());
        }
    }

    public class GetPostByIdTask extends AsyncTask<String, String, String> {

        PackageItem content = new PackageItem();
        JSONArray ja = new JSONArray();


        @Override
        protected String doInBackground(String... args) {
            ja = HttpHelper.getSinglePostById(args[0]);

            if (ja != null) {
                JSONObject jo;
                try {
                    jo = ja.getJSONObject(0);
                    content.setTitle(jo.get("title").toString());
                    content.setReply("💬 "
                            + jo.get("responseCount").toString());
                    // content.setReply(jo.get("postLike").toString());
                    content.setTime(Util.dateFormTransfer(jo
                            .get("postDate").toString(), getActivity()));
                    content.setAuthor(jo.getJSONObject("userID").getString(
                            "userName"));
                    content.setAbstracts(jo.get("content").toString());
                    content.setPostId(jo.get("postID").toString());
                    content.setThumb("❤️ " + jo.get("postLike").toString());
                    content.setViewed("👀 "
                            + jo.get("clickCount").toString());

                    content.setAuthorID(jo.getJSONObject("userID")
                            .getString("userID"));

                    if (jo.has("unitID")) {
                        if (jo.getJSONObject("unitID").has("unitID")) {
                            content.setSub_title(jo.getJSONObject("unitID").getString("unitID"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "success";
            } else {
                return "false";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("success")) {
                StaticResource.item = content;
                Intent mIntent = new Intent(getActivity(),
                        ReadArticleActivity.class);
                startActivity(mIntent);
                getActivity().overridePendingTransition(
                        R.anim.pull_in_right, R.anim.push_out_left);
            }
        }
    }
}
