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
package com.smartbear.collaborator.context;

import java.util.HashMap;
import java.util.Map;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;

import static com.smartbear.collaborator.util.Constants.*;

/**
 * Context with parameters that is used for web panel "Review Information"
 * 
 * @author kpl
 * 
 */
public class ReviewInformationContext extends AbstractJiraContextProvider {

	@Override
	public Map getContextMap(User arg0, JiraHelper arg1) {

		IssueImpl issue = (IssueImpl) arg1.getContextParams().get("issue");

		CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
		CustomField reviewIdCustomField = customFieldManager.getCustomFieldObjectByName(CUSTOM_FIELD_REVIEWID);
		
		
				
		CustomField reviewLinkCustomField = customFieldManager.getCustomFieldObjectByName(CUSTOM_FIELD_REVIEWLINK);
		Object reviewLinkObject = null;
		if (reviewLinkCustomField == null || (reviewLinkObject = reviewLinkCustomField.getValue(issue)) == null) {
			//try to load reviewLinkCustomField for issues that were created with plugin before version 2.2.1
			reviewLinkCustomField = customFieldManager.getCustomFieldObjectByName(CUSTOM_FIELD_REVIEWLINK_V_2_1_1);
			if (reviewLinkCustomField != null) reviewLinkObject = reviewLinkCustomField.getValue(issue);
			if (reviewLinkObject != null) {
				//Add reviewId to reviewLink
				reviewLinkObject += reviewIdCustomField.getValue(issue).toString();
			}
		}		

		CustomField reviewPhaseCustomField = customFieldManager.getCustomFieldObjectByName(CUSTOM_FIELD_REVIEWPHASE);
		CustomField reviewParticipantsCustomField = customFieldManager.getCustomFieldObjectByName(CUSTOM_FIELD_REVIEWPARTICIPANTS);

		String participants = (String) reviewParticipantsCustomField.getValue(issue);
		if (participants == null) {
			participants = "";
		}
		
		Map contextMap = new HashMap();
		contextMap.put("reviewId", reviewIdCustomField.getValue(issue));
		contextMap.put("reviewLink", reviewLinkObject);
		contextMap.put("reviewPhase", reviewPhaseCustomField.getValue(issue));
		contextMap.put("reviewParticipants", participants);

		return contextMap;

	}

}
