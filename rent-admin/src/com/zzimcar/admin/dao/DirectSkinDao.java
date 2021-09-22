package com.zzimcar.admin.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value = "DirectSkinDao")
public class DirectSkinDao extends ZzimcarDao {
	private static final String mapper_namespace = "com.zzimcar.admin.dao.DirectSkinDao";

	public DirectSkinDao() {
		super(mapper_namespace);
	}
	
	public int getCountLayoutSetting(int pid) {
		int result = 0;
		Map<String, Object> mp = new HashMap<String, Object>();
		mp.put("rccorp_pid", pid);
		try {
			result = selectList("select_by_pk", mp).size();
		} catch(Exception e) {
			
		}
		return result;
	}
	
	public int insertDefault(Map<String, Object> mp) {
		return insert(mp);
	}
}