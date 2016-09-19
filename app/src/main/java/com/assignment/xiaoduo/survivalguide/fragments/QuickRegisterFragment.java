/*
 * Copyright (C) 2013 Manuel Peinado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.assignment.xiaoduo.survivalguide.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;

import static com.nineoldandroids.animation.Animator.AnimatorListener;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

public class QuickRegisterFragment extends Fragment {

    CallLoginFragmentListener mCallback;
    private Bundle mArguments;
    public static final String ARG_UNAME = "userName";
    public static final String ARG_UPWD = "userPassword";
    public static final String ARG_BACK_STATUS = "backStatus";
    private Button bt_signIn;
    private TextView tv_signUp;
    private TextView tv_signUpPreText;
    private TextView tv_agreement;
    private boolean isRotating = false;
    private LinearLayout signInIdLayout;
    private LinearLayout signInPasswordLayout;
    private LinearLayout signUpLayout;
    private LinearLayout agreementLayout;
    private boolean isSignIn = true;
    private int oneTime = 0;
    private int backStatus = 0;
    private EditText et_signInId;
    private EditText et_signInPassword;
    private CheckBox cb_aggrementCheckBox;

    public interface CallLoginFragmentListener {
        /**
         * Called by Listfragment (SwipeFragment) when a list item is selected
         */
        void goLoginFragment(String uName, String uPwd, boolean isSignIn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container,
                false);

        LinearLayout ll_background = (LinearLayout) rootView
                .findViewById(R.id.background_layout);
        ll_background.setBackgroundColor(Color
                .parseColor(LocalConfiguration.currentTheme.getBackgroundColor()));
        mArguments = getArguments();
        if (mArguments != null) {
            backStatus = mArguments.getInt(ARG_BACK_STATUS);
        }
        bt_signIn = (Button) rootView.findViewById(R.id.signIn);
        tv_signUp = (TextView) rootView.findViewById(R.id.signUp);
        tv_agreement = (TextView) rootView.findViewById(R.id.agreement);
        SpannableString content = new SpannableString(getString(R.string.term_of_service_string));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_agreement.setText(content);
        tv_agreement.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Dialog dialog = new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_info).setTitle(getString(R.string.term_of_service_string))
                        .setMessage(R.string.agreement)
                        .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                            //同意
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cb_aggrementCheckBox.setChecked(true);
                                dialog.dismiss();
                            }
                            //关闭
                        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        });
        tv_signUpPreText = (TextView) rootView.findViewById(R.id.signUpPreText);
        et_signInId = (EditText) rootView.findViewById(R.id.signInId);
        et_signInPassword = (EditText) rootView
                .findViewById(R.id.signInPassword);

        signInIdLayout = (LinearLayout) rootView
                .findViewById(R.id.signInIdLayout);
        signInPasswordLayout = (LinearLayout) rootView
                .findViewById(R.id.signInPasswordLayout);
        signUpLayout = (LinearLayout) rootView.findViewById(R.id.signUpLayout);

        cb_aggrementCheckBox = (CheckBox) rootView
                .findViewById(R.id.aggrementCheckBox);

        agreementLayout = (LinearLayout) rootView
                .findViewById(R.id.agreementLayout);
        animate(agreementLayout).setDuration(400);
        animate(bt_signIn).setDuration(400);
        animate(bt_signIn).setListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {
                isRotating = true;
            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {
            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                isRotating = false;
                if (oneTime == 0) {

                    animate(bt_signIn).alpha(1);
                    oneTime++;
                    if (isSignIn) {
                        SpannableString content = new SpannableString(getString(R.string.login_with_dot));
                        content.setSpan(new UnderlineSpan(), 0,
                                content.length(), 0);
                        tv_signUp.setText(content);
                        tv_signUpPreText.setText(getString(R.string.old_user));
                        bt_signIn.setText(getString(R.string.sign_up));
                        et_signInId.setHint(getString(R.string.nick_name_with_colon)
                                + LocalConfiguration.userNameMinLength + "-"
                                + LocalConfiguration.userNameMaxLength + getString(R.string.characters));
                        et_signInPassword.setHint(getString(R.string.password_with_colon)
                                + LocalConfiguration.userPwdMinLength + "-"
                                + LocalConfiguration.userPwdMaxLength + getString(R.string.characters));
                        et_signInPassword
                                .setInputType(InputType.TYPE_CLASS_TEXT);
                        animate(agreementLayout).alpha(1);
                    } else {
                        SpannableString content = new SpannableString(getString(R.string.sign_up));
                        content.setSpan(new UnderlineSpan(), 0,
                                content.length(), 0);
                        tv_signUp.setText(content);
                        tv_signUpPreText.setText(getString(R.string.new_user_text));
                        bt_signIn.setText(getString(R.string.login));
                        et_signInId.setHint(getString(R.string.nick_name));
                        et_signInPassword.setHint(getString(R.string.password));
                        // et_signInPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                        et_signInPassword
                                .setInputType(InputType.TYPE_CLASS_TEXT
                                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    ObjectAnimator.ofFloat(tv_signUp, "alpha", 0, 1)
                            .setDuration(500).start();
                    isSignIn = !isSignIn;
                } else {
                    oneTime = 0;
                }
            }

            @Override
            public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {
            }

        });
        if (mArguments != null) {
            String uName = mArguments.getString(ARG_UNAME);
            String uPwd = mArguments.getString(ARG_UPWD);
            if (uName != null && !uName.equals("")) {
                et_signInId.setText(uName);
            }
            if (uPwd != null && !uPwd.equals("")) {
                et_signInPassword.setText(uPwd);
            }
        }

        bt_signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInfo()) {
                    if (isSignIn) {
                        mCallback.goLoginFragment(et_signInId.getText()
                                .toString(), et_signInPassword.getText()
                                .toString(), isSignIn);
                    } else {
                        if (cb_aggrementCheckBox.isChecked()) {
                            mCallback.goLoginFragment(et_signInId.getText()
                                    .toString(), et_signInPassword.getText()
                                    .toString(), isSignIn);
                        } else {
                            ObjectAnimator
                                    .ofFloat(agreementLayout, "translationX",
                                            0, 50, -50, 50, -50, 50, -50, 50,
                                            -50, 50, -50, 50, -50, 50, 0)
                                    .setDuration(1000).start();

                        }
                    }

                }
            }
        });

        tv_signUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRotating) {
                    animate(bt_signIn).alpha(0);
                    animate(agreementLayout).alpha(0);
                    ObjectAnimator.ofFloat(tv_signUp, "alpha", 1, 0)
                            .setDuration(500).start();
                    if (isSignIn) {

                        ObjectAnimator
                                .ofFloat(bt_signIn, "translationY", 0, 150)
                                .setDuration(500).start();
                        ObjectAnimator
                                .ofFloat(signUpLayout, "translationY", 0, 150)
                                .setDuration(500).start();

                    } else {
                        ObjectAnimator
                                .ofFloat(bt_signIn, "translationY", 150, 0)
                                .setDuration(500).start();
                        ObjectAnimator
                                .ofFloat(signUpLayout, "translationY", 150, 0)
                                .setDuration(500).start();

                    }
                }

            }
        });

        switch (backStatus) {
            case LoginFragment.BACK_STATUS_REPEATED_USERNAME:
                shakeEditTextId();
                toast(getString(R.string.username_exist));
                break;
            case LoginFragment.BACK_STATUS_INVALIED_USERINFO:
                shakeBoth();
                break;
            case LoginFragment.BACK_STATUS_NULL_USERINFO:
                break;
            case LoginFragment.BACK_STATUS_NO_INTERNET_CONNECTION:
                break;
            case LoginFragment.BACK_STATUS_TEMP_TEST_STATUS:
                break;
        }
        return rootView;
    }

    public void shakeEditTextId() {
        ObjectAnimator
                .ofFloat(signInIdLayout, "translationX", 0, 50, -50, 50, -50,
                        50, -50, 50, -50, 50, -50, 50, -50, 50, 0)
                .setDuration(1000).start();
    }

    public void shakeBoth() {
        ObjectAnimator
                .ofFloat(signInIdLayout, "translationX", 0, 50, -50, 50, -50,
                        50, -50, 50, -50, 50, -50, 50, -50, 50, 0)
                .setDuration(1000).start();
        ObjectAnimator
                .ofFloat(signInPasswordLayout, "translationX", 0, 50, -50, 50,
                        -50, 50, -50, 50, -50, 50, -50, 50, -50, 50, 0)
                .setDuration(1000).start();
    }

    public void shakeEditTextPassword() {
        ObjectAnimator
                .ofFloat(signInPasswordLayout, "translationX", 0, 50, -50, 50,
                        -50, 50, -50, 50, -50, 50, -50, 50, -50, 50, 0)
                .setDuration(1000).start();
    }

    public boolean validateInfo() {
        boolean valid = true;
        String id = et_signInId.getText().toString();
        String pwd = et_signInPassword.getText().toString();
        if (id.equals("") || id.length() < LocalConfiguration.userNameMinLength
                || id.length() > LocalConfiguration.userNameMaxLength) {
            shakeEditTextId();
            valid = false;
        }

        if (pwd.equals("") || pwd.length() < LocalConfiguration.userPwdMinLength
                || pwd.length() > LocalConfiguration.userPwdMaxLength) {
            shakeEditTextPassword();
            valid = false;
        }
        return valid;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (CallLoginFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public void onDestroy() {
        mArguments = null;
        bt_signIn = null;
        tv_signUp = null;
        tv_signUpPreText = null;
        tv_agreement = null;
        signInIdLayout = null;
        signInPasswordLayout = null;
        signUpLayout = null;
        agreementLayout = null;
        et_signInId = null;
        et_signInPassword = null;
        cb_aggrementCheckBox = null;
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
