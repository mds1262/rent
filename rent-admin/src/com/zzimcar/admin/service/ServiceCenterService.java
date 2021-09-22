package com.zzimcar.admin.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.ServiceCenterDao;

@Service(value = "ServiceCenterService")
public class ServiceCenterService extends ZzimcarService{

	@Resource(name="ServiceCenterDao")
	private ServiceCenterDao serviceCenterDao;
	
	@Autowired
    public ServiceCenterService (ServiceCenterDao dao) {
        super(dao);
        this.serviceCenterDao = (ServiceCenterDao) dao;
    }
	
	public Map<String, Object> getList(Map<String, Object> param) {
		return serviceCenterDao.search("selectNoticeList", param);
	}

	public void insertBoardItemTx(Map<String, Object> param) throws Exception {
		serviceCenterDao.insert(param);
	}

	public void updateBoardItemTx(Map<String, Object> param) throws Exception {
		serviceCenterDao.updateByPk(param);
	}

	public void deleteBoardTx(Map<String, Object> param) {
		Object checkitems = param.get("myCheck");
		Map<String, Object> hashMap = new HashMap<String, Object>();
		String[] deleteItem = new String[1];

		if (checkitems instanceof String[]) {
			deleteItem = (String[]) checkitems;
		} else {
			deleteItem[0] = (String) checkitems;
		}

		for (int i = 0; i < deleteItem.length; i++) {
			hashMap.clear();
			hashMap.put("bbs_pid", deleteItem[i]);
			hashMap.put("mod_mem_pid", param.get("mod_mem_pid"));
			
			serviceCenterDao.deleteBoardItem(hashMap);
		}
	}
}