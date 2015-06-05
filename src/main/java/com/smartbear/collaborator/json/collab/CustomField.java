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
package com.smartbear.collaborator.json.collab;

import java.util.Set;

public class CustomField {
	private String name;
	private Set<String> value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	public Set<String> getValue() {
		return value;
	}
	public void setValue(Set<String> value) {
		this.value = value;
	}
	public CustomField() {
		
	}
	
	public CustomField(String name,Set<String> value) {
		this.name = name;
		this.value = value;
	}
	
	
}
