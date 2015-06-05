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
	  	    
	  function createUpdateReview() {
		var issueKey = AJS.$("meta[name='ajs-issue-key']").attr("content");
	    AJS.$.ajax({
	      url: baseUrl + "/rest/collab/1.0/issue?issueKey=" + issueKey,
	      type: "GET",
	      dataType: "json",  
	      success: function(restResponse) {	    	 
	    	  if (restResponse.statusCode == 1) {	    		  	    		  
	    		  JIRA.Messages.showSuccessMsg(restResponse.message);
	    		  location.reload();
	    	  } else {
	    		  JIRA.Messages.showErrorMsg(restResponse.message);
	    	  }
	      }		       
	    });
	  }  
	  
	  AJS.$("#collab-issue-item-link").click(function(e) {
		  
	    e.preventDefault();
	    createUpdateReview();
	  });
	  
});
	