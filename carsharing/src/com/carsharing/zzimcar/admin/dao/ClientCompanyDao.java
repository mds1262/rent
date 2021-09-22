package com.carsharing.zzimcar.admin.dao;

import org.springframework.stereotype.Repository;

import com.carsharing.zzimcar.base.CarsharingZzimcarDao;

@Repository(value="ClientCompanyDao")
public class ClientCompanyDao extends CarsharingZzimcarDao{
	
	private static final String mapper_namespace = "com.carsharing.zzimcar.admin.dao.ClientCompanyDao";
	
	public ClientCompanyDao() {
		super(mapper_namespace);
	}
}
