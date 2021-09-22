package com.carsharing.zzimcar.user.dao;

import org.springframework.stereotype.Repository;

import com.carsharing.zzimcar.base.CarsharingZzimcarDao;

@Repository(value="MyScheduleDao")
public class MyScheduleDao extends CarsharingZzimcarDao {
	private static final String mapper_namespace = "com.carsharing.zzimcar.user.dao.MyScheduleDao";
	
	public MyScheduleDao() {
		super(mapper_namespace);
	}

}
