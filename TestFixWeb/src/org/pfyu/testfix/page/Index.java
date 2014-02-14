package org.pfyu.testfix.page;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.pfyu.testfix.dao.TestFixDAO;
import org.pfyu.testfix.data.entity.Testfix;
import org.primefaces.context.RequestContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Index {

	private String className;
	private String createdOn;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String search() {
		if (className == null || className.trim().length() == 0) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "The file name must not be empty, please try again");
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
			if (fix.getModifiedclass() != null) {
				String[] array = fix.getModifiedclass().split("\r\n");
				for (String file : array) {
					if (file.toLowerCase().indexOf(query.toLowerCase()) != -1) {
						if (!results.contains(file)) {
							results.add(file);
						}
					}
				}
			}
		}
		return results;
	}

}
