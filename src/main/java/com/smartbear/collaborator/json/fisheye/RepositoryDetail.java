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

public class RepositoryDetail {
	private String type;
	private String name;
	private String description;
	private Boolean storeDiff;
	private Boolean enabled;
	
	private Git git;
	private Svn svn;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Git getGit() {
		return git;
	}
	public void setGit(Git git) {
		this.git = git;
	}
	public Svn getSvn() {
		return svn;
	}
	public void setSvn(Svn svn) {
		this.svn = svn;
	}
	public Boolean getStoreDiff() {
		return storeDiff;
	}
	public void setStoreDiff(Boolean storeDiff) {
		this.storeDiff = storeDiff;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	
	
	
}
