package com.assignment.xiaoduo.survivalguide.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.fragments.LoginFragment;
import com.assignment.xiaoduo.survivalguide.fragments.QuickRegisterFragment;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

public class LoginLoadingActivity extends Activity implements
        LoginFragment.BackToInputFragmentListener,
        QuickRegisterFragment.CallLoginFragmentListener {

    public static String LOG_STATUS = "log_status";
    public static String LOGIN_MODE = "login_mode";
    public static boolean reNewByNotification = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_loading);
        LocalConfiguration.setResolution(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        reNewByNotification = intent.getBooleanExtra(LOGIN_MODE, false);
        Boolean logout = intent.getBooleanExtra(LOG_STATUS, false);
        if (logout) {
            clearDatabaseTask clear = new clearDatabaseTask();
            clear.execute();
            getFragmentManager().beginTransaction()
                    .add(R.id.main_frame, new QuickRegisterFragment()).commit();
        } else if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.main_frame, new LoginFragment()).commit();
        }

    }

//    this is a set ip dialog for debugging
//    private void SetIpAddressDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        final EditText[] IPs = new EditText[4];
//        final TextView[] dot = new TextView[4];
//
//        LinearLayout dialogView = new LinearLayout(this);
//        dialogView.setGravity(Gravity.CENTER);
//        for (int i = 0; i < IPs.length; i++) {
//            dot[i] = new TextView(this);
//            dot[i].setText(".");
//            IPs[i] = new EditText(this);
//            IPs[i].setGravity(Gravity.CENTER);
//            IPs[i].setText(LocalConfiguration.IPs[i]);
//            IPs[i].setWidth(200);
//            IPs[i].setHeight(50);
//            IPs[i].setInputType(InputType.TYPE_CLASS_NUMBER);
//            IPs[i].setFocusable(true);
//            dialogView.addView(IPs[i]);
//            if (i < IPs.length - 1) {
//                dialogView.addView(dot[i]);
//            }
//
//        }
//
//        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case DialogInterface.BUTTON_POSITIVE:
//                        try {
//                            Field field = dialog.getClass().getSuperclass()
//                                    .getDeclaredField("mShowing");
//                            field.setAccessible(true);
//                            field.set(dialog, false);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        if (inputCheck(IPs[0].getText().toString(), IPs[1]
//                                        .getText().toString(), IPs[2].getText().toString(),
//                                IPs[3].getText().toString())) {
//                            // close
//                            try {
//                                Field field = dialog.getClass().getSuperclass()
//                                        .getDeclaredField("mShowing");
//                                field.setAccessible(true);
//                                field.set(dialog, true);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            dialog.dismiss();
//                        }
//                        break;
//                    case DialogInterface.BUTTON_NEGATIVE:
//                        try {
//                            Field field = dialog.getClass().getSuperclass()
//                                    .getDeclaredField("mShowing");
//                            field.setAccessible(true);
//                            field.set(dialog, true);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        dialog.dismiss();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };
//        builder.setTitle(getString(R.string.set_ip_address)).setIcon(R.drawable.filter)
//                .setView(dialogView)
//                .setNegativeButton("cancel", dialogListener);
//        builder.setPositiveButton("ok", dialogListener);
//        builder.show();
//
//    }


    //check input ip address
//    public boolean inputCheck(String ip1, String ip2, String ip3, String ip4) {
//        int[] ips = new int[4];
//        String[] strIp = {ip1, ip2, ip3, ip4};
//        for (int i = 0; i < ips.length; i++) {
//            if (!strIp[i].equals("")) {
//                ips[i] = Integer.parseInt(strIp[i]);
//                if (ips[i] > 255 || ips[i] < 0) {
//                    return false;
//                }
//                LocalConfiguration.IPs[i] = strIp[i];
//            } else {
//                return false;
//            }
//        }
//        return true;
//    }


    //when login failed, then go back to input fragment
    @Override
    public void backToInputFragment(String uName, String uPwd, int backStatus) {
        Fragment fragment = new QuickRegisterFragment();
        Bundle args;
        args = new Bundle();
        args.putString(QuickRegisterFragment.ARG_UNAME, uName);
        args.putString(QuickRegisterFragment.ARG_UPWD, uPwd);
        args.putInt(QuickRegisterFragment.ARG_BACK_STATUS, backStatus);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_frame, fragment).commit();
        if (backStatus == 4) {
            //reset ip address
//            SetIpAddressDialog();
            toast(getString(R.string.maintenance));
        }
    }

    //go to loading fragment
    @Override
    public void goLoginFragment(String uName, String uPwd, boolean isSignIn) {

        Fragment fragment = new LoginFragment();
        Bundle args;
        args = new Bundle();
        args.putString(LoginFragment.ARG_UNAME, uName);
        args.putString(LoginFragment.ARG_UPWD, uPwd);
        args.putBoolean(LoginFragment.ARG_IS_SIGNIN, isSignIn);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_frame, fragment).commit();

    }

    //clear database
    public class clearDatabaseTask extends AsyncTask<String, String, String> {
        private SQLiteDatabase sampleDB = null;
        @Override
        protected String doInBackground(String... args) {
            sampleDB = LoginLoadingActivity.this
                    .openOrCreateDatabase(LocalConfiguration.dbName,
                            Activity.MODE_PRIVATE, null);
            sampleDB.execSQL("DROP TABLE IF EXISTS "
                    + LocalConfiguration.userTable);
            sampleDB.close();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            sampleDB = null;
        }
    }

    @Override
    public void onDestroy() {
        setContentView(R.layout.empty_view);
        super.onDestroy();
    }

    public void toast(String text) {
        Toast toast = Toast.makeText(LoginLoadingActivity.this, text,
                Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackground(null);
        toast.show();
    }
}
