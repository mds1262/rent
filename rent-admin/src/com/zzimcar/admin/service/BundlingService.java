package com.zzimcar.admin.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.BundlingDao;

@Service(value = "BundlingService")
public class BundlingService extends ZzimcarService {
	
	@Resource(name="BundlingDao")
	private BundlingDao bundlingDao;
	
	@Autowired
    public BundlingService (BundlingDao dao) {
        super(dao);
        this.bundlingDao = (BundlingDao) dao;
    }
}
