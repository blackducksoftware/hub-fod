package com.blackducksoftware.integration.hub.fod.domain;

import java.io.Serializable;
import java.util.List;

public class FoDAppResponseItems implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int totalCount;
	private List<FoDApplication> items;
	
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public List<FoDApplication> getItems() {
		return items;
	}
	public void setItems(List<FoDApplication> items) {
		this.items = items;
	}
	
	

}
