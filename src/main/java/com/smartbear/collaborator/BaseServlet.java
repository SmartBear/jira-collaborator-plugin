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

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;

public class BaseServlet extends HttpServlet {
	
	private final UserManager userManager;
	private final LoginUriProvider loginUriProvider;
	protected final TemplateRenderer renderer;
	
	public BaseServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer) {
		this.userManager = userManager;
		this.loginUriProvider = loginUriProvider;
		this.renderer = renderer;
	}
	
	/*
	 * Checks that the user is logged in before performing any operations. If they are not, then ask them to log in before proceeding.
	 * Also checks that the user is an administrator. If they are logged in but not an administrator, they'll still be asked to log in.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String username = userManager.getRemoteUsername(request);
		if (username == null || !userManager.isAdmin(username)) {
			redirectToLogin(request, response);
			return;
		}
	
		
	}
	
	private URI getUri(HttpServletRequest request) {
		StringBuffer builder = request.getRequestURL();
		if (request.getQueryString() != null) {
			builder.append("?");
			builder.append(request.getQueryString());
		}
		return URI.create(builder.toString());
	}
	
	private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
	}

}
