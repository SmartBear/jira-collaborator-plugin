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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;
import com.smartbear.collaborator.BaseServlet;

public class AdminServlet extends BaseServlet {
	private static final Logger log = LoggerFactory.getLogger(AdminServlet.class);


	public AdminServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer) {
		super(userManager, loginUriProvider, renderer);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		super.doGet(request, response);
	
		Map<String, Object> context = Maps.newHashMap();
        context.put("projectKey", request.getParameter("projectKey")); 
		response.setContentType("text/html;charset=utf-8");
		renderer.render("admin.vm", context, response.getWriter());
	}

}