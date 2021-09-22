package com.zzimcar.admin.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class StringCheckTextAreaPrintJSTL extends SimpleTagSupport {
	private String textareaName;
	private String textareaId;
	private String textareaClass;
	private String textareaStyle;
	private String textareaValue;
	
	private Map<String, Object> textareaEvent = new HashMap<>();
	private boolean distinguishKorAndEng = false;
	private int maxlength = 500;
	
	public String getTextareaName() {
		return textareaName;
	}

	public void setTextareaName(String textareaName) {
		this.textareaName = textareaName;
	}

	public String getTextareaId() {
		return textareaId;
	}

	public void setTextareaId(String textareaId) {
		this.textareaId = textareaId;
	}

	public String getTextareaClass() {
		return textareaClass;
	}

	public void setTextareaClass(String textareaClass) {
		this.textareaClass = textareaClass;
	}

	public String getTextareaStyle() {
		return textareaStyle;
	}

	public void setTextareaStyle(String textareaStyle) {
		this.textareaStyle = textareaStyle;
	}

	public String getTextareaValue() {
		return textareaValue;
	}

	public void setTextareaValue(String textareaValue) {
		this.textareaValue = textareaValue;
	}

	public Map<String, Object> getTextareaEvent() {
		return textareaEvent;
	}

	public void setTextareaEvent(Map<String, Object> textareaEvent) {
		this.textareaEvent = textareaEvent;
	}

	public boolean isDistinguishKorAndEng() {
		return distinguishKorAndEng;
	}

	public void setDistinguishKorAndEng(boolean distinguishKorAndEng) {
		this.distinguishKorAndEng = distinguishKorAndEng;
	}

	public int getMaxlength() {
		return maxlength;
	}

	public void setMaxlength(int maxlength) {
		this.maxlength = maxlength;
	}

	@Override
	public void doTag() throws JspException, IOException {
		try {
			String result = "";

			result += "<p>";
			
			result += "<textarea name='" + textareaName + "'";
			if (textareaId != null && !textareaId.equals("")) {
				result += " id='" + textareaId + "'";
				;
			}
			if (textareaClass != null && !textareaClass.equals("")) {
				result += " class='" + textareaClass + "'";
			}
			
			result += " style='resize: vertical;";
			if (textareaStyle != null && !textareaStyle.equals("")) {
				result += textareaStyle;
			}
			result += "'";
			
			result += " onkeyup='fnChkByte(this,"+ maxlength +", \"#"+textareaName+"_length_area\", "+ distinguishKorAndEng+")'";
			for(String event_name : textareaEvent.keySet()) {
				if(event_name != null && !event_name.equals("") && textareaEvent.get(event_name) != null && !textareaEvent.get(event_name).equals("")) {
					if(event_name.equals("onkeyup")) {
						continue;
					} else {
						result += " "+ event_name +"='" + textareaEvent.get(event_name) + "'";
					}
				}
			}
			
			result += ">";

			if (textareaValue != null && !textareaValue.equals("")) {
				result += textareaValue;
			}
			result += "</textarea>";
			result += "<span id='"+textareaName+"_length_area' style='font-size:10pt;float: right;'>0/"+maxlength + "자</span>";
			result += "</p>";
			getJspContext().getOut().write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
