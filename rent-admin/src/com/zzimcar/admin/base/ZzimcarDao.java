package com.zzimcar.admin.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.zzimcar.admin.config.ZzimcarConstants.PAGING;

public abstract class ZzimcarDao {

	@Autowired
	protected SqlSession sqlSession;
	protected String namespace;

	public static final String SQLNAME_SEPARATOR = "."; 
	public static final String SEARCH_ROWS			= PAGING.RES_KEY_ROWS;
	public static final String SEARCH_TOTAL_COUNT	= PAGING.RES_KEY_TOTAL_COUNT;
	
	public static final String MAPPER_INSERT_BASE	= "insert_base";
	public static final String MAPPER_UPDATE_BY_PK	= "update_by_pk";
	public static final String MAPPER_DELETE_BY_PK	= "delete_by_pk";
	public static final String MAPPER_SELECT_ALL	= "select_all";
	public static final String MAPPER_SELECT_BY_PK	= "select_by_pk";
	public static final String MAPPER_SELECT_FOUND_ROWS	= "select_found_rows";	// SELECT 문에 SQL_CALC_FOUND_ROWS 가 있을 경우 사용가능

	public ZzimcarDao(String namespace) {
        this.namespace = namespace;
    }
	
	protected String getSqlName(String queryId) { 
		return namespace + SQLNAME_SEPARATOR + queryId; 
	} 
	
	/**
	 * 신규 등록(INSERT), Query ID = insert_base
	 */
	public int insert(Map<String, Object> mp) {
		return insert(MAPPER_INSERT_BASE, mp);
	}
	public int insert(String queryId, Map<String, Object> mp) {
		return sqlSession.insert(getSqlName(queryId), mp);
	}

	/**
	 * 데이터 수정(UPDATE), Query ID = update_by_pk
	 */
	public int updateByPk(Map<String, Object> mp) {
		return updateByPk(MAPPER_UPDATE_BY_PK, mp);
	}
	public int updateByPk(String queryId, Map<String, Object> mp) {
		return sqlSession.update(getSqlName(queryId), mp);
	}

	/**
	 * 데이터 삭제(DELETE), Query ID = delete_by_pk
	 */
	public int deleteByPk(Map<String, Object> mp) {
		return deleteByPk(MAPPER_DELETE_BY_PK, mp);
	}
	public int deleteByPk(String queryId, Map<String, Object> mp) {
		return sqlSession.delete(getSqlName(queryId), mp);
	}
	public int deleteByPk(String queryId, List<Map<String, Object>> lsMp) {
		return sqlSession.delete(getSqlName(queryId), lsMp);
	}
	
	
	/**
	 * PK로 SELECT, Query ID = select_by_pk
	 */
	public Map<String, Object> selectByPk(int pid) {
		 return selectByPk(MAPPER_SELECT_BY_PK, pid);
	}
	public Map<String, Object> selectByPk(String queryId, int pid) {
		return sqlSession.selectOne(getSqlName(queryId), pid);
	}
	
	/**
	 * Select One
	 */
	public Map<String, Object> selectOne(String queryId, Map<String, Object> mp) {
		 return sqlSession.selectOne(getSqlName(queryId), mp);
	}
	

	/**
	 * 모든 데이터 SELECT, Query ID = select_all
	 */
	public List<Map<String, Object>> selectAll() {
		return sqlSession.selectList(getSqlName(MAPPER_SELECT_ALL));
	}
	
	public List<Map<String, Object>> selectList(String queryId, Map<String, Object> mp) {
		return sqlSession.selectList(getSqlName(queryId), mp);
	}
	
	public List<Map<String, Object>> selectList(String queryId) {
		return sqlSession.selectList(getSqlName(queryId));
	}

	/**
	 * 페이징이 필요한 검색인 경우사용. 검색 총 갯수를 같이 구한다.
	 */
	public Map<String, Object> search(String queryId, Map<String, Object> mp) {
		Map<String, Object> result = new HashMap<String, Object>();
		int totalCount = 0;
				
		List<Map<String, Object>> rows = sqlSession.selectList(getSqlName(queryId), mp);
		totalCount = sqlSession.selectOne(getSqlName(MAPPER_SELECT_FOUND_ROWS));
		
		result.put(SEARCH_ROWS, rows);
		result.put(SEARCH_TOTAL_COUNT, totalCount);
		
		return result;       
	}

}
