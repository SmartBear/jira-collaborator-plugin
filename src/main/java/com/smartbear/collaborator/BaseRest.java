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
package com.smartbear.collaborator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.smartbear.collaborator.json.collab.JsonCommandResult;
import com.smartbear.collaborator.json.collab.JsonError;
import com.smartbear.collaborator.util.Util;

public class BaseRest {

	protected final UserManager userManager;
	protected final PluginSettingsFactory pluginSettingsFactory;
	protected final TransactionTemplate transactionTemplate;

	public BaseRest(UserManager userManager, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate) {
		this.userManager = userManager;
		this.pluginSettingsFactory = pluginSettingsFactory;
		this.transactionTemplate = transactionTemplate;
	}

	// Converts json response to result map or throws exception if response
	// contains errors
	public Map<String, Object> getResultMap(JsonCommandResult[] resultArray) throws Exception {
		for (JsonCommandResult jsonResult : resultArray) {
			if (!Util.isEmpty(jsonResult.getErrors())) {
				// json errors
				throw new Exception(parseJsonError(jsonResult.getErrors()));
			}
			if (!Util.isEmpty(jsonResult.getResult())) {
				return jsonResult.getResult();
			}
		}
		return Collections.emptyMap();
	}

	private String parseJsonError(List<JsonError> jsonErrors) {
		StringBuilder builder = new StringBuilder();
		for (JsonError jsonError : jsonErrors) {
			builder.append(jsonError.getMessage());
			builder.append("\n");
		}
		return builder.toString();
	}
}
