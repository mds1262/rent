package com.zzimcar.admin.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="CommonCodeDao")
public class CommonCodeDao extends ZzimcarDao {
	private static final String mapper_namespace = "com.zzimcar.user.dao.CommonCodeDao"; 

	public CommonCodeDao() {
		super(mapper_namespace);
	}

	public List<Map<String, Object>> getModelClassGroup() {
		return selectList("getModelCodeGroup");
	}

	public List<Map<String, Object>> getModelGearGroup() {
		return selectList("getModelGearGroup");
	}

	public List<Map<String, Object>> getModelMakerGroup() {
		return selectList("getModelMakerGroup");
	}

	public List<Map<String, Object>> getModelFuelGroup() {
		return selectList("getModelFuelGroup");
	}

	public List<Map<String, Object>> getModelCarrierGroup() {
		return selectList("getModelCarrierGroup");
	}

	public List<Map<String, Object>> getLicenseCodeGroup() {
		return selectList("getLicenseCodeGroup");
	}

	public List<Map<String, Object>> getCodeAll() {
		return selectList(MAPPER_SELECT_ALL);
	}

	public List<Map<String, Object>> getRcModelMasterAllInfo() {
		// TODO Auto-generated method stub
		return selectList("getRcModelMasterAllInfo");
	}

	public List<Map<String, Object>> getBankInfo() {
		// TODO Auto-generated method stub
		return selectList("getBankInfo");
	}
}
