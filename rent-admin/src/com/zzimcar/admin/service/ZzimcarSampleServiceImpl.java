package com.zzimcar.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zzimcar.admin.dao.ZzimcarSampleDao;

@Service(value = "ZzimcarSampleService")
public class ZzimcarSampleServiceImpl implements ZzimcarSampleService {

	@Resource(name = "ZzimcarSampleDao")
	ZzimcarSampleDao zzimcarSampleDao;

	@Override
	public List<Map<String, Object>> testList(int offset, int max)
			throws Exception {
		// TODO Auto-generated method stub
		return zzimcarSampleDao.testListDao(offset, max);
	}

	@Override
	public void insertTestTx() throws Exception {
		// TODO Auto-generated method stub
		// Transaction test
		Map<String, Object> list = new HashMap<String, Object>();

		list.put("id", 52);
		list.put("name", "TEST");

		zzimcarSampleDao.insertTest(list);

		list.put("id", 52);
		list.put("name", "TEST");

		zzimcarSampleDao.insertTest(list);
	}

	@Override
	public int writeGetCount() throws Exception {
		return zzimcarSampleDao.writeGetCount();
	}

}
