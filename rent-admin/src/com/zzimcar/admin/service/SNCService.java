package com.zzimcar.admin.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.SNCDao;

@Service(value = "SNCService")
public class SNCService extends ZzimcarService{
	@Resource(name = "SNCDao")
	private SNCDao sncDao;
	
	@Autowired
	public SNCService (SNCDao dao) {
		super(dao);
		this.sncDao = (SNCDao) dao;
	}
	
	/**
	 * 2018.09.06 김준태 공동구매 제조사 관리
	 */
	public int saveMaker(Map<String, Object> map) throws Exception {
		return sncDao.insert("insert_maker", map);
	}
	
	public int deleteMaker(Map<String, Object> map) throws Exception {
		return sncDao.updateByPk("delete_maker_by_pk", map);
	}
	
	/**
	 * 2018.09.06 김준태 공동구매 차종 등록 관리
	 */
	public int saveSncModel(Map<String, Object> map) throws Exception {
		return sncDao.insert("insert_sncModel", map);
	}
	
	public int deleteSncModel(Map<String, Object> map) throws Exception {
		return sncDao.updateByPk("delete_sncModel_by_pk", map);
	}
	
	/**
	 * 2018.09.06 김준태 공동구매 트림 등록 관리
	 */
	public int saveSncTrim(Map<String, Object> map) throws Exception {
		return sncDao.insert("insert_sncTrim", map);
	}
	
	public int deleteSncTrim(Map<String, Object> map) throws Exception {
		return sncDao.updateByPk("delete_sncTrim_by_pk", map);
	}
	
	/**
	 * 2018.09.06 김준태 공동구매 컬러,옵션 등록 관리
	 */
	public int saveSncOption(Map<String, Object> map) throws Exception {
		return sncDao.insert("insert_sncOption", map);
	}
	
	public int deleteSncOption(Map<String, Object> map) throws Exception {
		return sncDao.updateByPk("delete_sncOption_by_pk", map);
	}
	public int updateCarColor(Map<String, Object> map) throws Exception {
		return sncDao.updateByPk("update_carcolor_by_pk", map);
	}
	public int insertMultipleTx(Map<String, Object> map) throws Exception {
		return sncDao.insert("insert_sncOption", map);
	}
}