package com.zzimcar.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.LoginDao;
import com.zzimcar.admin.dao.MenuauthorityDao;
import com.zzimcar.admin.dao.NmMenuCtrlDao;

@Service(value = "LoginService")
public class LoginService extends ZzimcarService{
	
	@Resource(name="LoginDao")
	private LoginDao loginDao;
	
	@Resource(name="MenuauthorityDao")
	private MenuauthorityDao authorityDao;
	
	@Resource(name="NmMenuCtrlDao")
	private NmMenuCtrlDao menuCtrlDao;
	
    public Map<String, Object> loginAuth(Map<String, Object> mp) throws Exception {
    	return loginDao.loginAuth(mp);
	}
    
    public Map<String, Object> getAuthorityPath(Map<String, Object> map)throws Exception{
    	Map<String, Object> resultMap = new HashMap<>();
    	List<Map<String, Object>> pathPidtMap = menuCtrlDao.getMenuPath(map);
    	if(pathPidtMap.size() > 0) {
    		map.put("menuPid", pathPidtMap.get(0).get("menuPid"));
    		List<Map<String, Object>> resultList = authorityDao.getAuthorityPath(map);
    		if(resultList.size() >0) {
    			resultMap.put("result", "Y");
    			resultMap.put("menuCode",pathPidtMap.get(0).get("menuCode"));
    		}
    		else {
    			resultMap.put("result", "N");
    		}
    	}
    	else {
    		resultMap.put("result", "noUrl");
    	}
    	return resultMap;
    }
    
    public void logout(HttpSession session) {
    	session.invalidate();
    }
}
