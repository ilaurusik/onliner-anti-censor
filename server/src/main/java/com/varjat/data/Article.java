package com.varjat.data;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames= {"url"}))
@EqualsAndHashCode(of={"url"})
@NoArgsConstructor
public class Article {
	
	public Article(String url, String title) {
		this.url = url;
		this.title = title;
	}
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(length=255)
	private String url;
	
	@Column(length=1024)
	private String title;
	
	@OneToMany(mappedBy="article", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private List<Comment> comments;
	
	@Column(updatable=false)
	private Date createdOn=new Date();
	
	@Column(updatable=false)
	@Setter(value=AccessLevel.NONE)
	private Date updatedOn;
	
	@PreUpdate
	private void preUpdate(){
		updatedOn = new Date();
	}

	@Override
	public String toString() {
		return "Article [id=" + id + ", url=" + url + ", title=" + title + "]";
	}
	

}
