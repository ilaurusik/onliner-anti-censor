package com.varjat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.varjat.data.Article;
import com.varjat.data.ArticleRepository;
import com.varjat.dto.CensoredCommentsData;
import com.varjat.service.CommentGrabbingService;

@RestController
public class CensoredController {
	
	@Autowired
	private CommentGrabbingService commentGrabbingService;
	
	@Autowired
	private ArticleRepository articleRepository;
	
	@RequestMapping("/censored")
	public CensoredCommentsData censored(@RequestParam(value = "url", defaultValue = "https://people.onliner.by/2017/02/04/rabota-28") String url) {
		commentGrabbingService.grabComments(url);

		
		Article article = articleRepository.findByUrl(url);
		
		CensoredCommentsData censoredCommentsData = new CensoredCommentsData(article.getUrl(), article.getTitle());
		return censoredCommentsData;
	}

}
