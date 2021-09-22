package com.zzimcar.admin.dao;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="SNCDao")
public class SNCDao extends ZzimcarDao{
	private static final String mapper_namespace = "com.zzimcar.admin.dao.SNCDao";
	
	public SNCDao() {
		super(mapper_namespace);
	}
}
