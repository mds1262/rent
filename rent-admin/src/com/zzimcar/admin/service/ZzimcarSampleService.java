package com.zzimcar.admin.service;

import java.util.List;
import java.util.Map;

public interface ZzimcarSampleService {

	public List<Map<String, Object>> testList(int offset, int max)throws Exception;

	public void insertTestTx() throws Exception;

	public int writeGetCount() throws Exception;
}
