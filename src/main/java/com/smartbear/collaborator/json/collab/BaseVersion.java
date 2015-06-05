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

public class BaseVersion {

	private String scmPath;
	private String md5;
	private String scmVersionName;
	private Action action;
	private FileSource source;

	public String getScmPath() {
		return scmPath;
	}

	public void setScmPath(String scmPath) {
		this.scmPath = scmPath;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getScmVersionName() {
		return scmVersionName;
	}

	public void setScmVersionName(String scmVersionName) {
		this.scmVersionName = scmVersionName;
	}


	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public FileSource getSource() {
		return source;
	}

	public void setSource(FileSource source) {
		this.source = source;
	}
		
}
