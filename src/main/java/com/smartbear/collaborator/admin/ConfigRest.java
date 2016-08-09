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

import static com.smartbear.collaborator.util.Constants.COLLAB_COMMAND_LOGINTICKET;
import static com.smartbear.collaborator.util.Constants.FISHEYE_ADMIN_REPOSITORIES_API;
import static com.smartbear.collaborator.util.Constants.URI_COLAB_JSON;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

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
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.smartbear.collaborator.BaseRest;
import com.smartbear.collaborator.issue.IssueRest;
import com.smartbear.collaborator.json.RestResponse;
import com.smartbear.collaborator.json.collab.JsonCommand;
import com.smartbear.collaborator.json.collab.JsonCommandResult;
import com.smartbear.collaborator.json.collab.ScmToken;
import com.smartbear.collaborator.json.fisheye.Repository;
import com.smartbear.collaborator.util.BeanUtil;
import com.smartbear.collaborator.util.Util;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

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
	 * 
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
				savePluginSettings(pluginSettings, configModel, restResponse);			
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
	 * 
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
				return Util.getConfigModel(settings);
			}
		})).build();
	}
	
	/**
	 * Used to check collab connection with username/password
	 * 
	 * @param projectKey
	 * @param request
	 * @return
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("checkCollabConnection")
	public Response checkCollabConnection(final ConfigModel configModel, @Context HttpServletRequest request) {
		String username = userManager.getRemoteUsername(request);
		if (username == null || !userManager.isSystemAdmin(username)) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		return Response.ok(transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction() {
				final RestResponse restResponse = new RestResponse();
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(configModel.getProjectKey());
				savePluginSettings(settings, configModel, restResponse);		
				String errors = testCollabConnection(Util.getConfigModel(settings));
				if (errors == null) {
					restResponse.setMessage("Connection to Collaborator server is successful!");
					restResponse.setStatusCode(RestResponse.STATUS_SUCCESS);
				} else {
					restResponse.setMessage("Connection to Collaborator server was failed");
					restResponse.setDescription(errors);
					restResponse.setStatusCode(RestResponse.STATUS_ERROR);
				}
				return restResponse;
			}
		})).build();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("checkFisheyeConnection")
	public Response checkFisheyeConnection(final ConfigModel configModel, @Context HttpServletRequest request) {
		String username = userManager.getRemoteUsername(request);
		if (username == null || !userManager.isSystemAdmin(username)) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		return Response.ok(transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction() {
				final RestResponse restResponse = new RestResponse();
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(configModel.getProjectKey());
				savePluginSettings(settings, configModel, restResponse);		
				String errors = testFisheyeConnection(Util.getConfigModel(settings));
				if (errors == null) {
					restResponse.setMessage("Connection to Fisheye server is successful!");
					restResponse.setStatusCode(RestResponse.STATUS_SUCCESS);
				} else {
					restResponse.setMessage("Connection to Fisheye server was failed");
					restResponse.setDescription(errors);
					restResponse.setStatusCode(RestResponse.STATUS_ERROR);
				}
				return restResponse;
			}
		})).build();
	}

	private String testCollabConnection(ConfigModel configModel) {
		try {
			JsonCommand jsonCommand = new JsonCommand();
			jsonCommand.setCommand(COLLAB_COMMAND_LOGINTICKET);
			jsonCommand.getArgs().put("login", configModel.getLogin());
			jsonCommand.getArgs().put("password", configModel.getPassword());

			Client client = Client.create();
			ObjectMapper mapper = new ObjectMapper();
			WebResource service = client.resource(configModel.getUrl() + URI_COLAB_JSON);
			String jsonRequestString = mapper.writeValueAsString(new JsonCommand[] { jsonCommand });
			ClientResponse response = service.type("application/json").post(ClientResponse.class, jsonRequestString);
			String responseString = response.getEntity(String.class);
			getResultMap(mapper.readValue(responseString, JsonCommandResult[].class));
			return null;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	private String testFisheyeConnection(ConfigModel configModel) {
		try {
			Client client = Client.create();
			WebResource service = client.resource(Util.encodeURL(configModel.getFisheyeUrl() + FISHEYE_ADMIN_REPOSITORIES_API));
			WebResource.Builder builder = service.getRequestBuilder();
			builder.header("Authorization", "Basic " + IssueRest.getFisheyeAuthStringEncoded(configModel));

			ClientResponse response = builder.get(ClientResponse.class);
			
			String responseString = response.getEntity(String.class);
			if (response.getResponseStatus() != Response.Status.OK) {
				return responseString;
			}
		} catch (Exception e) {
			return e.toString();
		} 
			
		return null;
	}
	
	private void savePluginSettings(PluginSettings pluginSettings, ConfigModel configModel, RestResponse restResponse) {
		try {

			try {
				pluginSettings.put(ConfigModel.class.getName() + ".url", Util.compileDomainUrl(URLDecoder.decode(configModel.getUrl(), "UTF-8")));
				pluginSettings.put(ConfigModel.class.getName() + ".login", URLDecoder.decode(configModel.getLogin(), "UTF-8"));
				pluginSettings.put(ConfigModel.class.getName() + ".password", URLDecoder.decode(configModel.getPassword(), "UTF-8"));
			} catch (MalformedURLException e) {
				restResponse.setStatusCode(RestResponse.STATUS_ERROR);
				restResponse.setMessage("Collaborator URL has wrong format. Example: http(s)://host(:port)");
			}

			pluginSettings.put(ConfigModel.class.getName() + ".fisheyeLogin", URLDecoder.decode(configModel.getFisheyeLogin(), "UTF-8"));
			pluginSettings.put(ConfigModel.class.getName() + ".fisheyePassword", URLDecoder.decode(configModel.getFisheyePassword(), "UTF-8"));
			pluginSettings.put(ConfigModel.class.getName() + ".allowEmptyReviewCreation", URLDecoder.decode(configModel.getAllowEmptyReviewCreation().toString(), "UTF-8"));

			try {
				pluginSettings.put(ConfigModel.class.getName() + ".fisheyeUrl", Util.compileDomainUrl(URLDecoder.decode(configModel.getFisheyeUrl(), "UTF-8")));
			} catch (MalformedURLException e) {
				restResponse.setStatusCode(RestResponse.STATUS_ERROR);
				restResponse.setMessage("Fisheye URL has wrong format. Example: http(s)://host(:port)");
			}
			
		} catch (UnsupportedEncodingException e) {
			restResponse.setStatusCode(RestResponse.STATUS_ERROR);
			restResponse.setMessage("Encoding exception has occured");
			restResponse.setDescription(ExceptionUtils.getStackTrace(e));
		}
		try {
		//Create custom fields if they are not already exist
		BeanUtil.loadReviewIdCustomField(); 
		BeanUtil.loadReviewLinkCustomField(); 
		BeanUtil.loadReviewPhaseCustomField(); 
		BeanUtil.loadReviewParticipantsCustomField(); 
		BeanUtil.loadReviewUploadedCommitListCustomField();
		} catch (GenericEntityException e) {
			restResponse.setStatusCode(RestResponse.STATUS_ERROR);
			restResponse.setMessage("Can't create custom fields");
			restResponse.setDescription(ExceptionUtils.getStackTrace(e));
		}
		
	}

}
