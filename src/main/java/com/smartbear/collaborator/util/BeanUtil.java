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

import static com.smartbear.collaborator.util.Constants.CUSTOM_FIELD_REVIEWID;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.context.GlobalIssueContext;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.customfields.CustomFieldSearcher;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;

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
	
	public static List getIssueTypes() {
		Collection<IssueType> issueTypes = ComponentAccessor.getConstantsManager().getAllIssueTypeObjects();
		
		Method getGenericValueMethod = null;
		try {
			//for JIRA 6 we should convert IssueType to GenericValue while creating new custom field
			getGenericValueMethod = IssueType.class.getMethod("getGenericValue", (Class<?>[]) null);
			List<GenericValue> resulList = new ArrayList<GenericValue>(issueTypes.size());
			for(IssueType issueType : issueTypes) {
				resulList.add(issueType.getGenericValue());
			}
			return resulList;
		} catch (Exception e) {
			//for JIRA 7 we can use IssueType while creating new custom field
			return new ArrayList(issueTypes);
		}
		
	}
	
	public static CustomField loadReviewIdCustomField() throws GenericEntityException {
		CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
		CustomField reviewIdCustomField = customFieldManager.getCustomFieldObjectByName(Constants.CUSTOM_FIELD_REVIEWID);
		if (reviewIdCustomField == null) {
			
			CustomFieldType fieldType = getTextCustomFieldType(customFieldManager);
			CustomFieldSearcher fieldSearcher = getCustomFieldSearcher(customFieldManager, fieldType);

			List<JiraContextNode> contexts = Arrays.asList(new JiraContextNode[]{GlobalIssueContext.getInstance()});
			reviewIdCustomField = customFieldManager.createCustomField(CUSTOM_FIELD_REVIEWID, CUSTOM_FIELD_REVIEWID, fieldType, fieldSearcher, contexts, getIssueTypes());		
		}
		
		return reviewIdCustomField;
	}
	
	public static CustomField loadReviewLinkCustomField() throws GenericEntityException {
		CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
		CustomField reviewLinkCustomField = customFieldManager.getCustomFieldObjectByName(Constants.CUSTOM_FIELD_REVIEWLINK);
		if (reviewLinkCustomField == null) {				
			CustomFieldType fieldType = getTextCustomFieldType(customFieldManager);
			CustomFieldSearcher fieldSearcher = getCustomFieldSearcher(customFieldManager, fieldType);
			List<JiraContextNode> contexts = Arrays.asList(new JiraContextNode[]{GlobalIssueContext.getInstance()});
			reviewLinkCustomField = customFieldManager.createCustomField(Constants.CUSTOM_FIELD_REVIEWLINK, Constants.CUSTOM_FIELD_REVIEWLINK, fieldType, fieldSearcher, contexts, getIssueTypes());		
		}
		
		return reviewLinkCustomField;
	}
	
	public static CustomField loadReviewPhaseCustomField() throws GenericEntityException {
		CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
		CustomField reviewPhaseCustomField = customFieldManager.getCustomFieldObjectByName(Constants.CUSTOM_FIELD_REVIEWPHASE);
		if (reviewPhaseCustomField == null) {					
			CustomFieldType fieldType = getTextCustomFieldType(customFieldManager);
			CustomFieldSearcher fieldSearcher = getCustomFieldSearcher(customFieldManager, fieldType);
			List<JiraContextNode> contexts = Arrays.asList(new JiraContextNode[]{GlobalIssueContext.getInstance()});
			reviewPhaseCustomField = customFieldManager.createCustomField(Constants.CUSTOM_FIELD_REVIEWPHASE, Constants.CUSTOM_FIELD_REVIEWPHASE, fieldType, fieldSearcher, contexts, getIssueTypes());		
		}
		
		return reviewPhaseCustomField;
	}
	
	public static CustomField loadReviewParticipantsCustomField() throws GenericEntityException {
		CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
		CustomField reviewParticipantsCustomField = customFieldManager.getCustomFieldObjectByName(Constants.CUSTOM_FIELD_REVIEWPARTICIPANTS);
		if (reviewParticipantsCustomField == null) {		
			CustomFieldType fieldType = getTextCustomFieldType(customFieldManager);
			CustomFieldSearcher fieldSearcher = getCustomFieldSearcher(customFieldManager, fieldType);
			List<JiraContextNode> contexts = Arrays.asList(new JiraContextNode[]{GlobalIssueContext.getInstance()});
			reviewParticipantsCustomField = customFieldManager.createCustomField(Constants.CUSTOM_FIELD_REVIEWPARTICIPANTS, Constants.CUSTOM_FIELD_REVIEWPARTICIPANTS, fieldType, fieldSearcher, contexts, getIssueTypes());		
		}
		
		return reviewParticipantsCustomField;
	}
	
	public static CustomField loadReviewUploadedCommitListCustomField() throws GenericEntityException {
		CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
		CustomField reviewUploadedCommitListCustomField = customFieldManager.getCustomFieldObjectByName(Constants.CUSTOM_FIELD_REVIEWUPLOADEDCOMMITLIST);
		if (reviewUploadedCommitListCustomField == null) {					
			CustomFieldType fieldType = getTextCustomFieldType(customFieldManager);
			CustomFieldSearcher fieldSearcher = getCustomFieldSearcher(customFieldManager, fieldType);
			List<JiraContextNode> contexts = Arrays.asList(new JiraContextNode[]{GlobalIssueContext.getInstance()});
			reviewUploadedCommitListCustomField = customFieldManager.createCustomField(Constants.CUSTOM_FIELD_REVIEWUPLOADEDCOMMITLIST, Constants.CUSTOM_FIELD_REVIEWUPLOADEDCOMMITLIST, fieldType, fieldSearcher, contexts, getIssueTypes());		
		}
		
		return reviewUploadedCommitListCustomField;
	}
	
	private static CustomFieldSearcher getCustomFieldSearcher(CustomFieldManager customFieldManager, CustomFieldType fieldType) {
		CustomFieldSearcher fieldSearcher = null;
		List searchers = customFieldManager.getCustomFieldSearchers(fieldType);
		if (searchers != null && !searchers.isEmpty()) {
			fieldSearcher = (CustomFieldSearcher) searchers.get(0);
		}
		return fieldSearcher;
	}
	
	private static CustomFieldType getTextCustomFieldType(CustomFieldManager customFieldManager) {
		return customFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:textfield");
	}
	
	
}
