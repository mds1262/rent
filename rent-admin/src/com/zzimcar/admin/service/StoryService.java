package com.zzimcar.admin.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.StoryDao;

@Service(value="StoryService")
public class StoryService extends ZzimcarService{
	
	@Resource(name="StoryDao")
	private StoryDao storyDao;
	
	@Autowired
	public StoryService (StoryDao dao) {
		super(dao);
		this.storyDao = (StoryDao) dao;
	}
}
