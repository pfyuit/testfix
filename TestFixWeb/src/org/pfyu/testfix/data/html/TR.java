package org.pfyu.testfix.data.html;

import java.util.ArrayList;
import java.util.List;

public class TR {

	private List<TD> tdList = new ArrayList<TD>();
	private List<String> details = new ArrayList<String>();

	public List<String> getDetails() {
		return details;
	}

	public void setDetails(List<String> details) {
		this.details = details;
	}

	public List<TD> getTdList() {
		return tdList;
	}

	public void setTdList(List<TD> tdList) {
		this.tdList = tdList;
	}

	public void addTD(TD td) {
		tdList.add(td);
	}

}
