package com.assignment.xiaoduo.survivalguide.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.assignment.xiaoduo.customized.view.gifview.GifMovieView;
import com.assignment.xiaoduo.survivalguide.activities.LoginLoadingActivity;
import com.assignment.xiaoduo.survivalguide.activities.MainActivity;
import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.entities.StaticResource;
import com.assignment.xiaoduo.survivalguide.entities.User;
import com.assignment.xiaoduo.survivalguide.helpers.HttpHelper;
import com.assignment.xiaoduo.survivalguide.util.Util;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import org.json.JSONArray;
import org.json.JSONObject;


public class LoginFragment extends Fragment {

    private Bundle mArguments;
    public static final String ARG_UNAME = "userName";
    public static final String ARG_UPWD = "userPassword";
    public static final String ARG_IS_SIGNIN = "isSignIn";
    private SQLiteDatabase sampleDB = null;
    private String uName = "";
    private String uPwd = "";
    private boolean isSignIn = false;


    BackToInputFragmentListener mChangeFragmentCallback;

    public interface BackToInputFragmentListener {
        /**
         * Called by Listfragment (SwipeFragment) when a list item is selected
         */
        void backToInputFragment(String uName, String uPwd, int backStatus);
        //backStatus:
        //1: Register failed by repeated userName
        //2: Sign in failed by invalied userName or Password
        //3: userName or Password is null, I dont know why this triggered
    }

