package org.pfyu.testfix.page;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.pfyu.testfix.dao.TestFixDAO;
import org.pfyu.testfix.data.entity.Testfix;
import org.primefaces.context.RequestContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SearchByCustomer {

	private String customer;
	private String createdOn;


	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String search() {
		if (customer == null || customer.trim().length() == 0) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "The customer name must not be empty, please try again");
			RequestContext.getCurrentInstance().showMessageInDialog(message);
		} else {
			return "success";
		}
		return null;
	}
	
	public List<String> complete(String query){
		List<String> results = new ArrayList<>();

		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		TestFixDAO dao = (TestFixDAO) ctx.getBean("testfixDAO");
		List<Testfix> allTestFixes = dao.getTestfixs();
		for(Testfix fix : allTestFixes){
			if(fix.getCustomer() != null && fix.getCustomer().toLowerCase().contains(query.toLowerCase())){
				if(!results.contains(fix.getCustomer())){
					results.add(fix.getCustomer());
				}
			}
		}
		return results;
	}

}
