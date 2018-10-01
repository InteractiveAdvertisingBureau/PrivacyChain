package com.acxiom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.acxiom.bean.UserRequest;
import com.acxiom.response.BaseResponseBean;
import com.acxiom.response.UserListResponse;
import com.acxiom.response.UserResponse;
import com.acxiom.service.UserService;
import com.acxiom.utils.Utils;

@RestController
@RequestMapping("/user")
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	@Autowired
	UserService userService;

	@RequestMapping(value = "register", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseBean register(@RequestBody UserRequest user) {
		log.info("[register]" + Utils.objectToJson(user));
		return userService.register(user);
	}

	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
	public UserResponse login(@RequestBody UserRequest user) {
		log.info("[login]" + Utils.objectToJson(user));
		return userService.login(user);
	}

	@RequestMapping(value = "list", method = RequestMethod.GET)
	@ResponseBody
	public UserListResponse getUsers() {
		log.info("[getUsers]");
		return userService.getUsers();
	}

}
