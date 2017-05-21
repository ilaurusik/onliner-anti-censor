package com.varjat.data;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long>{
	
	/**
	 * Return NATIVE (onliner's) article IDs
	 * @param articleId
	 * @return ext Article ids
	 */
	@Query("select c.extCommentId from Comment c where article.id = ?1")
	Set<Long> findComments(Long articleId);
	
	List<Article> findByArticleId(Long articleId);
}
