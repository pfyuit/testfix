package org.pfyu.testfix.web.actions;

import com.opensymphony.xwork2.ActionSupport;

public class AdminAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String execute() throws Exception {
		System.out.println("execute admin action....");
		return super.SUCCESS;
	}
	
	

}
