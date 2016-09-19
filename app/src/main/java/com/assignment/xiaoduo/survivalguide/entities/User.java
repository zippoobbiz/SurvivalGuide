package com.assignment.xiaoduo.survivalguide.entities;


import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String userID;
    private String userName;
    private String userPwd;

    public User(String userID, String userName, String userPwd) {
        this.userID = userID;
        this.userName = userName;
        this.userPwd = userPwd;

    }


    public User(String jsonObjectStr) {
        if(jsonObjectStr != null && !"".equals(jsonObjectStr))
        {
            final char firstChar = jsonObjectStr.charAt(0);
            if (firstChar == '[') {
                jsonObjectStr = jsonObjectStr.substring(1,jsonObjectStr.indexOf(']'));
            }
        }
        try {
            JSONObject jsonobject = new JSONObject(jsonObjectStr);
            this.userID = jsonobject.getString("userID");
            this.userName = jsonobject.getString("userName");
            this.userPwd = jsonobject.getString("userPwd");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        if (null == userID) {
            throw new IllegalArgumentException("user id cannot be null");
        } else if ("".equals(userID)) {
            throw new IllegalArgumentException("user id cannot be empty");
        }
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (null == userName) {
            throw new IllegalArgumentException("user name cannot be null");
        } else if (userName.length() < 4) {
            throw new IllegalArgumentException("user name should be longer than 4 char");
        }
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        if (null == userPwd) {
            throw new IllegalArgumentException("user password cannot be null");
        } else if (userPwd.length() < 5) {
            throw new IllegalArgumentException("user password should be shorter than 5 char");
        }
        this.userPwd = userPwd;
    }
}
