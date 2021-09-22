package com.zzimcar.admin.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value="MemberDao")
public class MemberDao extends ZzimcarDao{

	private static final String mapper_namespace = "com.zzimcar.admin.dao.MemberDao";

	public MemberDao() {
		super(mapper_namespace);
	}
	
	public int writeGetCount() throws Exception {
		return sqlSession.selectOne("write_get_count");
	}
	
	public List<Map<String, Object>> MemberListDao(int offset, int max) throws Exception {
		
		HashMap<String, Object> params = new HashMap<String, Object>(); 

		params.put("offset", offset);
		params.put("noOfRecords", max);
		
		return selectList("member_list_excel", params);
	}
}
