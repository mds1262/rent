package com.zzimcar.admin.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="RcorderDao")
public class RcorderDao extends ZzimcarDao{

	private static final String mapper_namespace = "com.zzimcar.admin.dao.RcorderDao";

	public RcorderDao() {
		super(mapper_namespace);
	}

	public List<Map<String, Object>> getClassCodeGroup() {
		return selectList("getClassCodeGroup");
	}
	
	public List<Map<String, Object>> getOptionCodeGroup() {
		return selectList("getOptionCodeGroup");
	}
	
	public String requestRcorder(Map<String, Object> mp) {
		updateByPk("update_requestRcorder", mp);
		return sqlSession.selectOne("select_found_reqdtime", mp);
	}
	
	public Map<String, Object> selectRcOrder(Map<String, Object> mp){
		return selectOne("selectRcOrder", mp);
	}
	
	public List<Map<String, Object>> selectOrderMaster(Map<String, Object> mp){
		return selectList("selectOrderMaster",mp);
	}
}
