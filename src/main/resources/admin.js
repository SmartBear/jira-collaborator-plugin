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
AJS.toInit(function() {

	  var baseUrl = AJS.$("meta[name='ajs-context-path']").attr("content");
	  var secureWarning = "Be careful. You are using an insecure protocol for transferring secure data!";

	  function checkSecureProtocol() {
		  if (baseUrl.indexOf("https://") >= 0) {
	    	  alert(secureWarning);
	      }
	  }

	  function computeConfigData() {
		  return '{ "url": "' + encodeURIComponent(AJS.$("#url").attr("value")) +
	      '", "login": "' + encodeURIComponent(AJS.$("#login").attr("value")) +
	      '", "password": "' + encodeURIComponent(AJS.$("#password").attr("value")) +
	      '", "fisheyeUrl": "' + encodeURIComponent(AJS.$("#fisheyeUrl").attr("value")) +
	      '", "fisheyeLogin": "' + encodeURIComponent(AJS.$("#fisheyeLogin").attr("value")) +
	      '", "fisheyePassword": "' + encodeURIComponent(AJS.$("#fisheyePassword").attr("value")) +
	      '", "allowEmptyReviewCreation": "' + encodeURIComponent(AJS.$("#jiraAllowEmptyReviewCreation").checked) +
	      '", "projectKey": "' + encodeURIComponent(AJS.$("#projectKey").attr("value")) + '"}';
	  }

	  function populateForm() {
	      AJS.$.ajax({
	      url: baseUrl + "/rest/collab/1.0/project",
	      dataType: "json",
	      data: {"projectKey": AJS.$("#projectKey").attr("value")},
	      type: "GET",

	      success: function(config) {
			AJS.$("#url").attr("value", config.url === undefined ? '' : decodeURIComponent(config.url) );
	        AJS.$("#login").attr("value", config.login === undefined ? '' : decodeURIComponent(config.login) );
	        AJS.$("#password").attr("value", config.password === undefined ? '' : decodeURIComponent(config.password));
	        AJS.$("#fisheyeUrl").attr("value", config.fisheyeUrl === undefined ? '' : decodeURIComponent(config.fisheyeUrl));
	        AJS.$("#fisheyeLogin").attr("value", config.fisheyeLogin === undefined ? '' : decodeURIComponent(config.fisheyeLogin));
	        AJS.$("#fisheyePassword").attr("value", config.fisheyePassword === undefined ? '' : decodeURIComponent(config.fisheyePassword));

	        var stringAllowEmptyReviewCreation = config.allowEmptyReviewCreation === undefined ? '' : decodeURIComponent(config.allowEmptyReviewCreation);
	        var boolAllowEmptyReviewCreation = allowEmptyReviewCreation.toLowerCase() === 'true';
	        AJS.$("#jiraAllowEmptyReviewCreation").checked = boolAllowEmptyReviewCreation;
	      }
	    });
	    checkSecureProtocol();
	  }
	  function updateConfig() {
		  makePutRequest( "/rest/collab/1.0/project" );
	  }

	  function checkCollabConnection() {
		  makePutRequest( "/rest/collab/1.0/project/checkCollabConnection" );
	  }

	  function checkFisheyeConnection() {
		  makePutRequest( "/rest/collab/1.0/project/checkFisheyeConnection" );
	  }

	  function makePutRequest(service) {
	      AJS.$.ajax({
	      url: baseUrl + "" + service,
	      type: "PUT",
	      contentType: "application/json",
	      data: computeConfigData(),
	      dataType: "json",
	      success: function(restResponse) {
	    	  if (restResponse.statusCode == 1) {
	    		  JIRA.Messages.showSuccessMsg(restResponse.message);
	    	  } else {
	    		  JIRA.Messages.showErrorMsg(restResponse.message);
	    	  }
		   }
	    });
	  }

	  populateForm();

	  AJS.$("#admin").submit(function(e) {
	    e.preventDefault();
	    updateConfig();
	  });

	  AJS.$("#testCollabConnection").click(function(e) {
		    e.preventDefault();
		    checkCollabConnection();
		  });

	  AJS.$("#testFisheyeConnection").click(function(e) {
		    e.preventDefault();
		    checkFisheyeConnection();
		  });
	});