    public static final int BACK_STATUS_REPEATED_USERNAME = 1;
    public static final int BACK_STATUS_INVALIED_USERINFO = 2;
    public static final int BACK_STATUS_NULL_USERINFO = 3;
    public static final int BACK_STATUS_NO_INTERNET_CONNECTION = 4;
    public static final int BACK_STATUS_TEMP_TEST_STATUS = 999;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_auto_login,
                container, false);
        final GifMovieView gif1 = (GifMovieView) rootView.findViewById(R.id.loading_gif);
        gif1.setMovieResource(LocalConfiguration.currentTheme.getLoadingImageId());
        LinearLayout ll_background = (LinearLayout) rootView.findViewById(R.id.background_layout);
        ll_background.setBackgroundColor(Color.parseColor(LocalConfiguration.currentTheme.getBackgroundColor()));
        StaticResource.clear();
        mArguments = getArguments();
        if (mArguments != null) {
            uName = mArguments.getString(ARG_UNAME);
            uPwd = mArguments.getString(ARG_UPWD);
            isSignIn = mArguments.getBoolean(ARG_IS_SIGNIN);

            if (uName != null && uPwd != null && !uName.equals("")
                    && !uPwd.equals("")) {

                if (isSignIn) {
                    LoginTask login = new LoginTask();
                    login.execute(uName, uPwd);
                } else {
                    RegisterTask register = new RegisterTask();
                    register.execute(uName, uPwd);
                }
            } else {
                mChangeFragmentCallback.backToInputFragment(uName, uPwd, BACK_STATUS_NULL_USERINFO);
            }
        } else {
            CheckSQLiteTask checksql = new CheckSQLiteTask();
            checksql.execute();
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mChangeFragmentCallback = (BackToInputFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    class LoginTask extends AsyncTask<String, Integer, String> {
        JSONArray ja_stu = null;


        @Override
        protected String doInBackground(String... params) {

            ja_stu = HttpHelper.login(params[0], params[1]);
            if (ja_stu != null) {
                try {
                    StaticResource.user.setUserName(params[0]);
                    StaticResource.user.setUserPwd(params[1]);
                } catch (Exception e) {

                }
            }
            return "";
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            switch (LocalConfiguration.GET_STATUS) {
                case 0:
                    toast("Get function not called, internal error.");
                    mChangeFragmentCallback.backToInputFragment(uName, uPwd, BACK_STATUS_TEMP_TEST_STATUS);
                    break;
                case 1:
                    try {
                        StaticResource.setUser(ja_stu.getJSONObject(0));
                        sampleDB = getActivity()
                                .openOrCreateDatabase(LocalConfiguration.dbName,
                                        Activity.MODE_PRIVATE, null);
                        sampleDB.execSQL("DROP TABLE IF EXISTS "
                                + LocalConfiguration.userTable);
                        sampleDB.execSQL("CREATE TABLE IF NOT EXISTS "
                                + LocalConfiguration.userTable
                                + " ("
                                + "userID INTEGER PRIMARY KEY, userName varchar(20),userPwd varchar(20))");
                        String sqlString = "REPLACE INTO " + LocalConfiguration.userTable
                                + " Values (" + StaticResource.user.getUserID()
                                + "," + "'" + StaticResource.user.getUserName()
                                + "'," + "'" + StaticResource.user.getUserPwd()
                                + "');";
                        sampleDB.execSQL(sqlString);

                        if (LoginLoadingActivity.reNewByNotification) {
                            Intent mIntent[] = new Intent[2];
//							mIntent[0].setClass(getActivity(),
//									NavigationDrawerActivity.class);
//							mIntent[1].setClass(getActivity(),
//									ViewReplyActivity.class);

                            mIntent[0] = new Intent(getActivity(), MainActivity.class);
//							mIntent[1] = new Intent(getActivity(), ViewReplyActivity.class);
//							mIntent[1].putExtra(ViewReplyFragment.VIEW_REPLY_FRAGMENT_MODE,
//							NotificationOnClickReceiver.VRF_MODE_FROM_SCHEDULING_SERVICE);
                            getActivity().startActivities(mIntent);
                            getActivity().finish();
                        } else {
                            Intent intent1 = new Intent();
                            intent1.setClass(getActivity(),
                                    MainActivity.class);
                            startActivity(intent1);
                            getActivity().finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mChangeFragmentCallback
                                .backToInputFragment(uName, uPwd, BACK_STATUS_TEMP_TEST_STATUS);
                    }
                    break;
                case 2:
                    toast(getString(R.string.wrong_id_or_password));
                    mChangeFragmentCallback.backToInputFragment(uName, uPwd, BACK_STATUS_INVALIED_USERINFO);

                    break;
                case 3:
                    toast(getString(R.string.time_out));
                    mChangeFragmentCallback.backToInputFragment(uName, uPwd, BACK_STATUS_NO_INTERNET_CONNECTION);
                    break;
                case 4:
                    toast(getString(R.string.server_error));
                    mChangeFragmentCallback.backToInputFragment(uName, uPwd, BACK_STATUS_TEMP_TEST_STATUS);

                    break;
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

    }

    class CheckSQLiteTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                sampleDB = getActivity().openOrCreateDatabase(LocalConfiguration.dbName,
                        Activity.MODE_PRIVATE, null);

                Cursor c = sampleDB.rawQuery("SELECT * FROM "
                        + LocalConfiguration.userTable, null);
                // If Cursor is valid
                if (c != null) {
                    // Move cursor to first row
                    if (c.moveToFirst()) {
                        do {
                            String userID = c.getString(c
                                    .getColumnIndex("userID"));
                            String userName = c.getString(c
                                    .getColumnIndex("userName"));
                            String userPwd = c.getString(c
                                    .getColumnIndex("userPwd"));
                            if (!userName.equals("null")
                                    && !userPwd.equals("null")
                                    && !userName.equals("")
                                    && !userPwd.equals("")) {
                                StaticResource.user = new User(userID, userName,
                                        userPwd);
                            }

                            // Add the version to Arraylist 'results'
                        } while (c.moveToNext()); // Move to next row
                    }
                }
                if (c != null) {
                    c.close();
                }
            } catch (SQLiteException se) {
                se.printStackTrace();
                return "BackAndInput";
            } finally {
                if (sampleDB != null) {
                    sampleDB.close();
                }
            }
            if (!StaticResource.user.getUserName().equals("null")
                    && !StaticResource.user.getUserPwd().equals("null")
                    && !StaticResource.user.getUserName().equals("")
                    && !StaticResource.user.getUserPwd().equals("")) {

                return "goOnAndLogin";
            } else {
                return "BackAndInput";
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.equals("goOnAndLogin")) {
                LoginTask login = new LoginTask();
                login.execute(StaticResource.user.getUserName(),
                        StaticResource.user.getUserPwd());
            } else if (result.equals("BackAndInput")) {
                mChangeFragmentCallback.backToInputFragment("", "", BACK_STATUS_NULL_USERINFO);
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

    }

    class RegisterTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String status;
            try {

                JSONObject jo = new JSONObject();
                jo.put("userName", params[0]);
                jo.put("userPwd", params[1]);

                status = HttpHelper.quickRegister(jo.toString());
                if (status != null) {
                    if (status.equals("none")) {
                        return "status not change";
                    } else if (Util.isNumeric(status)) {
                        sampleDB = getActivity().openOrCreateDatabase(
                                LocalConfiguration.dbName, Activity.MODE_PRIVATE, null);

                        sampleDB.execSQL("CREATE TABLE IF NOT EXISTS "
                                + LocalConfiguration.userTable
                                + " ("
                                + "userID INTEGER, userName varchar(20),userPwd varchar(20),userGender varchar(1),"
                                + "userHome varchar(20),userFaculty varchar(20),userPic BLOB, userCount INTEGER, version varchar(5))");
                        String sqlString = "INSERT INTO " + LocalConfiguration.userTable + " Values ("
                                + status + "," + "'" + params[0] + "'," + "'"
                                + params[1] + "'," + "'" + "null" + "'," + "'"
                                + "null" + "'," + "'" + "null" + "'," + "'"
                                + "null" + "'," + "" + "-1" + "," + "'" + "null"
                                + "'" + ");";
                        sampleDB.execSQL(sqlString);
                        StaticResource.setUser(status, params[0], params[1]);
                        return "success";
                    } else if (status.equals("noConection")) {
                        return "noConection";
                    } else if (status.equals("exist")) {
                        return "exist";
                    } else {
                        return "fail";
                    }
                } else {
                    return "fail";
                }

            } catch (Exception se) {
                se.printStackTrace();
                return "error";
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(final String result) {

            new Handler().post(new Runnable() {
                public void run() {
                    if (result.equals("success")) {
                        Intent intent1 = new Intent();
                        intent1.setClass(getActivity(), MainActivity.class);
                        startActivity(intent1);
                        getActivity().finish();
                    } else if (result.equals("exist")) {
                        mChangeFragmentCallback.backToInputFragment(uName, uPwd, BACK_STATUS_REPEATED_USERNAME);
                    } else if (result.equals("noConection")) {
                        mChangeFragmentCallback.backToInputFragment(uName, uPwd, BACK_STATUS_NO_INTERNET_CONNECTION);
                    } else if (result.equals("error")) {
                        mChangeFragmentCallback.backToInputFragment(uName, uPwd, BACK_STATUS_TEMP_TEST_STATUS);
                    } else {
                        mChangeFragmentCallback.backToInputFragment(uName, uPwd, BACK_STATUS_TEMP_TEST_STATUS);
                    }
                }
            });

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

    }


    @Override
    public void onDestroy() {
        mArguments = null;
        sampleDB = null;
        uName = null;
        uPwd = null;
        isSignIn = false;
        mChangeFragmentCallback = null;
        super.onDestroy();
    }

    public void toast(String text) {
        Toast toast = Toast.makeText(getActivity(), text,
                Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackground(null);
        toast.show();
    }
}
