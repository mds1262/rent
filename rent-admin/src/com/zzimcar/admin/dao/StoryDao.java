package com.zzimcar.admin.dao;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="StoryDao")
public class StoryDao extends ZzimcarDao{
	
	private static final String mapper_namespace = "com.zzimcar.admin.dao.StoryDao";

	public StoryDao() {
		super(mapper_namespace);
	}
}
