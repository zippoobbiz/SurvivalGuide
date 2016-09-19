package com.assignment.xiaoduo.survivalguide.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.assignment.xiaoduo.survivalguide.asynctasks.ViewedTask;
import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.entities.StaticResource;
import com.assignment.xiaoduo.survivalguide.fragments.ItemFragment;
import com.assignment.xiaoduo.survivalguide.fragments.ReplyFragment;
import com.assignment.xiaoduo.survivalguide.helpers.HttpHelper;
import com.assignment.xiaoduo.survivalguide.helpers.ImageLoader;
import com.assignment.xiaoduo.survivalguide.helpers.TextJustificationHelper;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class ReadArticleActivity extends ActionBarActivity implements ReplyFragment.ReplyFragmentCallBacks {


    private ViewHolder holder;
    private ImageLoader mLoader;
    public static final int VIEW_REPLY = 4;
    public static String VRF_MODE_FROM_VIEWPOST_FRAGMENT = "fromViewPost";
    private int width = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_article);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Display dis = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        dis.getMetrics(dm);
        width = dm.widthPixels;

        holder = new ViewHolder();
        holder.tv_subtitle = (TextView) findViewById(R.id.subtitle);
        holder.tv_author = (TextView) findViewById(R.id.author);
        holder.tv_time = (TextView) findViewById(R.id.time);
        holder.tv_thumb = (TextView) findViewById(R.id.thumb);
        holder.ll_content = (LinearLayout) findViewById(R.id.content);
        holder.tv_reply = (TextView) findViewById(R.id.reply_num);
        holder.et_reply = (TextView) findViewById(R.id.reply);
        holder.tv_viewed = (TextView) findViewById(R.id.viewed);
        holder.iv_portrait = (ImageView) findViewById(R.id.portrait);
        holder.scroll_view = (ScrollView) findViewById(R.id.scroll_view);
        //new features, that move reply to readArticleActivity, so ther is no need to start ReadArticleActivity
