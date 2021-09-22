package com.carsharing.zzimcar.user.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carsharing.zzimcar.base.CarsharingZzimcarService;
import com.carsharing.zzimcar.user.dao.MyScheduleDao;
import com.carsharing.zzimcar.utils.HMapUtils;

@Service(value = "MyScheduleService")
public class MyScheduleService extends CarsharingZzimcarService {

	@Resource(name = "MyScheduleDao")
	private MyScheduleDao myScheduleDao;
	
    @Autowired
    public MyScheduleService (MyScheduleDao dao) {
        super(dao);
        this.myScheduleDao = (MyScheduleDao) dao;
    }

    public Map<String, Object> cancelScheduleJson( int scsPid, Map<String, Object> member ) {
    	// 취소할 스케줄을 구한다.
    	Map<String, Object> cancelSchedule = myScheduleDao.selectByPk( scsPid );
		if( cancelSchedule == null ) {
			System.out.println("스케줄 취소 실패 [Not Found]: " + scsPid);
			return null;
		}
		
		int memPid = HMapUtils.getIntValue( member, "memPid", 0 );
		int scheduleMemPid = HMapUtils.getIntValue( cancelSchedule, "memPid", 0 );
		if( memPid !=  scheduleMemPid ) {
			System.out.println("스케줄 취소 할수 없음[취소 스케줄 사용자가 아님] : 스케줄주인["+scheduleMemPid+"]" + ", 취소시도자["+memPid+"]");
			return null;
		}
		
		Map<String, Object> updateSchedule = new HashMap<String, Object>();
		updateSchedule.put( "mod_mem_pid", memPid );
		updateSchedule.put( "scs_pid", scsPid );
		int updated = myScheduleDao.updateByPk("cancel_myschedule", updateSchedule);
		if( updated <= 0 ) {
			System.out.println("스케줄 취소 UPdATE 실패 : " + scsPid);
			return null;
		}
		
    	
    	return cancelSchedule;
    	
    }

}
