package com.assignment.xiaoduo.survivalguide.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.fragments.ItemFragment;
import com.assignment.xiaoduo.survivalguide.helpers.HttpHelper;


public class ViewedTask extends AsyncTask<String, String, String> {

    Context mContext;

    public ViewedTask(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(String... args) {

        SQLiteDatabase sampleDB = mContext.openOrCreateDatabase(LocalConfiguration.dbName,
                Activity.MODE_PRIVATE, null);
//		insert
        sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " + LocalConfiguration.viewedTable
                + " (" + "userID INTEGER, postID INTEGER)");

        try {
            String sqlStr = "INSERT INTO " + LocalConfiguration.viewedTable + " Values ("
                    + args[0] + "," + args[1] + ");";
            sampleDB.execSQL(sqlStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sampleDB.close();
        }

        HttpHelper.viewaddone(args[1]);
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        ItemFragment.data.get(ItemFragment.articlePosition).setMarked(true);
        ItemFragment.adapter.notifyDataSetChanged();
    }

}