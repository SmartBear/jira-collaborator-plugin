/*
 *  Copyright 2015 SmartBear Software, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at

 *     http://www.apache.org/licenses/LICENSE-2.0

 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.smartbear.collaborator.json.fisheye;

import java.util.Date;
import java.util.List;

public class Commit {
	private String id;
	private String displayId;
	private Date authorTimestamp;
	private String url;
	private Author author;
	private Integer fileCount;
	private Boolean merge;
	private String message;
	private List<File> files;
	private Branches branches;
	private Reviews reviews;
	private String createReviewUrl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayId() {
		return displayId;
	}

	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

	

	public Date getAuthorTimestamp() {
		return authorTimestamp;
	}

	public void setAuthorTimestamp(Date authorTimestamp) {
		this.authorTimestamp = authorTimestamp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Integer getFileCount() {
		return fileCount;
	}

	public void setFileCount(Integer fileCount) {
		this.fileCount = fileCount;
	}

	public Boolean getMerge() {
		return merge;
	}

	public void setMerge(Boolean merge) {
		this.merge = merge;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public Branches getBranches() {
		return branches;
	}

	public void setBranches(Branches branches) {
		this.branches = branches;
	}

	public Reviews getReviews() {
		return reviews;
	}

	public void setReviews(Reviews reviews) {
		this.reviews = reviews;
	}

	public String getCreateReviewUrl() {
		return createReviewUrl;
	}

	public void setCreateReviewUrl(String createReviewUrl) {
		this.createReviewUrl = createReviewUrl;
	}
	
	
	
	

	

}
