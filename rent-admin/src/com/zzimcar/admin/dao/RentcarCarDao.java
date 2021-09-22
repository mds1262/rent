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

@Repository("RentcarCarDao")
public class RentcarCarDao extends ZzimcarDao {

	private static String namespace = "RentcarCar";
	
	public RentcarCarDao() {
		
		super(namespace);
		// TODO Auto-generated constructor stub
	}

	public Map<String, Object> getRentcarCar(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
		return	search("getRentcarCar",map);
		
	}

	public void insertRentcarCar(Map<String, Object> map) {
		// TODO Auto-generated method stub
		insert("insertRentcarCar", map);
	}

	public Map<String, Object> getRentCarcarView(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectOne("getRentCarcarView", map);
	}

	public void updateRentcarCar(Map<String, Object> map) {
		// TODO Auto-generated method stub
		updateByPk("updateRentcarCar", map);
	}

	public void deleteRentcarCar(Map<String, Object> map) {
		// TODO Auto-generated method stub
		deleteByPk("deletePromotion",map);
	}

	public List<Map<String, Object>> getRcModelSearch(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getRcModelSearch",map);
	}

	public Map<String, Object> getReviewList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return search("getReviewList",map);
	}

	public List<Map<String, Object>> getRcModelPid(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getRcModelPid", map);
	}


}
