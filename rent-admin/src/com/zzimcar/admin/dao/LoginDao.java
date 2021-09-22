package com.zzimcar.admin.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="LoginDao")
public class LoginDao extends ZzimcarDao{

	private static final String mapper_namespace = "com.zzimcar.admin.dao.LoginDao"; 

	public LoginDao() {
		super(mapper_namespace);
	}

	public static final String MAPPER_SELECT_LOGIN_CHECK="Login_Check";

	public Map<String, Object> loginAuth(Map<String, Object> mp) throws Exception{
		Map<String, Object> result = selectOne(MAPPER_SELECT_LOGIN_CHECK, mp);
        return result;
	}

}
