package com.genmanner.partygm.core.framework.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
/**
 * 界面DataGrid json对象值
 *  
 *
 */
public class DDataGrid implements Serializable {
	
	public int total=0;
	
	public List<Map<String, Object>>  rows;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Map<String, Object>>  getRows() {
		return rows;
	}

	public void setRows(List<Map<String, Object>> rows) {
		this.rows = rows;
	}
	
}
