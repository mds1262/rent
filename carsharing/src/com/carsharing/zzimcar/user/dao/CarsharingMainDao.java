package com.carsharing.zzimcar.user.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.carsharing.zzimcar.base.CarsharingZzimcarDao;



@Repository("CarsharingMainDao")
public class CarsharingMainDao extends CarsharingZzimcarDao{

	private static String namespace = "CarsharingMainMapper";
	
	public CarsharingMainDao() {
		super(namespace);
		// TODO Auto-generated constructor stub
	}
	
	public List<Map<String, Object>> getMainBoardList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getMainBoardList", map);
	}

	public Map<String, Object> getChooseCarList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return search("getChooseCarList", map);
	}
	
}
