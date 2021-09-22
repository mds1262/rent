package com.zzimcar.admin.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.CommonCodeDao;

@Service(value = "CommonCodeService")
public class CommonCodeService extends ZzimcarService {
	
	@Resource(name = "CommonCodeDao")
	private CommonCodeDao codeDao;

    @Autowired
    public CommonCodeService (CommonCodeDao dao)throws Exception {
        super(dao);
        this.codeDao = (CommonCodeDao) dao;
    }

	public List<Map<String, Object>> getModelClassGroup()throws Exception {
		
	return	codeDao.getModelClassGroup();
		
	}

	public List<Map<String, Object>> getModelGearGroup()throws Exception {
		return	codeDao.getModelGearGroup();
	}

	public List<Map<String, Object>> getModelMakerGroup()throws Exception {
		return	codeDao.getModelMakerGroup();
	}

	public List<Map<String, Object>> getModelFuelGroup()throws Exception {
		return	codeDao.getModelFuelGroup();
	}

	public List<Map<String, Object>> getModelCarrierGroup()throws Exception {
		return	codeDao.getModelCarrierGroup();
	}	
	
	public List<Map<String, Object>> getLicenseCodeGroup()throws Exception {
		return	codeDao.getLicenseCodeGroup();
	}

	public List<Map<String, Object>> getCodeAll() throws Exception{
		return codeDao.getCodeAll();
	}

	public List<Map<String, Object>> getRcModelMasterAllInfo() {
		// TODO Auto-generated method stub
		return codeDao.getRcModelMasterAllInfo();
	}

	public List<Map<String, Object>> getBankInfo() {
		// TODO Auto-generated method stub
		return codeDao.getBankInfo();
	}
}
