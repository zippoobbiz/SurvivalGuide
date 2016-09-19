package com.assignment.xiaoduo.survivalguide.entities;


public class EntryItem implements Item{

	public final String title;
	public final String subtitle;
	public final int icon;

	public EntryItem(String title, String subtitle, int icon) {
		this.title = title;
		this.subtitle = subtitle;
		this.icon = icon;
	}
	
	@Override
	public boolean isSection() {
		return false;
	}

}
