package com.assignment.xiaoduo.survivalguide.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.assignment.xiaoduo.survivalguide.entities.ReplyItem;
import com.assignment.xiaoduo.survivalguide.entities.StaticResource;
import com.assignment.xiaoduo.survivalguide.helpers.HttpHelper;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import org.json.JSONException;
import org.json.JSONObject;

public class WriteReplyActivity extends ActionBarActivity {

    EditText reply_et;
    TextView remain;
    ReplyItem item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_reply);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reply_et = (EditText)findViewById(R.id.reply_et);
        remain = (TextView)findViewById(R.id.remaining);
        remain.setText("Characters Ramaining: 200");
        Intent intent = getIntent();
        item = (ReplyItem)intent.getSerializableExtra("ReplyItem");

        if(item != null)
        {
            getSupportActionBar().setTitle("Reply to: "+item.getAuthor());
        }else
        {
            getSupportActionBar().setTitle("Reply to: "+ StaticResource.item.getTitle());
        }
        reply_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                remain.setText("Characters Ramaining: "+(200-reply_et.getText().toString().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_write_reply, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                // DO SOMETHING WHEN BUTTON PRESSED!
                WriteReplyActivity.this.finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return true;
            case R.id.send:
                ReplyTask reply = new ReplyTask();
                reply.execute(reply_et.getText().toString());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    public class ReplyTask extends AsyncTask<String, String, String> {

        ReplyItem content = new ReplyItem();

        @Override
        protected String doInBackground(String... args) {
            if (item == null) {
                content.setPostLastResponseID(0);
            } else {
                content.setPostLastResponseID(item.getPostResponseID());
                content.setReplyFrom(item.getReplyContent());
                content.setSponsor(item.getAuthor());
            }
            content.setReplyContent(args[0]);
            content.setTime("Just now");
            content.setAuthor(StaticResource.user.getUserName());
            content.setPostResponseID(-1);
            content.setUserID(StaticResource.user.getUserID());
            JSONObject jo = new JSONObject();
            try {
                if (item == null) {
                    jo.put("postLastResponseID", 0);
                } else {
                    jo.put("postLastResponseID", item.getPostResponseID());
                }
                jo.put("content", args[0]);
                jo.put("userID", StaticResource.user.getUserID());
                jo.put("postID", StaticResource.item.getPostId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return HttpHelper.responsePost(jo.toString());
        }

        @Override
        protected void onPostExecute(String result) {
            int repltTimes = Integer.parseInt(StaticResource.item
                    .getReply().substring(3,
                            StaticResource.item.getReply().length()));
            StaticResource.item.setReply("💬 " + ++repltTimes);


            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            WriteReplyActivity.this.finish();

            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }
    }
}
