package com.zzimcar.admin.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value = "PushDao")
public class PushDao extends ZzimcarDao {
	private static final String mapper_namespace = "com.zzimcar.admin.dao.PushDao";

	public PushDao() {
		super(mapper_namespace);
	}

	public ArrayList<String> searchMemberTokensAndroid(String ids) {
		if(ids !=null) {
			ArrayList<String> result = new ArrayList<>();
			Map<String, Object> mp = new HashMap<String, Object>();
			mp.put("mem_pids", ids);
			
			List<Map<String,Object>> tokenList = selectList("searchMemberTokensAndroid", mp);
			for(Map<String, Object> map : tokenList) {
				if(map.get("pushId") != null && !map.get("pushId").equals("")) {
					result.add(String.valueOf(map.get("pushId")));
				}
			}
			return result;
		}
		return null;
	}

	public ArrayList<String> searchMemberTokensIOS(String ids) {
		if(ids !=null) {
			ArrayList<String> result = new ArrayList<>();
			Map<String, Object> mp = new HashMap<String, Object>();
			mp.put("mem_pids", ids);
			
			List<Map<String,Object>> tokenList = selectList("searchMemberTokensIOS", mp);
			for(Map<String, Object> map : tokenList) {
				if(map.get("pushId") != null && !map.get("pushId").equals("")) {
					result.add(String.valueOf(map.get("pushId")));
				}
			}
			return result;
		}
		return null;
	}
}