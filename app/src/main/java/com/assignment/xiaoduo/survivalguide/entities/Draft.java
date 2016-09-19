package com.assignment.xiaoduo.survivalguide.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class Draft implements Serializable {
    private int id = -1;
    private String title;
    private String catalog;
    private String catalogSub;
    private String content;
    private int numOfImage;
    private ArrayList<String> imagePaths;
    private String autoSavedTime;

    public Draft(int id, String title, String catalog, String catalogSub, String content, int numOfImage, ArrayList<String> imagePaths, String autoSavedTime) {
        this.id = id;
        this.title = title;
        this.catalog = catalog;
        this.catalogSub = catalogSub;
        this.content = content;
        this.numOfImage = numOfImage;
        this.imagePaths = imagePaths;
        this.autoSavedTime = autoSavedTime;
    }

    @Override
    public String toString() {
        String temp = "id: " + id + " \ntitle: " + title + " \ncatalog: " + catalog
                + " \ncatalogSub: " + catalogSub + " \ncontent: " + content
                + "\nnumOfImage:" + numOfImage + " \nautosavedTime: "
                + autoSavedTime;
        if (imagePaths != null) {
            for (String imagepath : imagePaths) {
                temp += " imagepath: " + imagepath + ", ";
            }
        }

        return temp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getAutoSavedTime() {
        return autoSavedTime;
    }

    public void setAutoSavedTime(String autoSavedTime) {
        this.autoSavedTime = autoSavedTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getCatalogSub() {
        return catalogSub;
    }

    public void setCatalogSub(String catalogSub) {
        this.catalogSub = catalogSub;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNumOfImage() {
        return numOfImage;
    }

    public void setNumOfImage(int numOfImage) {
        this.numOfImage = numOfImage;
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.imagePaths = imagePaths;
    }


    public Draft() {

    }
}
