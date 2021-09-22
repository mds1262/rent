package com.carsharing.zzimcar.user.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carsharing.zzimcar.user.dao.CarsharingMainDao;
import com.carsharing.zzimcar.user.dao.CarsharingMemberDao;



@Service
public class CarSharingMemberService {

	@Autowired
	private CarsharingMemberDao carSharingMainDao; 
	
	public List<Map<String, Object>> getLoginCheck(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		List<Map<String, Object>> resultMap = new ArrayList<>();	
		//초기 아이디 체크
		
		List<Map<String, Object>> memIdCheck = carSharingMainDao.getAllMemCheck(map);
		
		if(memIdCheck.isEmpty()) {
			
			resultMap.get(0).put("IDCheck", "NOID");
		}
		else {
			map.put("pwCheckUse", "OK");
			resultMap =	carSharingMainDao.getAllMemCheck(map);
		}
		return resultMap;
	}

}
