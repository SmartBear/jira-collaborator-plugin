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
	  var secureWarning = "Be careful. You are using not seccure protocol for transferring secure data!";
	  	  
	  function checkSecureProtocol() {
		  if (baseUrl.startsWith("https://") === false) {
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
	      '", "projectKey": "' + encodeURIComponent(AJS.$("#projectKey").attr("value")) + '"}';	  
	  }
	  
	  function populateForm() {		  
	      AJS.$.ajax({	      
	      url: baseUrl + "/rest/collab/1.0/project",
	      dataType: "json",
	      data: {"projectKey": AJS.$("#projectKey").attr("value")},	      
	      type: "GET",
	      
	      success: function(config) {
	        AJS.$("#url").attr("value", decodeURIComponent(config.url));
	        AJS.$("#login").attr("value", decodeURIComponent(config.login));
	        AJS.$("#password").attr("value", decodeURIComponent(config.password));
	        AJS.$("#fisheyeUrl").attr("value", decodeURIComponent(config.fisheyeUrl));
	        AJS.$("#fisheyeLogin").attr("value", decodeURIComponent(config.fisheyeLogin));
	        AJS.$("#fisheyePassword").attr("value", decodeURIComponent(config.fisheyePassword));
	      }
	    });
	    checkSecureProtocol();
	  }
	  function updateConfig() {
		  checkSecureProtocol();
	      AJS.$.ajax({      
	      url: baseUrl + "/rest/collab/1.0/project",
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
	  
	  function checkCollabConnection() {
	      AJS.$.ajax({      
	      url: baseUrl + "/rest/collab/1.0/project/checkCollabConnection",
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
	  
	  function checkFisheyeConnection() {
	      AJS.$.ajax({      
	      url: baseUrl + "/rest/collab/1.0/project/checkFisheyeConnection",
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