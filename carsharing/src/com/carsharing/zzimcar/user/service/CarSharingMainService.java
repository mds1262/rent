package com.carsharing.zzimcar.user.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carsharing.zzimcar.user.dao.CarsharingMainDao;



@Service
public class CarSharingMainService {

	@Autowired
	private CarsharingMainDao carSharingMainDao; 

	public List<Map<String, Object>> getMainBoardList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return carSharingMainDao.getMainBoardList(map);
	}

	public Map<String, Object> getChooseCarList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return carSharingMainDao.getChooseCarList(map);
	}
	
	
}
