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

@Repository("MenuauthorityDao")
public class MenuauthorityDao extends ZzimcarDao {

	private static String namespace = "menuauthority";
	
	public MenuauthorityDao() {
		
		super(namespace);
		// TODO Auto-generated constructor stub
	}

	public Map<String, Object> getAuthorityMember(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		
		return	search("getAuthorityMember",map);
		
	}

	public void insertmenuAuthority(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		insert("insertmenuAuthority", map);
	}

	public List<Map<String, Object>> getMenuAuthorityView(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		return selectList("getMenuAuthorityView", map);
	}

	public void updateMenuAuthority(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		insert("updatemenuAuthority", map);
	}

	
	
	public void deletemenuAuthority(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		updateByPk("deletemenuAuthority",map);
	}

	public void deleteMemberAuthority(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		updateByPk("deleteMemberAuthority",map);
	}

	public void updateMemberAuthority(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		
		updateByPk("updateMemberAuthority",map);
		
	}

	public List<Map<String, Object>> getMemberAuthorityView(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
		return selectList("getMemberAuthorityView", map);
	}

	public List<Map<String, Object>> checkRoleMenu(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
		return selectList("checkRoleMenu", map);
	}

	public void updateRoleUseChange(Map<String, Object> map) {
		// TODO Auto-generated method stub
		updateByPk("updateRoleUseChange",map);
	}

	public List<Map<String, Object>> getCheckRoleMenu(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getCheckRoleMenu", map);
	}

	public void deleteAuthority(Map<String, Object> map) {
		// TODO Auto-generated method stub
		deleteByPk("deleteAuthority",map);
	}

	public List<Map<String, Object>> getAuthorityPath(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getAuthorityPath",map);
	}








}
