package com.varjat.schedule;

import com.varjat.data.Article;
import com.varjat.data.ArticleRepository;
import com.varjat.service.CommentGrabbingService;
import org.apache.commons.collections4.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by varjat on 21.05.17.
 */
@Component
public class ScheduledJobs {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledJobs.class);

    @Autowired
    private CommentGrabbingService commentGrabbingService;

    @Autowired
    private ArticleRepository articleRepository;

    private LRUMap<String, Long> articleCache = new LRUMap<>(50);

    @Scheduled(fixedDelay = 1000*60*10)
    public void getLatestArticles() {

        List<String> result = new ArrayList<>(32);
        try {
            URL url = new URL("https://www.onliner.by/feed");
            URLConnection conn = url.openConnection();

            DocumentBuilder parser;

            parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            org.w3c.dom.Document document = parser.parse(conn.getInputStream());

            NodeList elements = document.getElementsByTagName("link");
            for (int i = 0; i < elements.getLength(); i++) {
                org.w3c.dom.Node node = elements.item(i);
                if ("item".equalsIgnoreCase(node.getParentNode().getNodeName())){
                    String articleUrl = node.getTextContent().trim();

                    Long articleId = articleCache.get(articleUrl);
                    if (articleId == null){
                        Article article = articleRepository.findByUrl(articleUrl);
                        if (article != null){
                            articleId = article.getId();
                            articleCache.put(articleUrl, articleId);
                        } else {
                            article = commentGrabbingService.grabComments(articleUrl);
                            if (article.getId() != null){
                                articleId = article.getId();
                                articleCache.put(articleUrl, articleId);
                                result.add(articleUrl);
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error(e.getMessage(), e);
        }

        if (!result.isEmpty()){
            logger.info(result.size()+ " articles added:"+ Arrays.toString(result.toArray(new String[0])));
        }
    }

    @Scheduled(fixedDelay = 1000*60*5)
    public void updateLatestComments() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -3);

        articleRepository.findArticlesAfter(calendar.getTime(), new Date()).stream()
                .forEach(article -> commentGrabbingService.grabComments(article.getUrl()));;
    }

    @Scheduled(fixedDelay = 1000*60*60)
    public void updateLastDayComments() {
        Calendar from = Calendar.getInstance();
        from.add(Calendar.DAY_OF_YEAR, -1);

        Calendar to = Calendar.getInstance();
        to.add(Calendar.HOUR, -3);


        articleRepository.findArticlesAfter(from.getTime(), to.getTime()).stream()
                .forEach(article -> commentGrabbingService.grabComments(article.getUrl()));;
    }

    @Scheduled(fixedDelay = 1000*60*60*4, initialDelay=1000*60)
    public void updateLastWeekComments() {
        Calendar from = Calendar.getInstance();
        from.add(Calendar.DATE, -7);

        Calendar to = Calendar.getInstance();
        to.add(Calendar.DATE, -1);

        articleRepository.findArticlesAfter(from.getTime(), to.getTime()).stream()
                .forEach(article -> commentGrabbingService.grabComments(article.getUrl()));;
    }

}
