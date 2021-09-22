package com.zzimcar.admin.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value = "ProviderCompanyDao")
public class ProviderCompanyDao extends ZzimcarDao {
	private static final String mapper_namespace = "com.zzimcar.admin.dao.ProviderCompanyDao";

	public ProviderCompanyDao() {
		super(mapper_namespace);
	}

	public int updateStatusProviderCorp(Map<String, Object> mp) throws Exception{
		return updateByPk("update_status_provider_corp", mp);
	}
	
	public int updateStatusRentcarCorp(Map<String, Object> mp) throws Exception{
		return updateByPk("update_status_rentcar_corp", mp);
	}
	
	public int updateStatusExtracharge(Map<String, Object> mp) throws Exception{
		return updateByPk("update_status_extracharge", mp);
	}
	
	public int updateStatusRentcarMaster(Map<String, Object> mp) {
		return updateByPk("update_status_rentcar_master", mp);
	}

	public int select_code_count(Map<String, Object> mp) {
		return selectList("select_code_count", mp).size();
	}
	
	// 정산리스트조회
	public List<Map<String, Object>> selectSettlementOrderList(Map<String, Object> mp) throws Exception{
		return selectList("selectSettlementOrderList", mp);
	}
	
	// 정산리스트 Excel
	public List<Map<String, Object>> selectSettlementListExcel(Map<String, Object> mp){
		return selectList("selectSettlementListExcel", mp);
	}
	
	// 정산상세리스트 Excel
	public List<Map<String, Object>> selectSettlementDetailListExcel(Map<String, Object> mp){
		return selectList("selectSettlementDetailListExcel", mp);
	}
	
	// 정산 인보이스 리스트
	public List<Map<String, Object>> selectSettleInVoceList(Map<String, Object> mp){
		return selectList("selectSettleInVoceList", mp);
	}
	
	// 정산 INSERT
	public void insertSettle(Map<String, Object> mp) throws Exception{
		insert("insertSettle", mp);
	}
	
	// 정산 완료처리
	public void updateSettlement(Map<String, Object> mp) throws Exception{
		updateByPk("updateSettlement", mp);
	}

	public Map<String, Object> getSettlePriceWaitInfo(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return search("getSettlePriceWaitInfo", map);
	}

	public List<Map<String, Object>> getSettlePriceInfo(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getSettlePriceInfo", map);
	}

	public Map<String, Object> getSettlePriceWaitDetailInfo(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return search("getSettlePriceWaitDetailInfo", map);
	}
	
	public int getDomainCheck(String domain, int rccorp_pid) {
		int result = 0;
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("dir_domain", domain);
			map.put("rccorp_pid", rccorp_pid);
			
			result = selectList("getDomainCheck", map).size();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			result = 1;
		}
		
		return result;		
	}

	public void updateSettlementBankInfo(Map<String, Object> param) {
		updateByPk("updateSettlementBankInfo", param);
	}
}