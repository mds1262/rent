package com.zzimcar.admin.dao;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="BundlingDao")
public class BundlingDao extends ZzimcarDao {
	private static final String mapper_namespace = "com.zzimcar.admin.dao.BundlingDao";

	public BundlingDao() {
		super(mapper_namespace);
	}
}