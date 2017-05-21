package com.varjat.data;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<Article, Long>{
	Article findByUrl(String url);
	
	@Query("from Article where createdOn > ?1 and createdOn < ?2")
	List<Article> findArticlesAfter(Date from, Date to);
	
}
