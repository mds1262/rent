package com.zzimcar.admin.utils;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelView extends AbstractExcelPOIView {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String target = URLEncoder.encode(model.get("fileName").toString(), "UTF-8");

		response.setHeader("Content-disposition", "attachment; filename=" + target + ".xlsx");

		List<String> excelTitle = (List<String>) model.get("excelTitle");
		List<String> excelTitleName = (List<String>) model.get("excelTitleName");
		List<Map<String, Object>> excelList = (List<Map<String, Object>>) model.get("excelList");

		Sheet sheet = workbook.createSheet(model.get("sheetName").toString());
		Row row = null;
		int rowCount = 0;
		int cellCount = 0;

		row = sheet.createRow(rowCount++);
		for (int i = 0; i < excelTitleName.size(); i++) {
			row.createCell(cellCount++).setCellValue(excelTitleName.get(i));
		}
		
		for (int i = 0; i < excelList.size(); i++) {
			
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			for (int count = 0; count < excelTitle.size(); count++) {
				row.createCell(cellCount++).setCellValue(excelList.get(i).get(excelTitle.get(count)).toString());
			}
		}

		for (int i = 0; i < excelTitleName.size(); i++) {
			sheet.autoSizeColumn(i);
			// sheet.setColumnWidth(i, (sheet.getColumnWidth(i)) + 512);
        }

	}

	@Override
	protected Workbook createWorkbook() {
		return new XSSFWorkbook();
	}

}