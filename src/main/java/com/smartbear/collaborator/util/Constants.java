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

public interface Constants {
	public static final String COLLAB_URI = "/ui#review:id=";

	public static final String COLLAB_PHASE_PLANNING = "PLANNING";
	public static final String CUSTOM_FIELD_REVIEWLINK = "collabReviewLink";
	public static final String CUSTOM_FIELD_REVIEWUPLOADEDCOMMITLIST = "collabReviewUploadedCommitList";
	public static final String CUSTOM_FIELD_REVIEWID = "Review Id";
	public static final String CUSTOM_FIELD_REVIEWPHASE = "Review Phase";
	public static final String CUSTOM_FIELD_REVIEWPARTICIPANTS = "Review Participants";

	public static final String REVIEW_MODEL = "review_model";
	public static final String FISHEYE_REPOSITORIES_API = "/rest-service-fe/repositories-v1";
	public static final String FISHEYE_ADMIN_REPOSITORIES_API = "/rest-service-fecru/admin/repositories";
	
	public static final String FISHEYE_CHANGESET_API = "/rest-service-fe/changeset-v1/listChangesets?rep=";	
	public static final String URI_COLAB_JSON = "/services/json/v1";
	public static final String URI_COLAB_UPLOAD = "/contentupload";
	
	public static final String COLLAB_COMMAND_CREATEREVIEW = "ReviewService.createReview";
	public static final String COLLAB_COMMAND_CHECKLOGGEDIN = "Examples.checkLoggedIn";
	public static final String COLLAB_COMMAND_LOGINTICKET = "SessionService.getLoginTicket";
	public static final String COLLAB_COMMAND_AUTHENTICATE = "SessionService.authenticate";
	public static final String COLLAB_COMMAND_ADDFILES = "ReviewService.addFiles";
	public static final String COLLAB_COMMAND_GETCOLLABUSERNAME = "UserService.findUserByRemoteServiceAndRemoteUser";

	
	
}
