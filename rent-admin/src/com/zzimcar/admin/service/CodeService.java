package com.zzimcar.admin.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.CodeDao;

@Service(value = "CodeService")
public class CodeService extends ZzimcarService{
	
	@Resource(name="CodeDao")
	private CodeDao codeDao;
	
	public int deleteCodeTx(Map<String, Object> mp) throws Exception {
    	return codeDao.updateByPk("update_delete", mp);
	}
	
	@Autowired
    public CodeService (CodeDao dao) {
        super(dao);
        this.codeDao = (CodeDao) dao;
    }
	
	public List<Map<String, Object>> getClassCodeGroup() {
		return	codeDao.getClassCodeGroup();
	}
	
	public List<Map<String, Object>> getOptionCodeGroup() {
		return	codeDao.getOptionCodeGroup();
	}
	
	public List<Map<String, Object>> getMakerCodeGroup() {
		return	codeDao.getMakerCodeGroup();
	}
	
	public List<Map<String, Object>> getFuelCodeGroup() {
		return	codeDao.getFuelCodeGroup();
	}
	
	public List<Map<String, Object>> getYearCodeGroup() {
		return	codeDao.getYearCodeGroup();
	}
	
	public List<Map<String, Object>> getInsuCodeGroup() {
		return	codeDao.getInsuCodeGroup();
	}
	
	public List<Map<String, Object>> getLayoutTypeCodeGroup() {
		return	codeDao.getLayoutTypeCodeGroup();
	}
	
	public List<Map<String, Object>> getSkinTypeCodeGroup() {
		return	codeDao.getSkinTypeCodeGroup();
	}
}
