package com.zzimcar.admin.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.MessageDao;

@Service(value = "MessageService")
public class MessageService extends ZzimcarService{
	
	@Resource(name="MessageDao")
	private MessageDao messageDao;
	
	public int saveMessageTx(Map<String, Object> mp) throws Exception {
    	return messageDao.insert("insert_npro_msg", mp);
	}
	
	public int saveMMS(Map<String, Object> mp) throws Exception {
    	return messageDao.insert("insert_npro_mms_msg", mp);
	}
	@Autowired
    public MessageService (MessageDao dao) {
        super(dao);
        this.messageDao = (MessageDao) dao;
    }
	
	
}
