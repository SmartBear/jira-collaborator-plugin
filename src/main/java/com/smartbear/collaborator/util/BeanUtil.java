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

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;

public class BeanUtil {

	public static MutableIssue getIssuebyId(Long issueId) {
		return ComponentAccessor.getIssueManager().getIssueObject(issueId);
	}

	public static MutableIssue getIssueByKey(String key) {
		return ComponentAccessor.getIssueManager().getIssueByKeyIgnoreCase(key);
	}

	public static CustomField getCustomFieldByName(String name) {
		return ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName(name);
	}

}
