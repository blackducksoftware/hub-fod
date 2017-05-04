package com.blackducksoftware.integration.hub.fod.domain;

import java.io.Serializable;
import java.util.List;

public class FoDAppReleaseResponseItems implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int totalCount;
	private List<FoDApplicationRelease> items;
	
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public List<FoDApplicationRelease> getItems() {
		return items;
	}
	public void setItems(List<FoDApplicationRelease> items) {
		this.items = items;
	}
	
	

}
