package com.zzimcar.admin.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="MessageDao")
public class MessageDao extends ZzimcarDao{

	private static final String mapper_namespace = "com.zzimcar.admin.dao.MessageDao";

	public MessageDao() {
		super(mapper_namespace);
	}

}
