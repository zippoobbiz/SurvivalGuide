package com.assignment.xiaoduo.survivalguide.helpers;

/**
 * Created by xiaoduo on 6/4/15.
 */
public class UrlComposer {

    //get server url
    //get entities urls
    public final static String user_url = "com.entities.user/";
    public final static String post_url = "com.entities.post/";
    public final static String postresponse_url = "com.entities.postresponse/";

    //make the login String
    public static String login(String userName, String userPwd) {
        return "login/" + userName + "/" + userPwd;
    }

    //get Post Response:
    public static String getPostResponse(String postID) {
        return "getResponse/" + postID;
    }

    //responseMe
    public static String getMyPostResponse(String userID) {
        return "responseMe/" + userID;
    }

    //setAsMarked
    public static String setAsMarked(String userID) {
        return "setAsMarked/" + userID;
    }


    //get Post Content:
    public static String getPostContent(String postID) {
        return "getPostContent/" + postID;
    }

    //Insert Post Response:
    public static String responsePost() {
        return "responsePost";
    }

    //Insert Post:
    public static String insertPost() {
        return "insertPost";
    }

    //quick register
    public static String quickRegister() {
        return "quickRegister";
    }

    //get top 20 news
    public static String getTop20NewPostLists(String time) {
        return "getNewPostLists/" + time;
    }

    //get top 20 by catalog id
    public static String getTop20PostLists(String catalogId, String time) {
        return "getNewPostLists/" + catalogId + "/" + time;
    }

    //get top 20 from unit table
    public static String getTop20PostLists(String catalogId, String time, String unitID) {
        return "getNewPostLists/" + catalogId + "/" + time + "/" + unitID;
    }

    //get my post
    public static String getMyPostLists(String userID) {
        return "myPost/" + userID;
    }


    public static String getPostDistributionDirectory() {
        return "postDistribution";
    }

    public static String uploadImage() {
        return "upLoadImage";
    }

    //Response Unit Comment:
    public static String likeAddOne(String postID) {
        return "likeaddone/" + postID;
    }

    public static String unLikeAddOne(String postID) {
        return "unlikeaddone/" + postID;
    }

    public static String viewAddOne(String postID) {
        return "viewaddone/" + postID;
    }

    //responseMe unread
//    public static String getMyUnreadPostResponse(String userID) {
//        return "myUnreadPost/" + userID;
//    }
//
//    //responseMe count
//    public static String countMyUnreadPostResponse(String userID) {
//        return "countMyUnreadPost/" + userID;
//    }
//
//    //make the getPostResponseById String
//    public static String getPostResponseById(String postId) {
//        return "getResponse/" + postId;
//    }
}
