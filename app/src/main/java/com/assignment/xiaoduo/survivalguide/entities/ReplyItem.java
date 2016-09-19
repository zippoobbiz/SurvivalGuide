/*
 * Copyright (C) 2013 47 Degrees, LLC
 *  http://47deg.com
 *  hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.assignment.xiaoduo.survivalguide.entities;

import java.io.Serializable;

public class ReplyItem implements Serializable{
	private int postLastResponseID;

	private int postResponseID;

	private String author;

	private String time;

	private String title;
	
	private String replyFrom;
	
	private String userID;
	
	private String sponsor;
	
	private boolean marked = false;
	
	private String postId;
	

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getReplyFrom() {
		return replyFrom;
	}

	public void setReplyFrom(String replyFrom) {
		this.replyFrom = replyFrom;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private String replyContent;

	public ReplyItem() {

	}


	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replycontent) {
		this.replyContent = replycontent;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getPostLastResponseID() {
		return postLastResponseID;
	}

	public void setPostLastResponseID(int postLastResponseID) {
		this.postLastResponseID = postLastResponseID;
	}

	public int getPostResponseID() {
		return postResponseID;
	}

	public void setPostResponseID(int postResponseID) {
		this.postResponseID = postResponseID;
	}

}
