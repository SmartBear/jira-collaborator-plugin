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

import java.net.URL;
import java.util.Date;

/**
 * @author kpl
 *
 */
public class File {
	private String path;
	private URL url;
	private String changeType;
	private Integer linesAdded;
	private Integer linesRemoved;
	private String md5;
	private String previousMd5;
	private String rev;
	private String ancestor;
	private String contentLink;
	private Date previousCommitDate;
	private String previousCommitAuthor;
	private String previousCommitComment;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getChangeType() {
		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	public Integer getLinesAdded() {
		return linesAdded;
	}

	public void setLinesAdded(Integer linesAdded) {
		this.linesAdded = linesAdded;
	}

	public Integer getLinesRemoved() {
		return linesRemoved;
	}

	public void setLinesRemoved(Integer linesRemoved) {
		this.linesRemoved = linesRemoved;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getPreviousMd5() {
		return previousMd5;
	}

	public void setPreviousMd5(String previousMd5) {
		this.previousMd5 = previousMd5;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public String getAncestor() {
		return ancestor;
	}

	public void setAncestor(String ancestor) {
		this.ancestor = ancestor;
	}

	public String getContentLink() {
		return contentLink;
	}

	public void setContentLink(String contentLink) {
		this.contentLink = contentLink;
	}

	public Date getPreviousCommitDate() {
		return previousCommitDate;
	}

	public void setPreviousCommitDate(Date previousCommitDate) {
		this.previousCommitDate = previousCommitDate;
	}

	public String getPreviousCommitAuthor() {
		return previousCommitAuthor;
	}

	public void setPreviousCommitAuthor(String previousCommitAuthor) {
		this.previousCommitAuthor = previousCommitAuthor;
	}

	public String getPreviousCommitComment() {
		return previousCommitComment;
	}

	public void setPreviousCommitComment(String previousCommitComment) {
		this.previousCommitComment = previousCommitComment;
	}	
}
