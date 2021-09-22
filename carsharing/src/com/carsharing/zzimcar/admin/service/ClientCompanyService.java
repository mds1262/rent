package com.carsharing.zzimcar.admin.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carsharing.zzimcar.admin.dao.ClientCompanyDao;
import com.carsharing.zzimcar.base.CarsharingZzimcarService;

@Service(value = "ClientCompanyService")
public class ClientCompanyService extends CarsharingZzimcarService{
	
	@Resource(name="ClientCompanyDao")
	private ClientCompanyDao clientCompanyDao;
	
	@Autowired
    public ClientCompanyService (ClientCompanyDao dao) {
        super(dao);
        this.clientCompanyDao = (ClientCompanyDao) dao;
    }
	
	public int deleteCodeTx(Map<String, Object> mp) throws Exception {
    	return clientCompanyDao.updateByPk("update_delete", mp);
	}
}
