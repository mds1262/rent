package com.zzimcar.admin.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.CommonCodeDao;
import com.zzimcar.admin.dao.PushDao;

@Service(value = "PushService")
public class PushService extends ZzimcarService {
	
	@Resource(name = "PushDao")
	private PushDao pushDao;
	
	@Autowired
    public PushService (PushDao dao)throws Exception {
        super(dao);
        this.pushDao = (PushDao) dao;
    }
	
	public Map<String, Object> getList(Map<String, Object> param) {
		return pushDao.search("select_page", param);
	}
	
	public String getPushNames(Map<String, Object> paMap) {
		String str = "";
		
		try {
			Map<String, Object> result = pushDao.selectOne("select_push_ids", paMap);
			
			str = String.valueOf(result.get("result"));
			str = str.replaceAll("=", "(").replaceAll(",", "), ") + ")";
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return str;
	}

	public List<Map<String, Object>> getMember(Map<String, Object> param) {
		return pushDao.selectList("searchMember", param);
	}
	
	
}