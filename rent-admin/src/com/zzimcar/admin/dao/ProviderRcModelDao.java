package com.zzimcar.admin.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.zzimcar.admin.base.ZzimcarDao;

@Repository(value = "ProviderRcModelDao")
public class ProviderRcModelDao extends ZzimcarDao{
	private static final String mapper_namespace = "com.zzimcar.admin.dao.ProviderRcModelDao";

	public ProviderRcModelDao() {
		super(mapper_namespace);
	}

	public int updateStatus(Map<String, Object> hashMap) throws Exception {
		return updateByPk("update_status", hashMap);
	}
	
	public int insertOption(Map<String, Object> hashMap) throws Exception{
		return insert("insert_rcmodel_option_by_pk", hashMap);
	}
	
	public int deleteOption(String pid) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rcmodel_pid", pid);
		return deleteByPk("delete_rcmodel_option_by_pk", map);
	}
	
	public void deleteAfterInsert(List<Map<String,Object>> list) throws Exception {
		if(list.size() > 0) {
			String pid = String.valueOf(list.get(0).get("rcmodel_pid"));
			deleteOption(pid);
			
			for(int i=0; i<list.size(); i++) {
				insertOption(list.get(i));
			}
		}
	}
}
