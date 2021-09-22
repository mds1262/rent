package com.zzimcar.admin.dao;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="BillgateDao")
public class BillgateDao extends ZzimcarDao {
	private static final String mapper_namespace = "com.zzimcar.admin.dao.BillgateDao";

	public BillgateDao() {
		super(mapper_namespace);
	}
}