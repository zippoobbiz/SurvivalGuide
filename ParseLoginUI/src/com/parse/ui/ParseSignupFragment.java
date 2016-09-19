/*
 *  Copyright (c) 2014, Parse, LLC. All rights reserved.
 *
 *  You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 *  copy, modify, and distribute this software in source code or binary form for use
 *  in connection with the web services and APIs provided by Parse.
 *
 *  As with any software that integrates with the Parse platform, your use of
 *  this software is subject to the Parse Terms of Service
 *  [https://www.parse.com/about/terms]. This copyright notice shall be
 *  included in all copies or substantial portions of the software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.parse.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;
import java.util.Random;

import entity.Faculty;
import entity.Images;

/**
 * Fragment for the user signup screen.
 */
public class ParseSignupFragment extends ParseLoginFragmentBase implements OnClickListener {
    public static final String USERNAME = "com.parse.ui.ParseSignupFragment.USERNAME";
    public static final String PASSWORD = "com.parse.ui.ParseSignupFragment.PASSWORD";

    private EditText usernameField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private EditText emailField;
    private EditText nameField;
    private EditText facultyField;
    private Button createAccountButton;
    private ParseOnLoginSuccessListener onLoginSuccessListener;

    private ParseLoginConfig config;
    private int minPasswordLength;

    private static final String LOG_TAG = "ParseSignupFragment";
    private static final int DEFAULT_MIN_PASSWORD_LENGTH = 6;
    private static final String USER_OBJECT_NAME_FIELD = "name";

    public static ParseSignupFragment newInstance(Bundle configOptions, String username, String password) {
        ParseSignupFragment signupFragment = new ParseSignupFragment();
        Bundle args = new Bundle(configOptions);
        args.putString(ParseSignupFragment.USERNAME, username);
        args.putString(ParseSignupFragment.PASSWORD, password);
        signupFragment.setArguments(args);
        return signupFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {



        Bundle args = getArguments();
        config = ParseLoginConfig.fromBundle(args, getActivity());

        minPasswordLength = DEFAULT_MIN_PASSWORD_LENGTH;
        if (config.getParseSignupMinPasswordLength() != null) {
            minPasswordLength = config.getParseSignupMinPasswordLength();
        }

        String username = (String) args.getString(USERNAME);
        String password = (String) args.getString(PASSWORD);

        View v = inflater.inflate(R.layout.com_parse_ui_parse_signup_fragment,
                parent, false);
        ImageView appLogo = (ImageView) v.findViewById(R.id.app_logo);
        usernameField = (EditText) v.findViewById(R.id.signup_username_input);
        passwordField = (EditText) v.findViewById(R.id.signup_password_input);
        confirmPasswordField = (EditText) v
                .findViewById(R.id.signup_confirm_password_input);
        emailField = (EditText) v.findViewById(R.id.signup_email_input);
        nameField = (EditText) v.findViewById(R.id.signup_name_input);

        facultyField = (EditText) v.findViewById(R.id.signup_faculty_input);
        createAccountButton = (Button) v.findViewById(R.id.create_account);

        usernameField.setText(username);
        passwordField.setText(password);

        if (appLogo != null && config.getAppLogo() != null) {
            appLogo.setImageResource(config.getAppLogo());
        }

        if (config.isParseLoginEmailAsUsername()) {
            usernameField.setHint(R.string.com_parse_ui_email_input_hint);
            usernameField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            if (emailField != null) {
                emailField.setVisibility(View.GONE);
            }
        }

        if (config.getParseSignupSubmitButtonText() != null) {
            createAccountButton.setText(config.getParseSignupSubmitButtonText());
        }
        createAccountButton.setOnClickListener(this);

        emailField.setText(getrandomEmail());
        usernameField.setText(getrandomEmail());
        nameField.setText(getrandom());
        passwordField.setText("aaaaaa");
        confirmPasswordField.setText("aaaaaa");
        facultyField.setText("fafsdf");

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ParseOnLoginSuccessListener) {
            onLoginSuccessListener = (ParseOnLoginSuccessListener) activity;
        } else {
            throw new IllegalArgumentException(
                    "Activity must implemement ParseOnLoginSuccessListener");
        }

        if (activity instanceof ParseOnLoadingListener) {
            onLoadingListener = (ParseOnLoadingListener) activity;
        } else {
            throw new IllegalArgumentException(
                    "Activity must implemement ParseOnLoadingListener");
        }
    }

