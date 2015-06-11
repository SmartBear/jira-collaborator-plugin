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
package com.smartbear.collaborator.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigModel {
	@XmlElement
	private String url;
	@XmlElement
	private String login;
	@XmlElement
	private String password;
	
	@XmlElement
	private String projectKey;
	
	@XmlElement
	private String authTicket;
	
	@XmlElement
	private String fisheyeLogin;
	
	@XmlElement
	private String fisheyePassword;
	
	@XmlElement
	private String fisheyeUrl;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public String getAuthTicket() {
		return authTicket;
	}

	public void setAuthTicket(String authTicket) {
		this.authTicket = authTicket;
	}

	public String getFisheyeLogin() {
		return fisheyeLogin;
	}

	public void setFisheyeLogin(String fisheyeLogin) {
		this.fisheyeLogin = fisheyeLogin;
	}

	public String getFisheyePassword() {
		return fisheyePassword;
	}

	public void setFisheyePassword(String fisheyePassword) {
		this.fisheyePassword = fisheyePassword;
	}

	public String getFisheyeUrl() {
		return fisheyeUrl;
	}

	public void setFisheyeUrl(String fisheyeUrl) {
		this.fisheyeUrl = fisheyeUrl;
	}
		
}
