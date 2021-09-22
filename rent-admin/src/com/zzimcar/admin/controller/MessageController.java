package com.zzimcar.admin.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.COM_COLUMN_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.COM_STATUS_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.service.MessageService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.PropConst;
import com.zzimcar.admin.utils.RequestParameterMapUtils;
import com.zzimcar.admin.config.ZzimcarConstants;

@Controller
public class MessageController extends ZzimcarController{
	
	@Resource(name="MessageService")
	private MessageService messageservice;
	
	@RequestMapping(value="/message/send_message.do")
	public ModelAndView sendMessage(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
        mav.setViewName(VIEW_ROOT_PC+"/message/send_message");
        return mav;
	}
	
	@RequestMapping(value="/message/save_message.do")
	public @ResponseBody Map<String, Object> saveMessage(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> sendMsg = new HashMap<String, Object>();
		try {
			String phoenNums = request.getParameter("phoenNum");
			String[] items = phoenNums.split("/");
			String msgType = request.getParameter("msgType");
			if(msgType.equals("sms")) {
				String insertMsg = "";
				for(int i=0; i<items.length; i++){
//					sendMsg.put("msg_to", items[i]);
//					sendMsg.put("msg_from", ZzimcarConstants.NPRO_MSG_PHONE_NUM);
//					sendMsg.put("msg", param.get("message"));
//					sendMsg.put("msg _type", "4");
//					messageservice.saveMessageTx(sendMsg);
//					sendMsg.clear();
					insertMsg += "('0','"+items[i]+"','"+ZzimcarConstants.NPRO_MSG_PHONE_NUM+"','"+param.get("message")+"','4'),";
				}
				insertMsg = insertMsg.substring(0, insertMsg.length()-1);
				System.out.println("인서트쿼리"+insertMsg);
				sendMsg.put("insertMsg", insertMsg);
				messageservice.saveMessageTx(sendMsg);
				sendMsg.clear();
				
			} else {
				sendMsg.put("mms_subject", param.get("titleText"));
				sendMsg.put("mms_body", param.get("message"));
				messageservice.saveMMS(sendMsg);
				int cont_seq = Integer.valueOf(sendMsg.get("cont_seq").toString());
				String insertMsg = "";
				sendMsg.put("cont_seq", cont_seq);
				for(int i=0; i<items.length; i++){
//					sendMsg.put("msg_to", items[i]);
//					sendMsg.put("msg_from", ZzimcarConstants.NPRO_MSG_PHONE_NUM);
//					sendMsg.put("msg", "MMS");
//					sendMsg.put("msg_type", "6");
//					sendMsg.put("cont_seq", cont_seq);
//					messageservice.saveMessageTx(sendMsg);
//					sendMsg.clear();
					insertMsg += "('0','"+items[i]+"','"+ZzimcarConstants.NPRO_MSG_PHONE_NUM+"','MMS','"+cont_seq+"','6'),";
				}
				insertMsg = insertMsg.substring(0, insertMsg.length()-1);
				sendMsg.put("insertMsg", insertMsg);
				messageservice.saveMessageTx(sendMsg);
				sendMsg.clear();
			}
			
			result.put("chk", "Y");
			result.put("msg", "메시지 전송 완료.");
		} catch(Exception e) {
			result.put("msg", "메시지 전송 도중 실패하였습니다.");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
        return result;
	}
	
	@RequestMapping(value="/message/excelUploadAjax.do")
	public @ResponseBody Map<String, Object> excelUploadAjax(MultipartHttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		MultipartFile excelFile =request.getFile("imgList");
		File destFile = new File(excelFile.getOriginalFilename());
		
		Map<String, Object> fileresult = null;
		// 파일업로드
		String fileName = request.getFile("imgList").getOriginalFilename();
		if (fileName != null && !(fileName.equals(""))) {
			fileresult = messageservice.fileupload(request, "excel");
		}
		System.out.println("업로드 경로"+fileresult.get("filepath0"));
		try {
			String filepath = request.getSession().getServletContext().getRealPath("/resources");
			FileInputStream fis = new FileInputStream(filepath+fileresult.get("filepath0"));
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0); // 해당 엑셀파일의 시트(Sheet) 수
            int rows = sheet.getPhysicalNumberOfRows(); // 해당 시트의 행의 개수
            System.out.println("rows"+rows);
            String excelNum = "";
            for (int rowIndex = 1; rowIndex < rows; rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex); // 각 행을 읽어온다
                if (row != null) {
                    int cells = row.getPhysicalNumberOfCells();
                    XSSFCell cell = row.getCell(3); // 셀에 담겨있는 값을 읽는다.
                    String value = "";
                    if(cell != null) {
	                    switch (cell.getCellType()) { // 각 셀에 담겨있는 데이터의 타입을 체크하고 해당 타입에 맞게 가져온다.
	                    case HSSFCell.CELL_TYPE_NUMERIC:
	                        value = cell.getNumericCellValue() + "";
	                        break;
	                    case HSSFCell.CELL_TYPE_STRING:
	                        value = cell.getStringCellValue() + "";
	                        break;
	                    case HSSFCell.CELL_TYPE_BLANK:
	                        value = cell.getBooleanCellValue() + "";
	                        break;
	                    case HSSFCell.CELL_TYPE_ERROR:
	                        value = cell.getErrorCellValue() + "";
	                        break;
	                    }
	                    excelNum += "/"+value.replaceAll("[^0-9]","");
                    }
                }
            }

			result.put("chk", "Y");
			result.put("num", excelNum);
		} catch(IllegalStateException | IOException e) {
			result.put("msg", "메시지 전송 도중 실패하였습니다.");
			throw new RuntimeException(e.getMessage(),e);
		}
        return result;
	}

}
