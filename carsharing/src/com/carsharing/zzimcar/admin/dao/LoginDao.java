package com.carsharing.zzimcar.admin.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.carsharing.zzimcar.base.CarsharingZzimcarDao;;

@Repository(value="LoginDao")
public class LoginDao extends CarsharingZzimcarDao{

	private static final String mapper_namespace = "com.carsharing.zzimcar.admin.dao.LoginDao"; 

	public LoginDao() {
		super(mapper_namespace);
	}

	public static final String MAPPER_SELECT_LOGIN_CHECK="Login_Check";

	public Map<String, Object> loginAuth(Map<String, Object> mp) throws Exception{
		Map<String, Object> result = selectOne(MAPPER_SELECT_LOGIN_CHECK, mp);
        return result;
	}

}
