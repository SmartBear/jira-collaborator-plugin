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

import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.smartbear.collaborator.issue.IssueRest;
import com.smartbear.collaborator.json.fisheye.Changeset;
import com.smartbear.collaborator.util.Util;

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
	private final PluginSettingsFactory pluginSettingsFactory;
	
	public ShowIssueCreateReviewBtn(PluginSettingsFactory pluginSettingsFactory) {
		this.pluginSettingsFactory = pluginSettingsFactory;
	}

	@Override
	public void init(Map<String, String> params) throws PluginParseException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldDisplay(Map<String, Object> context) {
		JiraHelper jiraHelper = (JiraHelper) context.get("helper");
		IssueImpl issue = (IssueImpl) context.get("issue");
		
		// Get plugin settings by project key
		PluginSettings pluginSettings = pluginSettingsFactory.createSettingsForKey(issue.getProjectObject().getKey());

		try {
			List<Changeset> changesets = IssueRest.getFisheyeChangesets(Util.getConfigModel(pluginSettings), issue.getKey(), jiraHelper.getRequest());
			if (!changesets.isEmpty()) {
				return true;
			}			
		} catch (Exception e) {
			return false;
		}
		return false;
	}

}
