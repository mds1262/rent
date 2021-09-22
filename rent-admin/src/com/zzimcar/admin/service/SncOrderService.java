package com.zzimcar.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.SncOrderDao;
import com.zzimcar.admin.utils.PropConst;


@Service
public class SncOrderService extends ZzimcarService{

	@Autowired
	private SncOrderDao sncOrderDao;
	
	public Map<String, Object> sncOrderList(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		return sncOrderDao.sncOrderList(map);
	}

	public Map<String, Object> cancelSncOrderTx(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> resultMap= new HashMap<>();
		try {
			sncOrderDao.cancelSncOrder(map);
			resultMap.put("result", "Y");
			resultMap.put("resultMsg", PropConst.NOMAR_DELETE_SUCCESS);
		}
		catch (Exception e) {
			resultMap.put("result", "N");
			resultMap.put("resultMsg", PropConst.NOMAR_DELETE_FAIL);
			e.printStackTrace();
		}

		return resultMap;
	}

	public List<Map<String, Object>> getSncOptionInfo(Map<String, Object> map)throws Exception {
		// TODO Auto-generated method stub

		return sncOrderDao.getSncOptionInfo(map);
	}

	public Map<String, Object> updateSncOrderStatusTx(Map<String, Object> map) {
		
		Map<String, Object> resultMap= new HashMap<>();
		try {
			sncOrderDao.updateSncOrderStatus(map);
			resultMap.put("result", "Y");
			resultMap.put("resultMsg", "잔행상태가 변경가"+PropConst.WRITE_SUCCESS_UPDATE);
		}
		catch (Exception e) {
			resultMap.put("result", "N");
			resultMap.put("resultMsg","잔행상태가 변경가" + PropConst.WRITE_FAIL_UPDATE);
			e.printStackTrace();
		}

		return resultMap;
		// TODO Auto-generated method stub
		
	}

	public Map<String, Object> UpdatesncOrderMemoTx(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
		Map<String, Object> resultMap= new HashMap<>();
		try {
			sncOrderDao.UpdatesncOrderMemo(map);
			resultMap.put("result", "Y");
			resultMap.put("resultMsg", "메모가"+PropConst.WRITE_SUCCESS_INSERT);
		}
		catch (Exception e) {
			resultMap.put("result", "N");
			resultMap.put("resultMsg","메모가" + PropConst.WRITE_FAIL_INSERT);
			e.printStackTrace();
		}

		return resultMap;

	}

	public List<Map<String, Object>> getSncOrderMemo(Map<String, Object> map) {

		return sncOrderDao.getSncOrderMemo(map);


	
	}

}
