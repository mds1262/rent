package com.carsharing.zzimcar.base;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class CarsharingZzimcarService {
	
	protected final Logger logger = LoggerFactory.getLogger(CarsharingZzimcarService.class);

	private CarsharingZzimcarDao dao;
	

	public CarsharingZzimcarService() {}
	
    public CarsharingZzimcarService(CarsharingZzimcarDao dao) {
        this.dao = dao;
    }
    
	public void insert(Map<String, Object> entity) {
		dao.insert(entity);
	}
	public void insert(String queryId, Map<String, Object> entity) {
		dao.insert(queryId, entity);
	}

	public void update(Map<String, Object> entity) {
		dao.updateByPk(entity);
	}
	public void update(String queryId, Map<String, Object> entity) {
		dao.updateByPk(queryId, entity);
	}

	public void delete(Map<String, Object> entity) {
		dao.deleteByPk(entity);
	}
	public void delete(String queryId, Map<String, Object> entity) {
		dao.deleteByPk(queryId, entity);
	}

	public Map<String, Object> selectByPk(int pid) {
		return dao.selectByPk(pid);
	}
	public Map<String, Object> selectByPk(String queryId, int pid) {
		return dao.selectByPk(queryId, pid);
	}

	public List<Map<String, Object>> selectAll() {
		return dao.selectAll();
	}

	public Map<String, Object> selectOne(String queryId, Map<String, Object> mp) {
		return dao.selectOne(queryId, mp);
	}
	
	public Map<String, Object> search(String queryId, Map<String, Object> mp) {
		return dao.search(queryId, mp);
	}
	
	public List<Map<String, Object>> selectList(String queryId, Map<String, Object> mp) {
		return dao.selectList(queryId, mp);
	}
	
}
