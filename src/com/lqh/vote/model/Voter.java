package com.lqh.vote.model;

import java.util.Random;

public class Voter {
	
	private String selectedPerson;
	private String name;
	private String address;
	private String identity;
	private String post;
	private String email;
	private String mobile;
	private String action;
	private String cap;
	private String postX;
	private String postY;
	
	//excel 表格中的行号
	private Integer excelId;
	
	static Random random = new Random();
	
	public String getSelectedPerson() {
		return selectedPerson;
	}
	public void setSelectedPerson(String selectedPerson) {
		this.selectedPerson = selectedPerson;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getCap() {
		return cap;
	}
	public void setCap(String cap) {
		this.cap = cap;
	}
	public String getPostX() {
		return postX;
	}
	public void setPostX(String postX) {
		this.postX = postX;
	}
	public String getPostY() {
		return postY;
	}
	public void setPostY(String postY) {
		this.postY = postY;
	}
	
	public Integer getExcelId() {
		return excelId;
	}
	public void setExcelId(Integer excelId) {
		this.excelId = excelId;
	}
	public Voter() {
		this.postX = String.valueOf(random.nextInt(80));
		this.postY = String.valueOf(random.nextInt(41));
	}
	
	public Voter(String selectedPerson,
				 String name, 
				 String address,
				 String identity, 
				 String post, 
				 String email, 
				 String mobile,
				 String action, 
				 String cap
				 ) {
		
		this.selectedPerson = selectedPerson;
		this.name = name;
		this.address = address;
		this.identity = identity;
		this.post = post;
		this.email = email;
		this.mobile = mobile;
		this.action = action;
		this.cap = cap;
		this.postX = String.valueOf(random.nextInt(80));
		this.postY = String.valueOf(random.nextInt(41));
	}
	
	/*public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		try {
			for(Field field : this.getClass().getDeclaredFields()) {
				final String name = field.getName();
				field.setAccessible(true);
				Object value = field.get(this);
				
				buffer.append(name + ":" + value + ", ");
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return buffer.substring(0, buffer.length()-2);
	}*/
	
	@Override
	public String toString() {
		return "(" + excelId + "　：" + name + ", " + identity + "," + mobile + ")";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identity == null) ? 0 : identity.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Voter other = (Voter) obj;
		if (identity == null) {
			if (other.identity != null)
				return false;
		} else if (!identity.equals(other.identity))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
	
}
