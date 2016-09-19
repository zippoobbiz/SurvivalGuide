package com.assignment.xiaoduo.survivalguide.entities;

import org.json.JSONException;
import org.json.JSONObject;

public class StaticResource {

    private static String USER_JSON_SAMPLE = "{\"userFaculty\":\"null\",\"userGender\":\"null\",\"userHome\":\"null\",\"userID\":null,\"userName\":\"null\",\"userPwd\":\"null\",\"userPic\":\"null\",\"userCount\":\"1\",\"version\":\"1\"}";
    public static User user = new User(USER_JSON_SAMPLE);
    public static String lastPostTimeStamp = "null";
    public static boolean allowedToLike = true;
    public static boolean likeCurrentPost = false;
    public static int numberOfUnReadThreshold = 0;
    public static int uploadingPostId = -1;
    public static boolean uploadSuccess = false;

    public static void clear() {
        user = new User(USER_JSON_SAMPLE);
    }

    public static void setUser(String userID, String userName, String userPwd) {
        StaticResource.user.setUserName(userName);
        StaticResource.user.setUserID(userID);
        StaticResource.user.setUserPwd(userPwd);
    }

    public static void setUser(JSONObject user) {
        try {
            if (user.has("userID")) {
                StaticResource.user.setUserID(user.getString("userID"));
            }
            if (user.has("userName")) {
                StaticResource.user.setUserName(user.getString("userName"));
            }
            if (user.has("userPwd")) {
                StaticResource.user.setUserPwd(user.getString("userPwd"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static PackageItem item = new PackageItem();
}
