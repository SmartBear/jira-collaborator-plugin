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
package com.smartbear.collaborator.issue;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;
import org.ofbiz.core.entity.GenericEntityException;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.context.GlobalIssueContext;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.customfields.CustomFieldSearcher;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.smartbear.collaborator.BaseRest;
import com.smartbear.collaborator.admin.ConfigModel;
import com.smartbear.collaborator.json.RestResponse;
import com.smartbear.collaborator.json.collab.*;
import com.smartbear.collaborator.json.fisheye.*;
import com.smartbear.collaborator.util.BeanUtil;
import com.smartbear.collaborator.util.Util;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.apache.commons.codec.binary.Base64;

import static com.smartbear.collaborator.util.Constants.*;

/**
 * This class is used for rest get request from jira view issue by custom button
 * "Create/Update Review". This request downloads raw files from Fisheye server,
 * uploads them to Colaborator server and adds changelists to new or existing
 * review
 * 
 * @author kpl
 * 
 */
@Path("/issue")
public class IssueRest extends BaseRest {

	/* Custom fields of issue where review information is stored */
	private CustomField reviewIdCustomField;
	private CustomField reviewLinkCustomField;
	private CustomField reviewPhaseCustomField;
	private CustomField reviewParticipantsCustomField;
	private CustomField reviewUploadedCommitListCustomField;

	/* Used to know what commit id's were already added to review */
	private Set<String> uploadedCommitList;

	/* Contains plugin configuration data */
	private ConfigModel configModel;

	private ObjectMapper mapper;
	private PluginSettings pluginSettings;
	private MutableIssue issue;
	IssueChangeHolder changeHolder;

