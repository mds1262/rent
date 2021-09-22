package com.zzimcar.admin.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.DirectSkinDao;

@Service(value = "DirectSkinService")
public class DirectSkinService extends ZzimcarService {

	@Resource(name = "DirectSkinDao")
	private DirectSkinDao directSkinDao;

    @Autowired
    public DirectSkinService (DirectSkinDao dao)throws Exception {
        super(dao);
        this.directSkinDao = (DirectSkinDao) dao;
    }

	public Map<String, Object> getSkinList(Map<String, Object> param) {
		return directSkinDao.search("select_page", param);
	}
}
