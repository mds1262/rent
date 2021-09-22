package com.zzimcar.admin.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.MemberDao;

@Service(value = "MemberService")
public class MemberService extends ZzimcarService{

	@Resource(name = "MemberDao")
	private MemberDao memberdao;
	
	@Autowired
	public MemberService (MemberDao dao) {
		super(dao);
		this.memberdao = (MemberDao) dao;
	}
	
	public int writeGetCount() throws Exception {
		return memberdao.writeGetCount();
	}
	
	public List<Map<String, Object>> MemberList(int offset, int max) throws Exception {
		return memberdao.MemberListDao(offset, max);
	}
}
