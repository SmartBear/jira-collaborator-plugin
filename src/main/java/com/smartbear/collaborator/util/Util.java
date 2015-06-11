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
package com.smartbear.collaborator.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.smartbear.collaborator.admin.ConfigModel;
import com.smartbear.collaborator.json.collab.Action;

public class Util {

	public static ConfigModel getConfigModel(PluginSettings settings) {

		ConfigModel configModel = new ConfigModel();
		
		try {
			configModel.setUrl(compileDomainUrl((String) settings.get(ConfigModel.class.getName() + ".url")));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		configModel.setLogin((String) settings.get(ConfigModel.class.getName() + ".login"));
		configModel.setPassword((String) settings.get(ConfigModel.class.getName() + ".password"));
		configModel.setFisheyeLogin((String) settings.get(ConfigModel.class.getName() + ".fisheyeLogin"));
		configModel.setFisheyePassword((String) settings.get(ConfigModel.class.getName() + ".fisheyePassword"));		
		configModel.setAuthTicket((String) settings.get(ConfigModel.class.getName() + ".authTicket"));
		
		try {
			configModel.setFisheyeUrl(compileDomainUrl((String) settings.get(ConfigModel.class.getName() + ".fisheyeUrl")));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return configModel;

	}

	public static boolean isEmpty(String obj) {
		return obj == null || obj.length() == 0;
	}

	public static boolean isEmpty(Collection collection) {
		return collection == null || collection.isEmpty();
	}
	
	public static boolean isEmpty(Map map) {
		return map == null || map.isEmpty();
	}

	public static String getRestString(String param) {
		if (param == null) {
			return "";
		}
		return param;
	}

	public static String compileDomainUrl(String urlString) throws MalformedURLException {
		if (urlString == null) {
			throw new MalformedURLException();
		}
		StringBuilder sb = new StringBuilder();
		URL url = new URL(urlString);
		sb.append(url.getProtocol());
		sb.append("://");
		sb.append(url.getHost());
		if (url.getPort() > 0) {
			sb.append(":");
			sb.append(url.getPort());
		}
		return sb.toString();
	}
	
	public static String encodeURL(String urlString) throws MalformedURLException, URISyntaxException {		
		if (urlString == null) {
			throw new MalformedURLException();
		}
		
		URL url = new URL(urlString);
        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        return uri.toASCIIString();
	
	}

	public static String compileDomainUrl(HttpServletRequest request) throws MalformedURLException {
		if (request == null) {
			throw new MalformedURLException();
		}
		return compileDomainUrl(request.getRequestURL().toString());
	}

	public static Action getVersionAction(String changeType) {

		if ("CHANGED".equals(changeType)) {
			return Action.MODIFIED;
		}

		if ("ADDED".equals(changeType)) {
			return Action.ADDED;
		}

		if ("REMOVED".equals(changeType)) {
			return Action.DELETED;
		}

		return null;
	}

	public static Map<String, String> getQueryMap(URL url) {
		if (url == null || isEmpty(url.getQuery())) {
			return Collections.EMPTY_MAP;
		}
		Map<String, String> map = new HashMap<String, String>();
		String[] params = url.getQuery().split("&");

		for (String param : params) {
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			map.put(name, value);
		}
		return map;
	}

}
