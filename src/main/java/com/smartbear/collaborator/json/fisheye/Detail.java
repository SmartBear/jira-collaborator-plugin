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
package com.smartbear.collaborator.json.fisheye;

import java.util.List;

public class Detail {
	private List<Repository> repositories;
	private Instance _instance;

	public List<Repository> getRepositories() {
		return repositories;
	}

	public void setRepositories(List<Repository> repositories) {
		this.repositories = repositories;
	}

	public Instance get_instance() {
		return _instance;
	}

	public void set_instance(Instance _instance) {
		this._instance = _instance;
	}

}
