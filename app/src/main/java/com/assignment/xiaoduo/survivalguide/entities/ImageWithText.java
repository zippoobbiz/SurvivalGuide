package com.assignment.xiaoduo.survivalguide.entities;

import android.graphics.Bitmap;

public class ImageWithText {

	private Bitmap image;
	private String text;
	private int id;
	private boolean hasImage;
	public boolean isHasImage() {
		return hasImage;
	}

	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