//        holder.tv_reply.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(ReadArticleActivity.this, ReadReplyActivity.class);
//                mIntent.putExtra(ReadReplyActivity.VIEW_REPLY_FRAGMENT_MODE, VRF_MODE_FROM_VIEWPOST_FRAGMENT);
//                startActivityForResult(mIntent,
//                        VIEW_REPLY);
//                ReadArticleActivity.this.overridePendingTransition(
//                        R.anim.pull_in_right, R.anim.push_out_left);
//            }
//        });

        holder.et_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ReadArticleActivity.this, WriteReplyActivity.class);
                startActivityForResult(mIntent,
                        VIEW_REPLY);
                ReadArticleActivity.this.overridePendingTransition(
                        R.anim.pull_in_right, R.anim.push_out_left);
            }
        });


        holder.tv_subtitle.setText(StaticResource.item.getSub_title());
        holder.tv_author.setText(StaticResource.item.getAuthor());
        holder.tv_time.setText(StaticResource.item.getTime());
        holder.tv_thumb.setText(StaticResource.item.getThumb());
        holder.tv_reply.setText(StaticResource.item.getReply());
        holder.tv_viewed.setText(StaticResource.item.getViewed());
        holder.tv_thumb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (StaticResource.allowedToLike) {
                    LikeTask like = new LikeTask();
                    like.execute(StaticResource.user.getUserID(),
                            StaticResource.item.getPostId());
                }
            }
        });


        //like a post
        CheckLikeTask checkLike = new CheckLikeTask();
        checkLike.execute(StaticResource.user.getUserID(),
                StaticResource.item.getPostId());

        //check if this post have been seen
        CheckViewedTask checkViewed = new CheckViewedTask();
        checkViewed.execute(StaticResource.user.getUserID(),
                StaticResource.item.getPostId());

        addText(StaticResource.item.getAbstracts());
        mLoader = new ImageLoader(ReadArticleActivity.this);
        String url = LocalConfiguration.getPicUrl() + LocalConfiguration.image_url + "user"
                + StaticResource.item.getAuthorID() + "/portrait";
        mLoader.DisplayImage(url, holder.iv_portrait);
        displayPostTask display = new displayPostTask();
        display.execute(StaticResource.item.getPostId());
    }

    //when user click back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // DO SOMETHING WHEN BUTTON PRESSED!
                ReadArticleActivity.this.finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIEW_REPLY) {

            if (resultCode == RESULT_OK) {
                holder.tv_reply.setText(StaticResource.item.getReply());
                ItemFragment.data.get(ItemFragment.articlePosition).setReply(StaticResource.item.getReply());
                ItemFragment.adapter.notifyDataSetChanged();
                ReplyFragment fragment = (ReplyFragment) getFragmentManager().findFragmentById(R.id.reply_container);
                fragment.refresh("refresh");
            }

        }
    }

    //after refresh reply, scroll down to bottom
    @Override
    public void afterRefresh() {
        holder.scroll_view.post(new Runnable() {

            @Override
            public void run() {
                holder.scroll_view.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private class ViewHolder {
        TextView tv_subtitle;
        TextView tv_author;
        TextView tv_time;
        TextView tv_thumb;
        TextView tv_reply;
        TextView tv_viewed;
        LinearLayout ll_content;
        TextView et_reply;
        ImageView iv_portrait;
        ScrollView scroll_view;
    }

    //add text depends on paragraph
    public void addText(String content) {
        if (!"".equals(content.trim())) {
            content = "\t" + content.trim().replace("\n", "\t");
            String desc = "";
            if (content.indexOf(LocalConfiguration.IMAGE_TEXT_SYMBOL_END) > 0) {
                content = content.replace(LocalConfiguration.IMAGE_TEXT_SYMBOL_START, "");
                desc = content.substring(0, content.indexOf(LocalConfiguration.IMAGE_TEXT_SYMBOL_END));
                content = content.substring(content.indexOf(LocalConfiguration.IMAGE_TEXT_SYMBOL_END) + LocalConfiguration.IMAGE_TEXT_SYMBOL_END.length());

            }
            TextView tv = new TextView(ReadArticleActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, convertDpToPixel(10), 0, 0);
            tv.setTextSize(16);
            tv.setLayoutParams(lp);
            // tv.setMinimumHeight(convertDpToPixel(120));
            tv.setGravity(Gravity.TOP | Gravity.START);
            tv.setHintTextColor(Color.GRAY);
            if (!"".equals(desc)) {
                TextView descTv = new TextView(ReadArticleActivity.this);
                LinearLayout.LayoutParams lpd = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                descTv.setLayoutParams(lpd);
                // tv.setMinimumHeight(convertDpToPixel(120));
                descTv.setTextColor(Color.GRAY);
                descTv.setTypeface(null, Typeface.ITALIC);
                descTv.setText(desc);
                descTv.setTextSize(11);
                holder.ll_content.addView(descTv);
            }
            tv.setText(content);
            tv.setText(TextJustificationHelper.justify(tv, width));
            holder.ll_content.addView(tv);
        }
    }

    public void setText(String content) {
        if (holder.ll_content.getChildCount() > 0) {
            TextView tv = (TextView) holder.ll_content
                    .getChildAt(holder.ll_content.getChildCount() - 1);
            tv.setText(content);
        }
    }

    //clear content
    public void clearContent() {
        holder.ll_content.removeAllViews();
    }

    //add image based on the structure of the article
    public void addImage(String url) {
        ImageView imageView = new ImageView(ReadArticleActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, convertDpToPixel(10), 0, 0);
        imageView.setLayoutParams(lp);
        // imageView.setBackgroundResource(R.drawable.portrait);
        imageView.setBackgroundColor(Color.GRAY);
        imageView.setAdjustViewBounds(true);

        mLoader.DisplayImage(url, imageView);
        holder.ll_content.addView(imageView);

    }

    //update content
    public void updateContent(String content, String currentPostUserId) {
        clearContent();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(StaticResource.item.getTitle());
        }
        Pattern p = Pattern.compile(LocalConfiguration.IMAGE_SYMBOL_START + "\\d+"
                + LocalConfiguration.IMAGE_SYMBOL_END);
        String[] strarray = p.split(content);
        for (int i = 0; i < strarray.length; i++) {
            if ((i - 1) >= 0) {
                String url = LocalConfiguration.getPicUrl() + LocalConfiguration.image_url + "user"
                        + currentPostUserId + "/post"
                        + StaticResource.item.getPostId() + "/image" + (i - 1)
                        + ".jpg";
                addImage(url);
            }
            addText(strarray[i]);
        }
    }

    //convert dp to pixel
    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    //display post, get further information for specific post
    public class displayPostTask extends AsyncTask<String, String, JSONArray> {

        JSONArray resultArray = null;

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                resultArray = HttpHelper.getPostContent(args[0]);
                return resultArray;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            if (result != null) {
                try {
                    updateContent(result.getJSONObject(0).get("content")
                                    .toString(),
                            result.getJSONObject(0).getJSONObject("userID")
                                    .getString("userID"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.reply_container, ReplyFragment.newInstance())
                        .commit();
            }
        }
    }

    //send a reply
    public class ReplyTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {

            JSONObject jo = new JSONObject();
            try {
                jo.put("postLastResponseID", args[0]);
                jo.put("content", args[1]);
                jo.put("postID", StaticResource.item.getPostId());
                jo.put("userID", StaticResource.user.getUserID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return HttpHelper.responsePost(jo.toString());
        }
    }

    //check if the current post was liked
    public class CheckLikeTask extends AsyncTask<String, String, String> {

        private SQLiteDatabase sampleDB = null;

        @Override
        protected String doInBackground(String... args) {

            sampleDB = ReadArticleActivity.this.openOrCreateDatabase(LocalConfiguration.dbName,
                    ReadArticleActivity.MODE_PRIVATE, null);

            try {

                Cursor c = sampleDB.rawQuery("SELECT * FROM "
                        + LocalConfiguration.likeTable + " where userID = " + args[0]
                        + " and postID = " + args[1], null);
                // If Cursor is valid
                if (c != null) {
                    // Move cursor to first row
                    StaticResource.likeCurrentPost = c.moveToFirst();
                    c.close();
                } else {
                    StaticResource.likeCurrentPost = false;
                }

            } catch (Exception se) {
                se.printStackTrace();
                sampleDB.execSQL("CREATE TABLE IF NOT EXISTS "
                        + LocalConfiguration.likeTable + " ("
                        + "userID INTEGER, postID INTEGER)");
                StaticResource.likeCurrentPost = false;
            } finally {
                if (sampleDB != null) {
                    sampleDB.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (StaticResource.likeCurrentPost) {
                holder.tv_thumb.setTextColor(Color.RED);
            } else {
                holder.tv_thumb.setTextColor(Color.BLACK);
            }
        }
    }

    //send a like requirement
    public class LikeTask extends AsyncTask<String, String, String> {

        private SQLiteDatabase sampleDB = null;

        @Override
        protected String doInBackground(String... args) {

            StaticResource.allowedToLike = false;
            sampleDB = ReadArticleActivity.this.openOrCreateDatabase(LocalConfiguration.dbName,
                    ReadArticleActivity.MODE_PRIVATE, null);
            // insert
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " + LocalConfiguration.likeTable
                    + " (" + "userID INTEGER, postID INTEGER)");
            if (StaticResource.likeCurrentPost) {
                HttpHelper.unlike(args[1]);
                try {
                    String sqlStr = "DELETE FROM " + LocalConfiguration.likeTable
                            + " where userID = " + args[0] + " and postID = "
                            + args[1] + ";";
                    sampleDB.execSQL(sqlStr);
                    StaticResource.likeCurrentPost = false;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (sampleDB != null) {
                        sampleDB.close();
                    }
                }
            } else {
                HttpHelper.like(args[1]);
                try {
                    String sqlStr = "INSERT INTO " + LocalConfiguration.likeTable
                            + " Values (" + args[0] + "," + args[1] + ");";
                    sampleDB.execSQL(sqlStr);
                    StaticResource.likeCurrentPost = true;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (sampleDB != null) {
                        sampleDB.close();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            StaticResource.allowedToLike = true;
            int like = Integer
                    .parseInt(holder.tv_thumb
                            .getText()
                            .toString()
                            .substring(
                                    3,
                                    holder.tv_thumb.getText().toString()
                                            .length()));
            if (StaticResource.likeCurrentPost) {
                like++;
                holder.tv_thumb.setText("❤️ " + like);
                holder.tv_thumb.setTextColor(Color.RED);

            } else {
                like--;
                holder.tv_thumb.setText("❤️ " + like);
                holder.tv_thumb.setTextColor(Color.BLACK);
            }
            ItemFragment.data.get(ItemFragment.articlePosition).setThumb(
                    "❤️ " + like);
            ItemFragment.adapter.notifyDataSetChanged();
        }
    }

    //check if this post has been
    public class CheckViewedTask extends AsyncTask<String, String, String> {

        private SQLiteDatabase sampleDB = null;
        private boolean viewed = false;
        private String postID = "";
        private String userID = "";

        @Override
        protected String doInBackground(String... args) {

            postID = args[1];
            userID = args[0];
            sampleDB = ReadArticleActivity.this.openOrCreateDatabase(LocalConfiguration.dbName,
                    ReadArticleActivity.MODE_PRIVATE, null);

            try {

                Cursor c = sampleDB.rawQuery("SELECT * FROM "
                        + LocalConfiguration.viewedTable + " where userID = " + args[0]
                        + " and postID = " + args[1], null);
                // If Cursor is valid
                if (c != null) {
                    // Move cursor to first row
                    viewed = c.moveToFirst();
                    c.close();
                } else {
                    viewed = false;
                }

            } catch (Exception se) {
                se.printStackTrace();
                sampleDB.execSQL("CREATE TABLE IF NOT EXISTS "
                        + LocalConfiguration.viewedTable + " ("
                        + "userID INTEGER, postID INTEGER)");
                viewed = false;
            } finally {
                if (sampleDB != null) {
                    sampleDB.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            int viewedTimes = Integer.parseInt(holder.tv_viewed
                    .getText()
                    .toString()
                    .substring(3,
                            holder.tv_viewed.getText().toString().length()));
            if (!viewed) {
                viewedTimes++;
                holder.tv_viewed.setText("👀 " + viewedTimes);
                holder.tv_viewed.setTextColor(Color.BLUE);
                ItemFragment.data.get(ItemFragment.articlePosition)
                        .setViewed("👀 " + viewedTimes);
                ItemFragment.adapter.notifyDataSetChanged();
                ViewedTask viewed = new ViewedTask(ReadArticleActivity.this);
                viewed.execute(userID, postID);
            }
        }
    }
}
