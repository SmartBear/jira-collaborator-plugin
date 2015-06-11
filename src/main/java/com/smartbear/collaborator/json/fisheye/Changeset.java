package com.smartbear.collaborator.json.fisheye;

import java.util.Date;
import java.util.List;

public class Changeset {
	
	private String repositoryName;
	private String csid;
	private Date date;
	private String author;
	private String branch;
	private String comment;
	private Object revisions;
	
	private List<File> files;
	
	public String getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	public String getCsid() {
		return csid;
	}
	public void setCsid(String csid) {
		this.csid = csid;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Object getRevisions() {
		return revisions;
	}
	public void setRevisions(Object revisions) {
		this.revisions = revisions;
	}
	public List<File> getFiles() {
		return files;
	}
	public void setFiles(List<File> files) {
		this.files = files;
	}
	
	
}
