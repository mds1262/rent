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

@Repository("PromotionDao")
public class PromotionDao extends ZzimcarDao {

	private static String namespace = "Promotion";
	
	public PromotionDao() {
		
		super(namespace);
		// TODO Auto-generated constructor stub
	}

	public Map<String, Object> getPromotionAll(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
		return	search("getPromotionAll",map);
		
	}

	public void insertPromotion(Map<String, Object> map) {
		// TODO Auto-generated method stub
		insert("insertPromotion", map);
	}

	public List<Map<String, Object>> getPromotionView(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectList("getPromotionView", map);
	}

	public void updatePromotion(Map<String, Object> map) {
		// TODO Auto-generated method stub
		updateByPk("updatePromotion", map);
	}

	public void deletePromotion(Map<String, Object> map) {
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
