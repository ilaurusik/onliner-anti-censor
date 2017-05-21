package com.varjat.service;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections4.map.LRUMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.varjat.data.Article;
import com.varjat.data.ArticleRepository;
import com.varjat.data.Comment;
import com.varjat.data.CommentRepository;

@Service
@Transactional
public class CommentGrabbingService {

	private static final Logger logger = LoggerFactory.getLogger(CommentGrabbingService.class);

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Async
	public Article grabComments(String url) {

		try {
			Document document = Jsoup.connect(url)
					.userAgent(
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
					.maxBodySize(0).timeout(3000).get();
			document.outputSettings().charset("UTF-8");

			Elements comments = document.select("div.news-comment__item_secondary");
			
			Article article = articleRepository.findByUrl(url);
			
			Set<Long> alreadyStoredComment = new HashSet<>();
			
			if (article == null) {
				article = new Article(url, document.select("div.news-header__title").text());
				List<Comment> commentsList = new ArrayList<>(comments.size());
				article.setComments(commentsList);
			} else {
				alreadyStoredComment = commentRepository.findComments(article.getId());
			}

			
			Set<Long> actualComments = new HashSet<>();
			int newCommentsAdded = 0;
			// Add new comments
			for (Element element : comments) {
				long extCommentId = Long.parseLong(element.attr("data-comment-id"));
				actualComments.add(extCommentId);
				if (!alreadyStoredComment.contains(extCommentId)) {
					element.select("div.news-comment__control").remove();
					newCommentsAdded++;
					article.getComments().add(new Comment(article, extCommentId,
							element.toString(), element.attr("data-author"), parseDate(element.attr("data-comment-date"))));
				}
			}

			// Mark censored
			int consoredCommetsCount = 0;
			if (article.getId() != null){
				for (Comment savedComments : article.getComments()) {
					if (!savedComments.isCensored() && !actualComments.contains(savedComments.getExtCommentId())){
						savedComments.setCensored(true);
						consoredCommetsCount++;
					}
				}
			}
			
			if (newCommentsAdded>0){
				logger.info(newCommentsAdded + " new comments added for "+article.getUrl());
			}
			
			if (consoredCommetsCount>0){
				logger.info(consoredCommetsCount + " comments was censored for "+article.getUrl());
			}

			return articleRepository.save(article);

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private static Date parseDate(String str) {
		try {
			return new Date(Long.parseLong(str) * 1000);
		} catch (Exception e) {
			return null;
		}
	}
}
