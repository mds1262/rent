package com.carsharing.zzimcar.user.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.carsharing.zzimcar.base.CarsharingZzimcarDao;



@Repository("CarsharingMemberDao")
public class CarsharingMemberDao extends CarsharingZzimcarDao{

	private static String namespace = "CarsharingMemberMapper";
	
	public CarsharingMemberDao() {
		super(namespace);
		// TODO Auto-generated constructor stub
	}


	public List<Map<String, Object>> getAllMemCheck(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		
		return selectList("allMemberInfo", map);		
	}


	
}