	public IssueRest(UserManager userManager, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate) {
		super(userManager, pluginSettingsFactory, transactionTemplate);
	}

	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@QueryParam("issueKey") final String issueKey, @Context HttpServletRequest request) {

		final RestResponse restResponse = new RestResponse(RestResponse.STATUS_SUCCESS, "Review was successfully created/updated");
		try {
			String username = userManager.getRemoteUsername(request);

			// Check authorized user
			if (username == null) {
				return Response.status(Status.UNAUTHORIZED).build();
			}

			// Find issue by issueId
			issue = BeanUtil.getIssueByKey(issueKey);

			// Get plugin settings by project key
			pluginSettings = pluginSettingsFactory.createSettingsForKey(issue.getProjectObject().getKey());

			// Get plugin configuration settings from plugin settings
			configModel = Util.getConfigModel(pluginSettings);

			loadCustomFields();

			loadUploadedCommitList();

			mapper = new ObjectMapper();
			changeHolder = new DefaultIssueChangeHolder();

			// Get commits attached to issue
			// Example:
			// http://nb-kpl:2990/jira/rest/dev-status/1.0/issue/detail?issueId=10100&applicationType=fecru&dataType=repository
			
			//JsonDevStatus jsonDevStatus = getFisheyeDevStatus(issue.getId(), request);
			
			List<Changeset> changesetList = getFisheyeChangesets (configModel, issue.getKey(), request);
			if (!changesetList.isEmpty()) {
				// download raw file contents from Fisheye server and put them
				// to zip file
				java.io.File targetZipFile = downloadRawFilesFromFisheye(changesetList);
				
				//Check if collab auth ticket is valid
			    checkCollabTicket();
			    
			    // upload zip file with raw files to Collaborator Server
				uploadRawFilesToCollab(targetZipFile);
				
				//get review id if exist or create new one
				String reviewId = (String) reviewIdCustomField.getValue(issue);
				if (reviewId == null) {
					CollabUserInfo collabUserInfo = getCollabUserInfo(username);
					reviewId = createReview(collabUserInfo);
					addAuthorToReview(reviewId, collabUserInfo) ;
				}

				//addchangelist to new/old review (depend on reviewModel)
				addFiles(changesetList, reviewId, request);
				
				// Update already uploaded commit id list
				issue.setCustomFieldValue(reviewUploadedCommitListCustomField, convertUploadedCommitListToString());
				reviewUploadedCommitListCustomField.updateValue(null, issue, new ModifiedValue(null, convertUploadedCommitListToString()), changeHolder);
			}

		} catch (Exception e) {
			restResponse.setStatusCode(RestResponse.STATUS_ERROR);
			restResponse.setMessage(e.getMessage());

		}

		return Response.ok(transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction() {
				return restResponse;
			}
		})).build();
	}

	private String convertUploadedCommitListToString() {
		StringBuilder result = new StringBuilder();
		for (String commit : uploadedCommitList) {
			result.append(commit);
			result.append(";");
		}
		return result.toString();
	}

	/**
	 * Calculates set of commit id's that were already added to review
	 * 
	 * @param issue
	 */
	private void loadUploadedCommitList() {
		String commitListStr = (String) reviewUploadedCommitListCustomField.getValue(issue);
		if (!Util.isEmpty(commitListStr)) {
			uploadedCommitList = new HashSet<String>(Arrays.asList(commitListStr.split(";")));
		} else {
			uploadedCommitList = new HashSet<String>();
		}
	}

	/**
	 * Creates or loads(if exist) custom fields for issue where review
	 * information is stored
	 * 
	 * @throws GenericEntityException
	 */
	private void loadCustomFields() throws GenericEntityException {
		CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
		CustomFieldType fieldType = customFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:textfield");

		CustomFieldSearcher fieldSearcher = null;
		List searchers = customFieldManager.getCustomFieldSearchers(fieldType);
		if (searchers != null && !searchers.isEmpty()) {
			fieldSearcher = (CustomFieldSearcher) searchers.get(0);
		}

		List<JiraContextNode> contexts = new ArrayList<JiraContextNode>();
		contexts.add(GlobalIssueContext.getInstance());

		reviewIdCustomField = customFieldManager.getCustomFieldObjectByName(CUSTOM_FIELD_REVIEWID);
		if (reviewIdCustomField == null) {
			reviewIdCustomField = customFieldManager.createCustomField(CUSTOM_FIELD_REVIEWID, CUSTOM_FIELD_REVIEWID, fieldType, fieldSearcher, contexts, ComponentAccessor.getConstantsManager()
					.getAllIssueTypes());
		}

		reviewLinkCustomField = customFieldManager.getCustomFieldObjectByName(CUSTOM_FIELD_REVIEWLINK);
		if (reviewLinkCustomField == null) {
			reviewLinkCustomField = customFieldManager.createCustomField(CUSTOM_FIELD_REVIEWLINK, CUSTOM_FIELD_REVIEWLINK, fieldType, fieldSearcher, contexts, ComponentAccessor.getConstantsManager()
					.getAllIssueTypes());
		}

		reviewPhaseCustomField = customFieldManager.getCustomFieldObjectByName(CUSTOM_FIELD_REVIEWPHASE);
		if (reviewPhaseCustomField == null) {
			reviewPhaseCustomField = customFieldManager.createCustomField(CUSTOM_FIELD_REVIEWPHASE, CUSTOM_FIELD_REVIEWPHASE, fieldType, fieldSearcher, contexts, ComponentAccessor
					.getConstantsManager().getAllIssueTypes());
		}

		reviewParticipantsCustomField = customFieldManager.getCustomFieldObjectByName(CUSTOM_FIELD_REVIEWPARTICIPANTS);
		if (reviewParticipantsCustomField == null) {
			reviewParticipantsCustomField = customFieldManager.createCustomField(CUSTOM_FIELD_REVIEWPARTICIPANTS, CUSTOM_FIELD_REVIEWPARTICIPANTS, fieldType, fieldSearcher, contexts,
					ComponentAccessor.getConstantsManager().getAllIssueTypes());
		}

		reviewUploadedCommitListCustomField = customFieldManager.getCustomFieldObjectByName(CUSTOM_FIELD_REVIEWUPLOADEDCOMMITLIST);
		if (reviewUploadedCommitListCustomField == null) {
			reviewUploadedCommitListCustomField = customFieldManager.createCustomField(CUSTOM_FIELD_REVIEWUPLOADEDCOMMITLIST, CUSTOM_FIELD_REVIEWUPLOADEDCOMMITLIST, fieldType, fieldSearcher,
					contexts, ComponentAccessor.getConstantsManager().getAllIssueTypes());
		}

	}

	/**
	 * Gets changeset list from Fisheye for current issue key
	 * 
	 * @param configModel
	 * @param issueKey
	 * @param request
	 * @return
	 * @throws Exception
	 */	
	public static List<Changeset> getFisheyeChangesets (ConfigModel configModel, String issueKey, HttpServletRequest request) throws Exception {
		try {
			List<Changeset> changesetList = new ArrayList<Changeset>();
			Client client = Client.create();
			//Get available repositories from Fisheye
			WebResource repositoriesService = client.resource(Util.encodeURL(configModel.getFisheyeUrl() + FISHEYE_REPOSITORIES_API));
			WebResource.Builder repositoriesBuilder = repositoriesService.getRequestBuilder();
			repositoriesBuilder.header("Authorization", "Basic " + getFisheyeAuthStringEncoded(configModel));
			repositoriesBuilder.header("Accept", "application/json");

			ClientResponse repositoriesResponse = repositoriesBuilder.get(ClientResponse.class);
			String repositoriesResponseString = repositoriesResponse.getEntity(String.class);
			
			//Check response status code
			if (repositoriesResponse.getStatus() == HttpURLConnection.HTTP_ACCEPTED || repositoriesResponse.getStatus() == HttpURLConnection.HTTP_CREATED
					|| repositoriesResponse.getStatus() == HttpURLConnection.HTTP_OK) {
							
				ObjectMapper mapper = new ObjectMapper();
				ObjectNode repositoriesResponseNode = (ObjectNode) mapper.readTree(repositoriesResponseString);
				ArrayNode repositories = (ArrayNode) repositoriesResponseNode.get("repository");
				for (Object repositoryObj : repositories) {
					ObjectNode repository = (ObjectNode) repositoryObj;
					String repositoryName = repository.get("name").asText();

					//Get changesets from Fisheye for current repositoryName
					WebResource changesetService = client.resource(Util.encodeURL(configModel.getFisheyeUrl() + FISHEYE_CHANGESET_API + repositoryName
							+ "&expand=changesets,revisions&comment=" + issueKey));
					WebResource.Builder changesetBuilder = changesetService.getRequestBuilder();
					changesetBuilder.header("Authorization", "Basic " + getFisheyeAuthStringEncoded(configModel));
					changesetBuilder.header("Accept", "application/json");
					
					ClientResponse changesetResponse = changesetBuilder.get(ClientResponse.class);
					String changesetResponseString = changesetResponse.getEntity(String.class);
					
					if (changesetResponse.getStatus() == HttpURLConnection.HTTP_ACCEPTED || changesetResponse.getStatus() == HttpURLConnection.HTTP_CREATED
							|| changesetResponse.getStatus() == HttpURLConnection.HTTP_OK) {
						ObjectNode changesetResponseNode = (ObjectNode) mapper.readTree(changesetResponseString);

						ObjectNode changesetsNode = (ObjectNode) changesetResponseNode.get("changesets");
						ArrayNode changesetArrayNode = (ArrayNode) changesetsNode.get("changeset");
						for (Object changesetObj : changesetArrayNode) {
							ObjectNode changesetNode = (ObjectNode) changesetObj;
							//Fill changeset object with data
							Changeset changeset = new Changeset();
							changeset.setAuthor(changesetNode.get("author").asText());
							changeset.setCsid(changesetNode.get("csid").asText());
							changeset.setComment(changesetNode.get("comment").asText());
							changeset.setRepositoryName(changesetNode.get("repositoryName").asText());
							List<File> files = new ArrayList<File>();
							ObjectNode revisionsNode = (ObjectNode) changesetNode.get("revisions");
							ArrayNode revisionArrayNode = (ArrayNode) revisionsNode.get("revision");
							for (Object revisionObj : revisionArrayNode) {
								ObjectNode revisionNode = (ObjectNode) revisionObj;
								//Fill file object with data
								File file = new File();
								file.setPath(revisionNode.get("path").asText());
								file.setRev(revisionNode.get("rev").asText());
								file.setContentLink(revisionNode.get("contentLink").asText());
								ArrayNode ancestorArrayNode = (ArrayNode) revisionNode.get("ancestor");
								for (Object ancestorObj : ancestorArrayNode) {
									TextNode ancestorNode = (TextNode) ancestorObj;
									file.setAncestor(ancestorNode.getTextValue());
								}
								file.setChangeType(revisionNode.get("fileRevisionState").asText());
								files.add(file);
							}
							changeset.setFiles(files);
							changesetList.add(changeset);
						}					
					} else {
						throw new Exception(changesetResponseString);
					}			
				}
			} else {
				throw new Exception(repositoriesResponseString);
			}

			return changesetList;
		} catch (Exception e) {
			throw new Exception("Can't get FishEye changeset information for issue " + issueKey + ". Check please FishEye url and username/password.", e);
		}	
	}
	

	/**
	 * Encodes authentication string with basa64 that is used for Base
	 * Authentication in http request
	 * 
	 * @return
	 */
	public static String getFisheyeAuthStringEncoded(ConfigModel configModel) {
		String authString = configModel.getFisheyeLogin() + ":" + configModel.getFisheyePassword();
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		return new String(authEncBytes);
	}

	
	/**
	 * Gets repository map where key is repository name and value is repository object
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private Map<String, Repository> getRepositoriesMap(HttpServletRequest request) throws Exception {
		try {
			Client client = Client.create();
			WebResource service = client.resource(Util.encodeURL(configModel.getFisheyeUrl() + FISHEYE_ADMIN_REPOSITORIES_API));
			WebResource.Builder builder = service.getRequestBuilder();
			builder.header("Authorization", "Basic " + getFisheyeAuthStringEncoded(configModel));

			ClientResponse response = builder.get(ClientResponse.class);
			String responseString = response.getEntity(String.class);
			ObjectNode responseNode = (ObjectNode) mapper.readTree(responseString);
			ArrayNode repositoriesArrayNode = (ArrayNode) responseNode.get("values");
			Map<String, Repository> repositoryMap = new HashMap<String, Repository>();
			for (Object repositoryObj  : repositoriesArrayNode) {
				ObjectNode 	repositoryNode = (ObjectNode) repositoryObj;
				Repository repository = new Repository();
				repository.setName(repositoryNode.get("name").asText());
				repository.setType(repositoryNode.get("type").asText());
				
				ObjectNode scmNode = (ObjectNode) repositoryNode.get(repository.getType());
				if (ScmToken.GIT.toString().equalsIgnoreCase(repository.getType())) {
					repository.setPath(scmNode.get("location").asText());
					repository.setScmToken(ScmToken.GIT);
				} else if ("svn".equalsIgnoreCase(repository.getType())) {
					repository.setScmToken(ScmToken.SUBVERSION);
					repository.setPath(scmNode.get("url").asText());
				} else {
					repository.setScmToken(ScmToken.NONE);
					repository.setPath("");
				}
				
				repositoryMap.put(repositoryNode.get("name").asText(), repository);				
			}
			
			return repositoryMap;

		} catch (Exception e) {
			throw new Exception("Can't get FishEye repositories information. Check please FishEye url and username/password.", e);
		}
	}

	
	/**
	 * Downloads raw files from fisheye, calculates checksum for each file and
	 * puts them to zip file
	 * 
	 * @param changesetList 
	 * @return
	 * @throws Exception
	 */
	private java.io.File downloadRawFilesFromFisheye(List<Changeset> changesetList) throws Exception {
		// Create temp zip file where versions will be put
		java.io.File targetZipFile = java.io.File.createTempFile("store-", ".zip");
		try {

			FileOutputStream fileOutputStream = new FileOutputStream(targetZipFile);
			ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
			HashSet<String> zipEntryNames = new HashSet<String>();

			Client client;
			String urlGetRawFileContent;
			WebResource service;
			ClientResponse response;
			InputStream fileInputStream;
			byte[] fileBytes;
			ZipEntry zipEntry;
			Action action;

			// Go through repositories->commits->files to put versions in temp
			// zip file
			for (Changeset changeset : changesetList) {

					for (File file : changeset.getFiles()) {

						// Get raw file content from fisheye server
						// Example
						// http://nb-kpl:8060/browse/~raw,r=HEAD/svn_test/test/log.txt
						urlGetRawFileContent = Util.encodeURL(configModel.getFisheyeUrl() + file.getContentLink());

						client = Client.create();
						service = client.resource(urlGetRawFileContent);
						WebResource.Builder builder = service.getRequestBuilder();
						builder.header("Authorization", "Basic " + getFisheyeAuthStringEncoded(configModel));
						response = builder.get(ClientResponse.class);
						fileInputStream = response.getEntity(InputStream.class);
						fileBytes = IOUtils.toByteArray(fileInputStream);

						// Set calculated md5 for raw version
						file.setMd5(calculateMd5(fileBytes));

						if (!zipEntryNames.contains(file.getMd5())) {
							zipEntry = new ZipEntry(file.getMd5());
							zipOutputStream.putNextEntry(zipEntry);

							// write version to temp zip file
							zipOutputStream.write(fileBytes);

							zipEntryNames.add(file.getMd5());
						}

						action = Util.getVersionAction(file.getChangeType());

						// Check if file was modified then download also
						// previous version
						if (action != null && action == Action.MODIFIED) {


							if (!Util.isEmpty(file.getAncestor())) {

								urlGetRawFileContent = Util.encodeURL(configModel.getFisheyeUrl() + "/browse/~raw,r=" + file.getAncestor() + "/" + changeset.getRepositoryName() + "/" + file.getPath());

								client = Client.create();
								service = client.resource(urlGetRawFileContent);
								response = service.get(ClientResponse.class);
								fileInputStream = response.getEntity(InputStream.class);
								fileBytes = IOUtils.toByteArray(fileInputStream);

								// Set calculated md5 for raw version
								file.setPreviousMd5(calculateMd5(fileBytes));

								if (!zipEntryNames.contains(file.getPreviousMd5())) {
									zipEntry = new ZipEntry(file.getPreviousMd5());
									zipOutputStream.putNextEntry(zipEntry);

									// write version to temp zip file
									zipOutputStream.write(fileBytes);

									zipEntryNames.add(file.getPreviousMd5());
								}

							} else {
								throw new Exception("Please, try to \"Create/Update Review\" a little bit later. FishEye server hasn't been refreshed commit info yet.");
							}
						}
					}				
			}

			// close ZipEntry to store the stream to the file
			zipOutputStream.closeEntry();
			zipOutputStream.close();
			fileOutputStream.close();
			return targetZipFile;

		} catch (Exception e) {
			throw new Exception("Can't download raw versions from FishEye server. Check that FishEye server is running. \n " + e.getMessage());
		}

	}

	/**
	 * Uploads zip file of raw files to Collaborator server
	 * 
	 * @param targetZipFile
	 * @throws Exception
	 */
	private void uploadRawFilesToCollab(java.io.File targetZipFile) throws Exception {
		HttpURLConnection httpUrlConnection = null;
		try {

			String crlf = "\r\n";
			String twoHyphens = "--";
			String boundary = "*****";
		
			URL url = new URL(configModel.getUrl() + URI_COLAB_UPLOAD);
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setDoOutput(true);
			

			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
			httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
			httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

			String loginCookie = "CodeCollaboratorLogin=" + configModel.getLogin();
			String ticketCookie = "CodeCollaboratorTicketId=" + configModel.getAuthTicket();

			httpUrlConnection.setRequestProperty("Cookie", loginCookie + "," + ticketCookie);

			DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());

			request.writeBytes(twoHyphens + boundary + crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + targetZipFile.getName() + "\"" + crlf);
			request.writeBytes("Content-Type: application/x-zip-compressed" + crlf);

			request.writeBytes(crlf);

			InputStream fileInStream = new FileInputStream(targetZipFile);
			request.write(IOUtils.toByteArray(fileInStream));

			request.writeBytes(crlf);
			request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

			request.flush();
			request.close();

			if (httpUrlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new Exception();
			}

		} catch (Exception e) {
			throw new Exception("Can't upload raw versions to Collaborator Server. Check plugin collaborator configuration (url, login, password).", e);
		} finally {
			if (httpUrlConnection != null) {
				httpUrlConnection.disconnect();
			}
		}
	}

	/**
	 * Checks that authentication ticket is valid for Collaborator server and updates it if needed
	 * 
	 * @throws Exception
	 */
	public void checkCollabTicket() throws Exception {

		try {
			Client client;
			WebResource service;
			String jsonRequestString;
			ClientResponse response;
			String responseString;
			Map<String, Object> resultMap;
					
			if (!Util.isEmpty(configModel.getAuthTicket())) {
			JsonCommand jsonAuthenticateCommand = new JsonCommand();
			jsonAuthenticateCommand.setCommand(COLLAB_COMMAND_AUTHENTICATE);
			jsonAuthenticateCommand.getArgs().put("login", configModel.getLogin());
			jsonAuthenticateCommand.getArgs().put("ticket", configModel.getAuthTicket());
			
			JsonCommand jsonCheckCommand = new JsonCommand();
			jsonCheckCommand.setCommand(COLLAB_COMMAND_CHECKLOGGEDIN);
		
			client = Client.create();
			service = client.resource(configModel.getUrl() + URI_COLAB_JSON);
			jsonRequestString = mapper.writeValueAsString(new JsonCommand[] { jsonAuthenticateCommand,  jsonCheckCommand});
			response = service.type("application/json").post(ClientResponse.class, jsonRequestString);
			responseString = response.getEntity(String.class);
						
				try {
					resultMap = getResultMap(mapper.readValue(responseString, JsonCommandResult[].class));
					if ("true".equals(resultMap.get("loggedIn"))) {
						return;
					}
				} catch (Exception e) {
					//Ticket is not valid
				}
			}

											
			JsonCommand jsonCommand = new JsonCommand();
			jsonCommand.setCommand(COLLAB_COMMAND_LOGINTICKET);
			jsonCommand.getArgs().put("login", configModel.getLogin());
			jsonCommand.getArgs().put("password", configModel.getPassword());

			client = Client.create();
			service = client.resource(configModel.getUrl() + URI_COLAB_JSON);
			jsonRequestString = mapper.writeValueAsString(new JsonCommand[] { jsonCommand });
			response = service.type("application/json").post(ClientResponse.class, jsonRequestString);
			responseString = response.getEntity(String.class);
			
			resultMap = getResultMap(mapper.readValue(responseString, JsonCommandResult[].class));	
			configModel.setAuthTicket((String) resultMap.get("loginTicket"));				
			pluginSettings.put(ConfigModel.class.getName() + ".authTicket", URLDecoder.decode(configModel.getAuthTicket(), "UTF-8"));

		} catch (Exception e) {
			throw new Exception("Can't update authentification ticket for Collaborator Server.\n " + e.getMessage());
		}

	}
	
	/**
	 * Gets Collaborator user info by current logged in username
	 * @return
	 * @throws Exception
	 */
	public CollabUserInfo getCollabUserInfo(String currentUsername) throws Exception {
		String collabUsername = null;
		try {
			JsonCommand authenticateCommand = new JsonCommand();
			authenticateCommand.setCommand(COLLAB_COMMAND_AUTHENTICATE);
			authenticateCommand.getArgs().put("login", configModel.getLogin());
			authenticateCommand.getArgs().put("ticket", configModel.getAuthTicket());

			JsonCommand getCollabUsernameCommand = new JsonCommand();
			getCollabUsernameCommand.setCommand(COLLAB_COMMAND_GETCOLLABUSERNAME);
			getCollabUsernameCommand.getArgs().put("remoteUsername", currentUsername);
			getCollabUsernameCommand.getArgs().put("remoteSystemToken", "JIRA");
			
			String jsonRequestString = mapper.writeValueAsString(new JsonCommand[] { authenticateCommand, getCollabUsernameCommand });
			Client client = Client.create();
			WebResource service = client.resource(configModel.getUrl() + URI_COLAB_JSON);
			ClientResponse response = service.type("application/json").post(ClientResponse.class, jsonRequestString);
			String responseString = response.getEntity(String.class);

			Map<String, Object> resultMap = getResultMap(mapper.readValue(responseString, JsonCommandResult[].class));
			if (!resultMap.isEmpty()) {
				Map<Integer, Object> remoteMapUserInfo = (Map<Integer, Object>) resultMap.get("remoteMapUserInfo");
								
				for (Map.Entry<Integer, Object> entry : remoteMapUserInfo.entrySet()) {
					return mapper.convertValue(entry.getValue(), CollabUserInfo.class);
	
				}
			}

			return null;

		} catch (Exception e) {
			throw new Exception("Can't get Collab username.\n " + e.getMessage());
		}

	}

	/**
	 * Creates new review on Collaborator server and returns review id
	 * 
	 * @param issue
	 * @return
	 * @throws Exception
	 */
	public String createReview(CollabUserInfo collabUserInfo) throws Exception {

		try {
			JsonCommand authenticateCommand = new JsonCommand();
			authenticateCommand.setCommand(COLLAB_COMMAND_AUTHENTICATE);
			authenticateCommand.getArgs().put("login", configModel.getLogin());
			authenticateCommand.getArgs().put("ticket", configModel.getAuthTicket());

			JsonCommand createReviewCommand = new JsonCommand();
			createReviewCommand.setCommand(COLLAB_COMMAND_CREATEREVIEW);
			//If Collaborator user exists for current jira username then set it as creator, 
			//else set creator as collaborator admin from jira collab plugin
			createReviewCommand.getArgs().put("creator", collabUserInfo != null ? collabUserInfo.getLogin() : configModel.getLogin());
			createReviewCommand.getArgs().put("title", issue.getSummary() != null ? issue.getSummary() : "");
			createReviewCommand.getArgs().put("customFields",
					new com.smartbear.collaborator.json.collab.CustomField[] { 
					new com.smartbear.collaborator.json.collab.CustomField("Overview", new HashSet(Arrays.asList(issue.getDescription() != null ? issue.getDescription() : "")) )	});
			createReviewCommand.getArgs().put("internalCustomFields",
					new com.smartbear.collaborator.json.collab.CustomField[] { 
					new com.smartbear.collaborator.json.collab.CustomField("JiraIssueId",  new HashSet(Arrays.asList(issue.getId().toString())) )});
			
			String jsonRequestString = mapper.writeValueAsString(new JsonCommand[] { authenticateCommand, createReviewCommand });

			Client client = Client.create();
			WebResource service = client.resource(configModel.getUrl() + URI_COLAB_JSON);
			ClientResponse response = service.type("application/json").post(ClientResponse.class, jsonRequestString);
			String responseString = response.getEntity(String.class);
			
			Map<String, Object> resultMap = getResultMap(mapper.readValue(responseString, JsonCommandResult[].class));
			String reviewId = String.valueOf(resultMap.get("reviewId"));
			
			if (reviewId == null) {
				throw new Exception("Bad response from Collab server.");
			}
							
			issue.setCustomFieldValue(reviewIdCustomField, reviewId);
			reviewIdCustomField.updateValue(null, issue, new ModifiedValue(null, reviewId), changeHolder);

			String link = Util.getRestString(configModel.getUrl()) + COLLAB_URI;
			issue.setCustomFieldValue(reviewLinkCustomField, link);
			reviewLinkCustomField.updateValue(null, issue, new ModifiedValue(null, link), changeHolder);

			issue.setCustomFieldValue(reviewPhaseCustomField, Phase.PLANNING.getTitle());
			reviewPhaseCustomField.updateValue(null, issue, new ModifiedValue(null, Phase.PLANNING.getTitle()), changeHolder);

			return reviewId;

		} catch (Exception e) {
			throw new Exception("Can't create new review on Collaborator Server.\n " + e.getMessage());
		}

	}
	
	/**
	 * Adds current jira user as author to review participants in case there is
	 * collaborator user mapped with current jira user
	 * 
	 * @param issue
	 * @return
	 * @throws Exception
	 */
	public void addAuthorToReview(String reviewId, CollabUserInfo collabUserInfo) throws Exception {
		if (collabUserInfo == null || Util.isEmpty(reviewId)) {
			return;
		}

		try {
			JsonCommand authenticateCommand = new JsonCommand();
			authenticateCommand.setCommand(COLLAB_COMMAND_AUTHENTICATE);
			authenticateCommand.getArgs().put("login", configModel.getLogin());
			authenticateCommand.getArgs().put("ticket", configModel.getAuthTicket());

			JsonCommand addAuthorToReviewCommand = new JsonCommand();
			addAuthorToReviewCommand.setCommand(COLLAB_COMMAND_ASSIGNMENT);
			
			addAuthorToReviewCommand.getArgs().put("reviewId", reviewId);
			
			
			addAuthorToReviewCommand.getArgs().put("assignments",
					new com.smartbear.collaborator.json.collab.Assignment[] { 
					new com.smartbear.collaborator.json.collab.Assignment(collabUserInfo.getLogin(), "AUTHOR", null)});
		
			String jsonRequestString = mapper.writeValueAsString(new JsonCommand[] { authenticateCommand, addAuthorToReviewCommand });

			Client client = Client.create();
			WebResource service = client.resource(configModel.getUrl() + URI_COLAB_JSON);
			ClientResponse response = service.type("application/json").post(ClientResponse.class, jsonRequestString);
			String responseString = response.getEntity(String.class);
			getResultMap(mapper.readValue(responseString, JsonCommandResult[].class));
			
			issue.setCustomFieldValue(reviewParticipantsCustomField, collabUserInfo.getFullName());
			reviewParticipantsCustomField.updateValue(null, issue, new ModifiedValue(null, collabUserInfo.getFullName()), changeHolder);

		} catch (Exception e) {
			throw new Exception("Can't add author " + collabUserInfo.getFullName() + " to review #" +reviewId + " on Collaborator Server.\n " + e.getMessage());
		}

	}
	
		
	/**
	 * Adds changelists to new or existing review on Collaborator server
	 * 
	 * @param changesetList for adding to review
	 * @param reviewId
	 * @param request
	 * @throws Exception
	 */
	public void addFiles(List<Changeset> changesetList, String reviewId, HttpServletRequest request) throws Exception {

		try {
			JsonCommand authenticateCommand = new JsonCommand();
			authenticateCommand.setCommand(COLLAB_COMMAND_AUTHENTICATE);
			authenticateCommand.getArgs().put("login", configModel.getLogin());
			authenticateCommand.getArgs().put("ticket", configModel.getAuthTicket());

			JsonCommand addFilesCommand = new JsonCommand();
			addFilesCommand.setCommand(COLLAB_COMMAND_ADDFILES);
			addFilesCommand.getArgs().put("reviewId", reviewId);

			List<ChangeList> changeLists = new ArrayList<ChangeList>();
				
			Map<String, Repository> repositoryMap = getRepositoriesMap(request);

				for (Changeset changeset : changesetList) {

					if (uploadedCommitList.contains(changeset.getCsid())) {
						continue;
					}

					List<Version> versions = new ArrayList<Version>();
					ChangeList changeList = new ChangeList();
					for (File file : changeset.getFiles()) {
						Version version = new Version();
						version.setScmPath(file.getPath());
						version.setMd5(file.getMd5());

						version.setScmVersionName(changeset.getCsid());
						Action action = Util.getVersionAction(file.getChangeType());
						version.setAction(action);
						version.setSource(FileSource.CHECKEDIN);

						// Check if file was modified then add base version to
						// changelist
						if (action != null && action == Action.MODIFIED) {
							BaseVersion baseVersion = new BaseVersion();
							baseVersion.setScmPath(file.getPath());
							baseVersion.setScmVersionName(file.getAncestor());
							baseVersion.setMd5(file.getPreviousMd5());
							baseVersion.setSource(FileSource.CHECKEDIN);
							baseVersion.setAction(Action.MODIFIED);
							version.setBaseVersion(baseVersion);
						}

						versions.add(version);
					}
			
					Repository repository = repositoryMap.get(changeset.getRepositoryName());
					changeList.setScmConnectionParameters(new String[] { repository.getPath() });				
					changeList.setScmToken(repository.getScmToken());
					changeList.setCommitInfo(new CommitInfo(changeset.getAuthor(), changeset.getComment(), changeset.getDate()));

					Version[] versionsArray = new Version[0];
					changeList.setVersions(versions.toArray(versionsArray));
					changeLists.add(changeList);
					uploadedCommitList.add(changeset.getCsid());
				}
			

			addFilesCommand.getArgs().put("changelists", changeLists);

			String jsonRequestString = mapper.writeValueAsString(new JsonCommand[] { authenticateCommand, addFilesCommand });

			Client client = Client.create();
			WebResource service = client.resource(configModel.getUrl() + URI_COLAB_JSON);
			ClientResponse response = service.type("application/json").post(ClientResponse.class, jsonRequestString);
			String responseString = response.getEntity(String.class);

			getResultMap(mapper.readValue(responseString, JsonCommandResult[].class));

		} catch (Exception e) {
			throw new Exception("Can't addchangelists to Collaborator Server.\n " + e.getMessage());
		}
	}

	private String calculateMd5(byte[] fileBytes) throws Exception {
		return DigestUtils.md5Hex(fileBytes);
	}

}
