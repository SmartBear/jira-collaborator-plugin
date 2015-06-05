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
package com.smartbear.collaborator.condition;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.smartbear.collaborator.issue.IssueRest;
import com.smartbear.collaborator.json.fisheye.Detail;
import com.smartbear.collaborator.json.fisheye.JsonDevStatus;
import com.smartbear.collaborator.util.BeanUtil;
import com.smartbear.collaborator.util.Util;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This condition is used to make decision show/hide button
 * "Create/Update Review" on jira view issue screen
 * 
 * Show button if issue has attached commit ids
 * 
 * @author kpl
 * 
 */
public class ShowIssueCreateReviewBtn implements Condition {

	@Override
	public void init(Map<String, String> params) throws PluginParseException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldDisplay(Map<String, Object> context) {
		JiraHelper jiraHelper = (JiraHelper) context.get("helper");
		IssueImpl issue = (IssueImpl) context.get("issue");

		try {
			JsonDevStatus jsonDevStatus = IssueRest.getFisheyeDevStatus(issue.getId(), jiraHelper.getRequest());
			if (jsonDevStatus.getErrors().isEmpty() && !jsonDevStatus.getDetail().isEmpty()) {
				Detail detail = jsonDevStatus.getDetail().get(0);
				if (!detail.getRepositories().isEmpty()) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

}
