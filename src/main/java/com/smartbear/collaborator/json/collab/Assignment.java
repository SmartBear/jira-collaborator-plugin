package com.smartbear.collaborator.json.collab;

public class Assignment {
	
	private String user;
	private String role;
	private String poolGuid;
	
	public Assignment() {
	}
	
	
	public Assignment(String user, String role, String poolGuid) {
		super();
		this.user = user;
		this.role = role;
		this.poolGuid = poolGuid;
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getPoolGuid() {
		return poolGuid;
	}
	public void setPoolGuid(String poolGuid) {
		this.poolGuid = poolGuid;
	}
}
