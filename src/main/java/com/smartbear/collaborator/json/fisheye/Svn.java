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

public class Svn extends BaseScm {
	private String url;

	private String initialImport;
	private Boolean followBase;
	private Boolean usingBuiltinSymbolicRules;
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getInitialImport() {
		return initialImport;
	}

	public void setInitialImport(String initialImport) {
		this.initialImport = initialImport;
	}

	public Boolean getFollowBase() {
		return followBase;
	}

	public void setFollowBase(Boolean followBase) {
		this.followBase = followBase;
	}

	public Boolean getUsingBuiltinSymbolicRules() {
		return usingBuiltinSymbolicRules;
	}

	public void setUsingBuiltinSymbolicRules(Boolean usingBuiltinSymbolicRules) {
		this.usingBuiltinSymbolicRules = usingBuiltinSymbolicRules;
	}

}
