package com.varjat.dto;

import java.util.List;

import lombok.Data;

@Data
public class CensoredCommentsData {
	public String url;
	public String title;
	public CensoredCommentsData(String url, String title) {
		super();
		this.url = url;
		this.title = title;
	}
	
	
	//private List<CensoredComment> comments;
}
