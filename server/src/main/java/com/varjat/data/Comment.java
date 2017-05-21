package com.varjat.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Type;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames={"extCommentId"}))
@RequiredArgsConstructor
@EqualsAndHashCode(of={"extCommentId"})
@NoArgsConstructor
public class Comment {

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="ARTICLE_ID", nullable=false, updatable=false)
	@NonNull
	private Article article;
	
	@Column(nullable=false, updatable=false)
	@NonNull
	private Long extCommentId;
	
	@Column(nullable=false)
	private boolean isCensored=false;

	@Type(type="text")
	@Column(updatable=false)
	@NonNull
	private String text;
	
	@Column(length=64, nullable=false, updatable=false)
	@NonNull
	private String author;
	
	@Column(nullable=false, updatable=false)
	@NonNull
	private Date creationDate;
}
