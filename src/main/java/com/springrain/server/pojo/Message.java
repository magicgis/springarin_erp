package com.springrain.server.pojo;

public class Message {
	
	private String taskId;
	private String country;
	private String messageId;
	private Long arrivalDate;
	
	public Message()
	{
		
	}

	public Message(String taskId, String country, String messageId,
			Long arrivalDate) {
		super();
		this.taskId = taskId;
		this.country = country;
		this.messageId = messageId;
		this.arrivalDate = arrivalDate;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Long getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Long arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}

	
	
	
	
	
	
	
	
	
}