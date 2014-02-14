package org.pfyu.testfix.page;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.pfyu.testfix.dao.TestFixDAO;
import org.pfyu.testfix.data.entity.Testfix;
import org.primefaces.context.RequestContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SearchByDeveloper {

	private String developer;
	private String createdOn;

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String search() {
		if (developer == null || developer.trim().length() == 0) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "The developer name must not be empty, please try again");
			RequestContext.getCurrentInstance().showMessageInDialog(message);
		} else {
			return "success";
		}
		return null;
	}

	public List<String> complete(String query) {
		List<String> results = new ArrayList<>();

		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		TestFixDAO dao = (TestFixDAO) ctx.getBean("testfixDAO");
		List<Testfix> allTestFixes = dao.getTestfixs();
		for (Testfix fix : allTestFixes) {
			if (fix.getSentby() != null && fix.getSentby().toLowerCase().contains(query.toLowerCase())) {
				if (!results.contains(fix.getSentby())) {
					results.add(fix.getSentby());
				}
			}
		}
		return results;
	}

}
