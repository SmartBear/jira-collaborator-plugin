package com.smartbear.collaborator.json.fisheye;

import com.smartbear.collaborator.json.collab.ScmToken;

public class Repository {
	private String type;
	private ScmToken scmToken;
	private String name;
	private String path;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public ScmToken getScmToken() {
		return scmToken;
	}
	public void setScmToken(ScmToken scmToken) {
		this.scmToken = scmToken;
	}
}
