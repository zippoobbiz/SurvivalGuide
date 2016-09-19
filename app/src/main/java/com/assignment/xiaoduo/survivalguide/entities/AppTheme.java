package com.assignment.xiaoduo.survivalguide.entities;

public class AppTheme {
    private String LoginActivitybackgroundColor;
    private int loadingImageId;


    private int postTitleColor;
    private int postContentColor;
    private int postItemColor;
    private int postReplyColor;
    private int postThumbColor;
    private int postTimeColor;
    private int postAuthorColor;
    private int postListBackground;
    private int postButtomBackground;

    public int getPostButtomBackground() {
        return postButtomBackground;
    }



    public int getPostListBackground() {
        return postListBackground;
    }



    public int getPostAuthorColor() {
        return postAuthorColor;
    }



    public int getPostTimeColor() {
        return postTimeColor;
    }



    public int getPostTitleColor() {
        return postTitleColor;
    }



    public int getPostContentColor() {
        return postContentColor;
    }


    public int getPostItemColor() {
        return postItemColor;
    }


    public int getPostReplyColor() {
        return postReplyColor;
    }



    public int getPostThumbColor() {
        return postThumbColor;
    }

    public String getBackgroundColor() {
        return LoginActivitybackgroundColor;
    }

    public int getLoadingImageId() {
        return loadingImageId;
    }

    public AppTheme(String backgroundColor, int loadingImageId) {
        this.LoginActivitybackgroundColor = backgroundColor;
        this.loadingImageId = loadingImageId;
    }

    public AppTheme(String backgroundColor, int loadingImageId, int weatherImageId,
                    int postTitleColor, int postContentColor, int postItemColor,
                    int postReplyColor, int postThumbColor, int postTimeColor, int postAuthorColor,
                    int postListBackground, int postButtomBackground) {
        this.LoginActivitybackgroundColor = backgroundColor;
        this.loadingImageId = loadingImageId;
        this.postTitleColor = postTitleColor;
        this.postContentColor = postContentColor;
        this.postItemColor = postItemColor;
        this.postReplyColor = postReplyColor;
        this.postThumbColor = postThumbColor;
        this.postTimeColor = postTimeColor;
        this.postAuthorColor = postAuthorColor;
        this.postListBackground = postListBackground;
        this.postButtomBackground = postButtomBackground;
    }
}
