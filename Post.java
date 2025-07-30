package com.example.socialapp;

public class Post {
	private String id;
	private String username;
	private String title;
	private String content;
	private String extra;
	
	public Post(String id, String username, String title, String content, String extra) {
		this.id = id;
		this.username = username;
		this.title = title;
		this.content = content;
		this.extra = extra;
	}
	
	public Post(int id, String username, String title, String content) {
		this.id = String.valueOf(id);
		this.username = username;
		this.title = title;
		this.content = content;
		this.extra = "";
	}
	
	public String getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getContent() {
		return content;
	}
	
	public String getExtra() {
		return extra;
	}
}