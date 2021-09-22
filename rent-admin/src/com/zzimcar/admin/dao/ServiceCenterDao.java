package com.zzimcar.admin.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="ServiceCenterDao")
public class ServiceCenterDao extends ZzimcarDao {
	private static final String mapper_namespace = "com.zzimcar.admin.dao.ServiceCenterDao";

	public ServiceCenterDao() {
		super(mapper_namespace);
	}

	public int deleteBoardItem(Map<String, Object> hashMap) {
		return updateByPk("deleteBoardItem", hashMap);
	}
}
