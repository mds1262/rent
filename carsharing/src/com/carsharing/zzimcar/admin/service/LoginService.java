package com.carsharing.zzimcar.admin.service;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.carsharing.zzimcar.admin.dao.LoginDao;
import com.carsharing.zzimcar.base.CarsharingZzimcarService;

@Service(value = "LoginService")
public class LoginService extends CarsharingZzimcarService{
	
	@Resource(name="LoginDao")
	private LoginDao loginDao;
	
    public Map<String, Object> loginAuth(Map<String, Object> mp) throws Exception {
    	return loginDao.loginAuth(mp);
	}
    
    public void logout(HttpSession session) {
    	session.invalidate();
    }
}