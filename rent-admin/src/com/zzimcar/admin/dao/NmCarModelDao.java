package com.zzimcar.admin.dao;



import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;
import com.zzimcar.admin.erp.vo.NmCarList;



/**
 * 2018.06.07
 * 문득수
 * NmCar차종관리
 * @author BCOM
 *
 */

@Repository("NmCarModelDao")
public class NmCarModelDao extends ZzimcarDao {

	private static String namespace = "nMCarModel";
	
	public NmCarModelDao() {
		
		super(namespace);
		// TODO Auto-generated constructor stub
	}

	public Map<String, Object> getNmCarModelAll(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		
		return	search("getNmCarModelAll",map);
		
	}

	public void insertCarModel(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		insert("insertCarModel", map);
	}

	public List<Map<String, Object>> getCarModelView(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		return selectList("getCarModelView", map);
	}

	public void updateCarModel(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		updateByPk("updateCarModel", map);
	}

	public void deleteNmModel(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		deleteByPk("deleteNmModel",map);
	}

	public List<Map<String, Object>> modelCodeCheck(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		return selectList("modelCodeCheck",map);
	}

	public List<NmCarList> getNmCarLists()throws Exception {
		return sqlSession.selectList("getNmCarLists");
	}
	public void updateRentCarModelMasterCode(Map<String, Object> map) {				
		// TODO Auto-generated method stub			
		updateByPk("updateRentCarModelMasterCode", map);			
	}				
					
	public void updateErpSyscCode(Map<String, Object> map) {				
		// TODO Auto-generated method stub			
		updateByPk("updateErpSyscCode", map);			
	}				


	
}
