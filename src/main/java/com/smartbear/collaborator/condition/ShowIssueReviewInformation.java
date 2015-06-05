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

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.smartbear.collaborator.util.BeanUtil;
import static com.smartbear.collaborator.util.Constants.*;

/** This condition is used to make decision show/hide web panel 
 * with "Review Information" on jira view issue screen
 * 
 * Show panel if issue has review id in its custom field
 * 
 * @author kpl
 *
 */
public class ShowIssueReviewInformation implements Condition {

	@Override
	public void init(Map<String, String> params) throws PluginParseException {

	}

	@Override
	public boolean shouldDisplay(Map<String, Object> context) {
		IssueImpl issue = (IssueImpl) context.get("issue");	
		CustomField reviewIdCustomField = BeanUtil.getCustomFieldByName(CUSTOM_FIELD_REVIEWID);		
		return reviewIdCustomField != null && reviewIdCustomField.hasValue(issue);
	}

}
