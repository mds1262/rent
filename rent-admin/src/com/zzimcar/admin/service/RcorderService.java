package com.zzimcar.admin.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.MemberPointDao;
import com.zzimcar.admin.dao.RcorderDao;

@Service(value = "RcorderService")
public class RcorderService extends ZzimcarService{
	
	@Resource(name="RcorderDao")
	private RcorderDao rcorderDao;
	
	@Resource(name = "MemberPointDao")
	private MemberPointDao memberPointDao;
	
	public int deleteRcorderTx(Map<String, Object> mp) throws Exception {
    	return rcorderDao.updateByPk("update_delete", mp);
	}
	
	@Autowired
    public RcorderService (RcorderDao dao) {
        super(dao);
        this.rcorderDao = (RcorderDao) dao;
    }
	
	public List<Map<String, Object>> getClassCodeGroup() {
		return	rcorderDao.getClassCodeGroup();
	}
	
	public List<Map<String, Object>> getOptionCodeGroup() {
		return	rcorderDao.getOptionCodeGroup();
	}
	
	public String requestRcorder(Map<String, Object> mp) {
		return	rcorderDao.requestRcorder(mp);
	}
	
	public Map<String, Object> selectRcOrder(Map<String, Object> mp){
		return rcorderDao.selectRcOrder(mp);
	}
	
	public void rePointTx(Map<String, Object> mp) throws Exception {
		
		// 사용자 point 가져오기
		Map<String, Object> memPoint = rcorderDao.selectOne("selectMemPoint", mp);
		
		int point = Integer.parseInt(memPoint.get("price_dc_point").toString());
		
		mp.put("rePoint", point);
		
		System.out.println("point        =  " + point);
		System.out.println("rcorderPid   =  " + mp.get("rcorderPid"));
		System.out.println("memCouponPid =  " + mp.get("memCouponPid"));
		System.out.println("memPid       =  " + mp.get("memPid"));
		System.out.println("isDirect     =  " + mp.get("isDirect"));
		
		// rentcar_order_master(price_dc_point, mem_coupon_pid, price_dc_coupon - 0으로초기화)
		rcorderDao.updateByPk("updateMemOrder", mp);
		
		// member_coupon Status 복원
		rcorderDao.updateByPk("updateMemCoupon", mp);
		
		// 해당 포인트 복구 member_master
		if(mp.get("isDirect").toString() == "N") {
			rcorderDao.insert("updateMemPoint", mp);
		} else {
			mp.put("memPoint", point);
			memberPointDao.updateByPk("update_direct_point", mp);
		}
		
		// point가 0이 아니면 history
		if(point!=0) {
			rcorderDao.updateByPk("updatePointHistory", mp);
		}
	}

	public void rePointCronTabTx(Map<String, Object> mp) throws Exception {
		
		System.out.println("TODAY @@@@@@@@@  " + mp.get("date"));
		
		// 복구할 주문정보 구하기
		List<Map<String, Object>> result = rcorderDao.selectOrderMaster(mp);
		
		Map<String, Object> pointMp = new HashMap<String, Object>();
		
		if(result.size() > 0){
			
			for(int i=0; i<result.size(); i++) {
				
				pointMp.put("modMemPid", mp.get("modMemPid"));
				pointMp.put("rcorderPid", result.get(i).get("rcorderPid"));
				pointMp.put("memCouponPid", result.get(i).get("memCouponPid"));
				pointMp.put("rePoint", result.get(i).get("priceDcPoint"));
				pointMp.put("memPid", result.get(i).get("memPid"));
				
				// rentcar_order_master(price_dc_point, mem_coupon_pid, price_dc_coupon - 0으로초기화)
				rcorderDao.updateByPk("updateMemOrder", mp);
				
				// member_coupon Status 복원
				rcorderDao.updateByPk("updateMemCoupon", mp);
				
				// 해당 포인트 복구 member_master
				rcorderDao.insert("updateMemPoint", mp);
				
				// point가 0이 아니면 history
				if(Integer.parseInt(pointMp.get("rePoint").toString())!=0) {
					rcorderDao.updateByPk("updatePointHistory", mp);
				}
			}
			
			
		}	
	}
}