    @Override
    public void onClick(View v) {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String passwordAgain = confirmPasswordField.getText().toString();

        String email = null;
        if (config.isParseLoginEmailAsUsername()) {
            email = usernameField.getText().toString();
        } else if (emailField != null) {
            email = emailField.getText().toString();
        }

        String name = null;
        if (nameField != null) {
            name = nameField.getText().toString();
        }

        String faculty = null;
        if (facultyField != null) {
            faculty = facultyField.getText().toString();
        }

        if (username.length() == 0) {
            if (config.isParseLoginEmailAsUsername()) {
                showToast(R.string.com_parse_ui_no_email_toast);
            } else {
                showToast(R.string.com_parse_ui_no_username_toast);
            }
        } else if (password.length() == 0) {
            showToast(R.string.com_parse_ui_no_password_toast);
        } else if (password.length() < minPasswordLength) {
            showToast(getResources().getQuantityString(
                    R.plurals.com_parse_ui_password_too_short_toast,
                    minPasswordLength, minPasswordLength));
        } else if (passwordAgain.length() == 0) {
            showToast(R.string.com_parse_ui_reenter_password_toast);
        } else if (!password.equals(passwordAgain)) {
            showToast(R.string.com_parse_ui_mismatch_confirm_password_toast);
            confirmPasswordField.selectAll();
            confirmPasswordField.requestFocus();
        } else if (email != null && email.length() == 0) {
            showToast(R.string.com_parse_ui_no_email_toast);
        } else if (name != null && name.length() == 0) {
            showToast(R.string.com_parse_ui_no_name_toast);
        } else if (faculty != null && faculty.length() == 0) {
            showToast(R.string.com_parse_ui_no_faculty_toast);
        } else {
            ParseUser user = new ParseUser();

            // Set standard fields
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            // Set additional custom fields only if the user filled it out
            if (name.length() != 0) {
                user.put(USER_OBJECT_NAME_FIELD, name);
            }

            loadingStart();
            user.signUpInBackground(new SignUpCallback() {

                @Override
                public void done(ParseException e) {
                    if (isActivityDestroyed()) {
                        return;
                    }

                    if (e == null) {
                        loadingFinish();
                        signupSuccess();
                    } else {
                        loadingFinish();
                        if (e != null) {
                            debugLog(getString(R.string.com_parse_ui_login_warning_parse_signup_failed) +
                                    e.toString());
                            switch (e.getCode()) {
                                case ParseException.INVALID_EMAIL_ADDRESS:
                                    showToast(R.string.com_parse_ui_invalid_email_toast);
                                    break;
                                case ParseException.USERNAME_TAKEN:
                                    showToast(R.string.com_parse_ui_username_taken_toast);
                                    break;
                                case ParseException.EMAIL_TAKEN:
                                    showToast(R.string.com_parse_ui_email_taken_toast);
                                    break;
                                default:
                                    showToast(R.string.com_parse_ui_signup_failed_unknown_toast);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    private void signupSuccess() {
        byte[] data = "Working at Parse is great!".getBytes();
        final ParseFile file = new ParseFile("resume.txt", data);
        file.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                // Handle success or failure here ...
                Images im = new Images();
                im.setDescription("lalala1");
                im.setImage(file);
                im.setPosition(1);
                im.saveInBackground();
            }
        });





        ParseQuery<ParseObject> query = ParseQuery.getQuery("Faculty");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> facultyList, ParseException e) {
                if (e == null) {


                    Faculty f1 = (Faculty) facultyList.get(0);
                    ParseRelation<ParseObject> relation = ParseUser.getCurrentUser().getRelation("faculties");
                    relation.add(f1);
                    relation.add((Faculty) facultyList.get(0));
                    relation.add((Faculty) facultyList.get(2));
                    relation.add((Faculty) facultyList.get(3));
                    relation.add((Faculty) facultyList.get(4));
                    relation.add((Faculty) facultyList.get(5));
                    relation.add((Faculty) facultyList.get(6));
                    ParseUser.getCurrentUser().saveInBackground();
                    Log.e("Faculty", "Retrieved " + facultyList.size() + " faculties");
                } else {
                    Log.e("Faculty", "Error: " + e.getMessage());
                }
            }
        });
        onLoginSuccessListener.onLoginSuccess();
    }

    public String getrandom() {
        final String alphabet = "abcdefgABCDE";
        final int N = alphabet.length();

        Random r = new Random();
        String result = "";
        for (int i = 0; i < 6; i++) {
            result += alphabet.charAt(r.nextInt(N));
        }

        return result;
    }

    public String getrandomEmail() {
        final String alphabet = "abcdefgABCDE";
        final int N = alphabet.length();

        Random r = new Random();
        String result = "";
        for (int i = 0; i < 6; i++) {
            result += alphabet.charAt(r.nextInt(N));
        }

        return result + "@gmail.com";
    }
}
