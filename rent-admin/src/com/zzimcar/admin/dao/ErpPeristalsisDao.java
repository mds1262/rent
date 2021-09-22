package com.zzimcar.admin.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="ErpPeristalsisDao")
public class ErpPeristalsisDao extends ZzimcarDao {
	
	private static final String mapper_namespace = "com.zzimcar.admin.dao.ErpPeristalsisDao";

	public ErpPeristalsisDao() {
		super(mapper_namespace);
	}
	
	public Map<String, Object> erpMasterList(Map<String, Object> mp) {
		return selectOne("erp_api_list", mp);
	}
	
	public int inserterpMasterLog(Map<String, Object> mp) {
		return insert("erp_api_log", mp);
	}
	
	public List<Map<String, Object>> rentCarCorpList(Map<String, Object> mp) {
		return selectList("rentcar_corp_list", mp);
	}
	
	public int insertCorp(Map<String, Object> mp) {
		insert("insertProviderCorp",mp);
		return insert("insertRentCarCorp",mp);
	}
	
	public List<Map<String, Object>> selectNmCarModel(Map<String, Object> mp) {
		return selectList("selectNmCarModel", mp);
	}
	
	public List<Map<String, Object>> selectRentCarModel(Map<String, Object> mp) {
		return selectList("selectRentCarModel", mp);
	}
	
	public List<Map<String, Object>> selectRentCarModelJeju(Map<String, Object> mp) {
		return selectList("selectRentCarModelJeju", mp);
	}
	
	public List<Map<String, Object>> selecterpSyncList(Map<String, Object> mp){
		return selectList("selecterpSyncList", mp);
	}
	
	public Map<String, Object> selectRcCorpList(Map<String, Object> mp){
		return selectOne("selectRcCorpList", mp);
	}
	
	public int insertRentcarModel(Map<String, Object> mp) {
		return insert("insertRentcarModel", mp);
	}
	
	public int insertRcModelOption(Map<String, Object> mp) {
		return insert("insertRcModelOption", mp);
	}
	
	public Map<String, Object> selectErpSyncCode(Map<String, Object> mp) {
		return selectOne("selectErpSyncCode", mp);
	}
	
	public List<Map<String, Object>> selectAllErpSyncCode(Map<String, Object> mp) {
		return selectList("selectAllErpSyncCode", mp);
	}
	
	public int insertErpSyncCode(Map<String, Object> mp) {
		return insert("insertErpSyncCode", mp);
	}
	
	public List<Map<String, Object>> selectNmcarModelList() {
		return selectList("selectNmcarModelList");
	}
	
	public void updateErpSync(Map<String, Object> mp) {
		updateByPk("updateErpSync", mp);
	}
	
}
