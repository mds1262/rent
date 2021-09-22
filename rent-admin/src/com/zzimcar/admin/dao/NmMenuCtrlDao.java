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

@Repository("NmMenuCtrlDao")
public class NmMenuCtrlDao extends ZzimcarDao {

	private static String namespace = "NmMenuCtrl";
	
	public NmMenuCtrlDao() {
		
		super(namespace);
		// TODO Auto-generated constructor stub
	}

	public Map<String, Object> getNmMenuCtrlList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
		return	search("getNmMenuCtrlList",map);
		
	}

	public void insertNmMenuCtrl(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
		insert("insertNmMenuCtrl", map);
	}

	public List<Map<String, Object>> getNmMenuCtrlView(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getNmMenuCtrlView", map);
	}

	public void updateNmMenuCtrl(Map<String, Object> map) {
		// TODO Auto-generated method stub
		updateByPk("updateNmMenuCtrl", map);
	}

	
	
	public void deleteNmMenuCtrl(Map<String, Object> map) {
		// TODO Auto-generated method stub
		updateByPk("deleteNmMenuCtrl",map);
	}

	public void deleteRoleMenu(Map<String, Object> map) {
		// TODO Auto-generated method stub
		deleteByPk("deleteRoleMenu",map);
	}

	public void insetRollMenu(Map<String, Object> map) {
		// TODO Auto-generated method stub
		insert("insetRollMenu",map);
	}

	public List<Map<String, Object>> getSuperMasterPid(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getSuperMasterPid", map);
	}

	public List<Map<String, Object>> getMenuPath(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getMenuPath", map);
	}

	public List<Map<String, Object>> getSideMenu(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getSideMenu", map);
	}








}
