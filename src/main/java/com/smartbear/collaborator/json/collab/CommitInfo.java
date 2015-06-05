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
package com.smartbear.collaborator.json.collab;

import java.util.Date;

public class CommitInfo {

	private String scmId;
	private String author;
	private String comment;
	private Date date;
	private Boolean local;
	private String hostGuid;
	
	

	public CommitInfo(String author, String comment, Date date) {
		this.author = author;
		this.comment = comment;
		this.date = date;
	}

	public String getScmId() {
		return scmId;
	}

	public void setScmId(String scmId) {
		this.scmId = scmId;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Boolean isLocal() {
		return local;
	}

	public void setLocal(Boolean local) {
		this.local = local;
	}

	public String getHostGuid() {
		return hostGuid;
	}

	public void setHostGuid(String hostGuid) {
		this.hostGuid = hostGuid;
	}

}
