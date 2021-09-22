package com.zzimcar.admin.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository("GroupPurchasesDao")
public class SncOrderDao extends ZzimcarDao{

	private static final String namespace ="SNCORDER";
	
	public SncOrderDao() {
		super(namespace);
		// TODO Auto-generated constructor stub
	}

	public Map<String, Object> sncOrderList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return search("sncOrderList", map);
	}

	public void cancelSncOrder(Map<String, Object> map) {
		// TODO Auto-generated method stub
		updateByPk("cancelSncOrder",map);
	}

	public List<Map<String, Object>> getSncOptionInfo(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getSncOptionInfo",map);
	}

	public void updateSncOrderStatus(Map<String, Object> map) {
		// TODO Auto-generated method stub
		updateByPk("updateSncOrderStatus",map);
	}

	public void UpdatesncOrderMemo(Map<String, Object> map) {
		// TODO Auto-generated method stub
		updateByPk("UpdatesncOrderMemo",map);
	}

	public List<Map<String, Object>> getSncOrderMemo(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getSncOrderMemo", map);
	}
	
}
