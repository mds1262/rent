package com.zzimcar.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zzimcar.admin.erp.vo.NmCarList;
import com.zzimcar.admin.erp.vo.NmCarLists;
import com.zzimcar.admin.service.NmCarModelService;

@Controller
public class NmCarListXmlContorller {
	
	@Autowired
	private NmCarModelService nmCarModelService;
	
	// REBON API 차량맵핑을 위한 차량 리스트 XML
	@RequestMapping(value="nmCarListXml")
	public @ResponseBody NmCarLists getNmCarDataList() throws Exception {
		
		List<NmCarList> data = nmCarModelService.getNmCarLists(); 
		
		NmCarLists result = new NmCarLists(); 
		result.setNmCarLists(data);
		
		return result;

	}
}
