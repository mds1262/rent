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

@Repository("CouponMasterDao")
public class CouponMasterDao extends ZzimcarDao {

	private static String namespace = "CouponMaster";
	
	public CouponMasterDao() {
		
		super(namespace);
		// TODO Auto-generated constructor stub
	}

	public Map<String, Object> getCouponMasterAll(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
		return	search("getCouponMasterAll",map);
		
	}

	public void insertCouponMaster(Map<String, Object> map) {
		// TODO Auto-generated method stub
		insert("insertCouponMaster", map);
	}

	public List<Map<String, Object>> getCouponMasterView(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getCouponMasterView", map);
	}

	public void updateCouponMaster(Map<String, Object> map) {
		// TODO Auto-generated method stub
		updateByPk("updateCouponMaster", map);
	}

	public void deleteCouponMaster(Map<String, Object> map) {
		// TODO Auto-generated method stub
		deleteByPk("deleteCouponMaster",map);
	}

	public void insertcouponPin(Map<String, Object> map) {
		// TODO Auto-generated method stub
		insert("insertcouponPin",map);
	}

	public List<Map<String, Object>> getCouponKeyCheck() {
		// TODO Auto-generated method stub
		
		return selectList("getCouponKeyCheck");
		
	}

	public void deleteCouponPin(Map<String, Object> map) {
		// TODO Auto-generated method stub
		updateByPk("deleteCouponPin",map);
	}

	public List<Map<String, Object>> getCouponPin(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
		return selectList("getCouponPin",map);
	}

	
}
