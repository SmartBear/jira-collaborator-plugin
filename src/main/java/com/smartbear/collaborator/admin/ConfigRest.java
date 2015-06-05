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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.smartbear.collaborator.BaseRest;
import com.smartbear.collaborator.json.RestResponse;
import com.smartbear.collaborator.util.Util;

/**
 * This class is used for rest get/put requests from jira project configuration
 * section for adding plugin configuration to each jira project
 * 
 * @author kpl
 * 
 */
@Path("/project")
public class ConfigRest extends BaseRest {

	public ConfigRest(UserManager userManager, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate) {
		super(userManager, pluginSettingsFactory, transactionTemplate);
	}

	/**
	 * Used to save plugin configuration for certain project
	 * @param configModel
	 * @param request
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response put(final ConfigModel configModel, @Context HttpServletRequest request) {
		final RestResponse restResponse = new RestResponse(RestResponse.STATUS_SUCCESS, "Collaborator configuration was successfully updated.");
		String username = userManager.getRemoteUsername(request);
		if (username == null || !userManager.isSystemAdmin(username)) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		final String projectKey = configModel.getProjectKey();

		transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction() {
				PluginSettings pluginSettings = pluginSettingsFactory.createSettingsForKey(projectKey);
				try {

					pluginSettings.put(ConfigModel.class.getName() + ".url", Util.compileDomainUrl(URLDecoder.decode(configModel.getUrl(), "UTF-8")));
					pluginSettings.put(ConfigModel.class.getName() + ".login", URLDecoder.decode(configModel.getLogin(), "UTF-8"));
					pluginSettings.put(ConfigModel.class.getName() + ".password", URLDecoder.decode(configModel.getPassword(), "UTF-8"));

					pluginSettings.put(ConfigModel.class.getName() + ".fisheyeLogin", URLDecoder.decode(configModel.getFisheyeLogin(), "UTF-8"));
					pluginSettings.put(ConfigModel.class.getName() + ".fisheyePassword", URLDecoder.decode(configModel.getFisheyePassword(), "UTF-8"));

				} catch (UnsupportedEncodingException e) {
					restResponse.setStatusCode(RestResponse.STATUS_ERROR);
					restResponse.setMessage("Encoding exception has occured");					
					restResponse.setDescription(ExceptionUtils.getStackTrace(e));
				} catch (MalformedURLException e) {
					restResponse.setStatusCode(RestResponse.STATUS_ERROR);
					restResponse.setMessage("Collaborator URL has wrong format. Example: http(s)://host(:port)");
				}
				return null;
			}
		});

		return Response.ok(transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction() {
				return restResponse;
			}
		})).build();

	}

	/**
	 * Used to get plugin configuration for certain project
	 * @param projectKey
	 * @param request
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public Response get(@QueryParam("projectKey") final String projectKey, @Context HttpServletRequest request) {
		String username = userManager.getRemoteUsername(request);
		if (username == null || !userManager.isSystemAdmin(username)) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		return Response.ok(transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				ConfigModel configModel = new ConfigModel();

				configModel.setUrl(Util.getRestString((String) settings.get(ConfigModel.class.getName() + ".url")));
				configModel.setLogin(Util.getRestString((String) settings.get(ConfigModel.class.getName() + ".login")));
				configModel.setPassword(Util.getRestString((String) settings.get(ConfigModel.class.getName() + ".password")));

				configModel.setFisheyeLogin(Util.getRestString((String) settings.get(ConfigModel.class.getName() + ".fisheyeLogin")));
				configModel.setFisheyePassword(Util.getRestString((String) settings.get(ConfigModel.class.getName() + ".fisheyePassword")));

				return configModel;
			}
		})).build();
	}

}
