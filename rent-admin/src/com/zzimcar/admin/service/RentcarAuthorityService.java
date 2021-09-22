package com.zzimcar.admin.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.RentcarAuthorityDao;

@Service(value = "RentcarAuthorityService")
public class RentcarAuthorityService extends ZzimcarService{
	
	@Resource(name="RentcarAuthorityDao")
	private RentcarAuthorityDao rentcarauthoritydao;
	
	@Autowired
    public RentcarAuthorityService (RentcarAuthorityDao dao) {
        super(dao);
        this.rentcarauthoritydao = (RentcarAuthorityDao) dao;
    }
	
	public void updateRentcarAuthorityTx(Map<String, Object> mp) throws Exception{
		rentcarauthoritydao.updateRentcarAuthority(mp);
	}
}
