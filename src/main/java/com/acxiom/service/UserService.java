package com.acxiom.service;

import com.acxiom.bean.DataRequest;
import com.acxiom.bean.UserRequest;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.DataResponse;
import com.acxiom.response.UserListResponse;
import com.acxiom.response.UserResponse;

public interface UserService {
	public BaseResponseBean register(UserRequest user);

	public UserResponse login(UserRequest fileName);

	public UserListResponse getUsers();

	public BaseResponseBean saveData(DataRequest data);

	public DataResponse getDataByKey(String key);
}
