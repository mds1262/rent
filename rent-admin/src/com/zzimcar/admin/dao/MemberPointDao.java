package com.zzimcar.admin.dao;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="MemberPointDao")
public class MemberPointDao extends ZzimcarDao {

	private static final String mapper_namespace = "com.zzimcar.user.dao.MemberPointDao";

	public MemberPointDao() {
		super(mapper_namespace);
	}
	
}
