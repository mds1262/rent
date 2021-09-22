package com.zzimcar.admin.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository(value="ZzimcarSampleDao")
public class ZzimcarSampleDao {
	
	@Autowired
	SqlSession sqlSession;

	public List<Map<String, Object>> testListDao(int offset, int max) throws Exception {
		HashMap<String, Object> params = new HashMap<String, Object>(); 

		params.put("offset", offset);
		params.put("noOfRecords", max);
		
		return sqlSession.selectList("testQuery", params);
	}

	public void insertTest(Map<String, Object> list) throws Exception {
		sqlSession.insert("insertTest", list);
	}
	
	public int writeGetCount() throws Exception {
		return sqlSession.selectOne("writeGetCount");
	}

}