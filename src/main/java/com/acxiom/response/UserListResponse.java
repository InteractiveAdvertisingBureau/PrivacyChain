package com.acxiom.response;

import java.util.List;
import java.util.Map;

public class UserListResponse extends BaseResponseBean {

	private List<Map<String, Object>> users;

	public List<Map<String, Object>> getUsers() {
		return users;
	}

	public void setUsers(List<Map<String, Object>> users) {
		this.users = users;
	}

}
