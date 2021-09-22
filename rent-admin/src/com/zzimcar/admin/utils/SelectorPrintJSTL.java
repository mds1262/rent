package com.zzimcar.admin.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class SelectorPrintJSTL extends SimpleTagSupport {
	private String selectorName;
	private String selectorId;
	private String selectedItem;
	private String selectedText;
	private String htmlClass;
	private String htmlStyle;
	private String headerFieldName;
	private String valueFieldName;
	private String nullFieldName;
	private String onChange;
	private List<Map<String, Object>> selectorItems;

	public SelectorPrintJSTL() {
	}

	public String getSelectorName() {
		return selectorName;
	}

	public void setSelectorName(String selectorName) {
		this.selectorName = selectorName;
	}

	public String getSelectorId() {
		return selectorId;
	}

	public void setSelectorId(String selectorId) {
		this.selectorId = selectorId;
	}

	public String getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	public String getSelectedText() {
		return selectedText;
	}

	public void setSelectedText(String selectedText) {
		this.selectedText = selectedText;
	}
	
	public String getHtmlClass() {
		return htmlClass;
	}

	public void setHtmlClass(String htmlClass) {
		this.htmlClass = htmlClass;
	}

	public String getHtmlStyle() {
		return htmlStyle;
	}

	public void setHtmlStyle(String htmlStyle) {
		this.htmlStyle = htmlStyle;
	}

	public String getHeaderFieldName() {
		return headerFieldName != null ? headerFieldName : "name";
	}

	public void setHeaderFieldName(String headerFieldName) {
		this.headerFieldName = headerFieldName;
	}

	public String getValueFieldName() {
		return valueFieldName != null ? valueFieldName : "value";
	}

	public void setValueFieldName(String valueFieldName) {
		this.valueFieldName = valueFieldName;
	}

	public String getNullFieldName() {
		return nullFieldName;
	}

	public void setNullFieldName(String nullFieldName) {
		this.nullFieldName = nullFieldName;
	}

	public List<Map<String, Object>> getSelectorItems() {
		return selectorItems;
	}

	public void setSelectorItems(List<Map<String, Object>> selectorItems) {
		this.selectorItems = selectorItems;
	}
	
	public String getOnChange() {
		return onChange;
	}

	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	@Override
	public void doTag() throws JspException, IOException {
		try {
			String result = "";

			result += "<select name='" + selectorName + "'";
			if (selectorId != null && !selectorId.equals("")) {
				result += " id='" + selectorId + "'";
				;
			}
			if (htmlClass != null && !htmlClass.equals("")) {
				result += " class='" + htmlClass + "'";
			}
			if (htmlStyle != null && !htmlStyle.equals("")) {
				result += " style='" + htmlStyle + "'";
			}
			
			if (onChange != null && !onChange.equals("")) {
				result += " onChange='" + onChange + "'";
			}
			
			result += ">";

			if (nullFieldName != null && !nullFieldName.equals("")) {
				result += "<option value=''>" + nullFieldName + "</option>";
			}

			for (Map<String, Object> map : selectorItems) {
				result += "<option";
				if (map.get(getValueFieldName()) != null && !map.get(getValueFieldName()).toString().equals("")) {
					result += " value=\"" + map.get(getValueFieldName()) + "\"";
				} else {
					result += " value=\"\"";
				}
				
				
				if(map.get("data") !=null && map.get("data") instanceof Map) {
					@SuppressWarnings("unchecked")
					Map<String, Object> data = (Map<String, Object>) map.get("data");
					for(String key : data.keySet()) {
						result += " data-"+key+"=\""+ data.get(key) + "\"";
					}
				}
								
				if (selectedItem != null && !selectedItem.equals("")) {
					if (map.get(getValueFieldName()) != null && selectedItem.equals(map.get(getValueFieldName()).toString())) {
						result += " selected";
					}
				}
				
				if ((selectedItem == null || selectedItem.equals("")) && selectedText !=null && !selectedText.equals("")) {
					if (map.get(getHeaderFieldName()) != null && selectedText.equals(map.get(getHeaderFieldName()).toString())) {
						result += " selected";
					}
				}
				result += ">";
				result += map.get(getHeaderFieldName());
			}
			result += "</select>";
			getJspContext().getOut().write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
