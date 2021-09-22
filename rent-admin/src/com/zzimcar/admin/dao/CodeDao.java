package com.zzimcar.admin.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="CodeDao")
public class CodeDao extends ZzimcarDao{

	private static final String mapper_namespace = "com.zzimcar.admin.dao.CodeDao";

	public CodeDao() {
		super(mapper_namespace);
	}

	public List<Map<String, Object>> getClassCodeGroup() {
		return selectList("getClassCodeGroup");
	}
	
	public List<Map<String, Object>> getOptionCodeGroup() {
		return selectList("getOptionCodeGroup");
	}
	
	public List<Map<String, Object>> getMakerCodeGroup() {
		return selectList("getMakerCodeGroup");
	}
	
	public List<Map<String, Object>> getFuelCodeGroup() {
		return selectList("getFuelCodeGroup");
	}
	
	public List<Map<String, Object>> getYearCodeGroup() {
		return selectList("getYearCodeGroup");
	}
	
	public List<Map<String, Object>> getInsuCodeGroup() {
		return selectList("getInsuCodeGroup");
	}

	public List<Map<String, Object>> getLayoutTypeCodeGroup() {
		return selectList("getLayoutTypeCodeGroup");
	}

	public List<Map<String, Object>> getSkinTypeCodeGroup() {
		return selectList("getSkinTypeCodeGroup");
	}
}
