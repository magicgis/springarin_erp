package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="config")
public class XmlEmailConfig {
	
	private List<Country> country;
	
	public List<Country> getCountry() {
		return country;
	}

	public void setCountry(List<Country> country) {
		this.country = country;
	}

	public static class Country {
		
		private String name;
		
		private List<Email> email;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<Email> getEmail() {
			return email;
		}

		public void setEmail(List<Email> email) {
			this.email = email;
		}
	}

	public static class Email {
		
		private String subject;
		
		private String templateName;
		
		private Integer startOrderId;
		
		private Integer endOrderHours;
		
		private List<Param> param;

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}
		
		public Integer getEndOrderHours() {
			return endOrderHours;
		}

		public void setEndOrderHours(Integer endOrderHours) {
			this.endOrderHours = endOrderHours;
		}

		public String getTemplateName() {
			return templateName;
		}

		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}
		
		public List<Param> getParam() {
			return param;
		}

		public void setParam(List<Param> param) {
			this.param = param;
		}

		public Integer getStartOrderId() {
			return startOrderId;
		}

		public void setStartOrderId(Integer startOrderId) {
			this.startOrderId = startOrderId;
		}

		public static class Param{
			
			private String name; 
			
			private Map<String,String> paramMap;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public Map<String, String> getParamMap() {
				return paramMap;
			}

			public void setParamMap(Map<String, String> paramMap) {
				this.paramMap = paramMap;
			}
		} 
	}
	
}
