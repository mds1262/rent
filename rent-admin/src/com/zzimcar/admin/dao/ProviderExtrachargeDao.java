package com.zzimcar.admin.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="ProviderExtrachargeDao")
public class ProviderExtrachargeDao extends ZzimcarDao{
	
	private static final String mapper_namespace = "com.zzimcar.admin.dao.ProviderExtrachargeDao";

	public ProviderExtrachargeDao() {
		super(mapper_namespace);
	}

	public Map<String, Object> selectByProviderPid(int pid) {
		return selectByPk("select_by_corp_pid", pid);
	}
}
