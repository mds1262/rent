package com.zzimcar.admin.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="RentcarAuthorityDao")
public class RentcarAuthorityDao extends ZzimcarDao{

	private static final String mapper_namespace = "com.zzimcar.admin.dao.RentcarAuthorityDao";

	public RentcarAuthorityDao() {
		super(mapper_namespace);
	}
	
	public void updateRentcarAuthority(Map<String, Object> mp) {
		updateByPk("update_member_corp", mp);
	}
}
