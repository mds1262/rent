package com.zzimcar.admin.dao;



import java.util.List;
import java.util.Map;



import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;



/**
 * 2018.06.07
 * 문득수
 * NmCar차종관리
 * @author BCOM
 *
 */

@Repository("ProViderExCludeDao")
public class ProViderExCludeDao extends ZzimcarDao {

	private static String namespace = "PVExculde";
	
	public ProViderExCludeDao() {
		
		super(namespace);
		// TODO Auto-generated constructor stub
	}

	public Map<String, Object> getPVExculdeAll(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
		return	search("getPVExculdeAll",map);
		
	}

	public void insertProExclude(Map<String, Object> map) {
		// TODO Auto-generated method stub
		insert("insertProExclude", map);
	}

	public List<Map<String, Object>> getProExcludeView(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getProExcludeView", map);
	}

	public List<Map<String, Object>> getRcModelNameModelGroup(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getRcModelNameModelGroup",map);
	}

	public List<Map<String, Object>> getSubExcludeModelPid(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getSubExcludeModelPid", map);
	}

	public void updateProExclude(Map<String, Object> map) {
		// TODO Auto-generated method stub
		updateByPk("updateProExclude", map);
	}

	
	
	public void deleteNmModel(Map<String, Object> map) {
		// TODO Auto-generated method stub
		deleteByPk("deleteNmModel",map);
	}








}
