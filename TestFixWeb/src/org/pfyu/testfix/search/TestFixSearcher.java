package org.pfyu.testfix.search;

import java.util.ArrayList;
import java.util.List;

import org.pfyu.testfix.dao.TestFixDAO;
import org.pfyu.testfix.data.entity.Testfix;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestFixSearcher {

	private static TestFixSearcher instance;

	public static TestFixSearcher getInstance() {
		if (instance == null) {
			instance = new TestFixSearcher();
		}
		return instance;
	}

	private TestFixSearcher() {
	}

	public List<Testfix> search(String className, String createdOn) {
		System.out.println("*************Searching file************************" + className + "***on " + createdOn);
		List<Testfix> result = new ArrayList<Testfix>();
		if(className == null || createdOn == null){
			return result;
		}

		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		TestFixDAO dao = (TestFixDAO) ctx.getBean("testfixDAO");
		List<Testfix> allTestFixes = dao.getTestfixs();
		int foundCount = 0;
		for (Testfix fix : allTestFixes) {
			String modifiedClasses = fix.getModifiedclass();
			if (modifiedClasses != null && modifiedClasses.indexOf(className) != -1) {
				if (createdOn.equals("all")) {// no need to match createdOnRelease
					System.out.println("File " + className + " is found in testfix: " + fix.getTestfixname() + ", on " + createdOn);
					result.add(fix);
					foundCount++;
				} else {// need to match createdOnRelease
					String createdOnReleases = fix.getCreatedon();
					if (createdOnReleases.indexOf(createdOn) != -1) {
						System.out.println("File " + className + " is found in testfix: " + fix.getTestfixname() + ", on " + createdOn);
						result.add(fix);
						foundCount++;
					}

				}

			}
		}

		if (foundCount == 0) {
			System.out.println("The specified file is not found in the test fix list");
		}
		return result;
	}
	
	public List<Testfix> searchByCustomer(String customer, String createdOn) {
		System.out.println("*************Searching customer**********************" + customer + "***on " + createdOn);
		List<Testfix> result = new ArrayList<Testfix>();
		if(customer == null || createdOn == null){
			return result;
		}

		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		TestFixDAO dao = (TestFixDAO) ctx.getBean("testfixDAO");
		List<Testfix> allTestFixes = dao.getTestfixs();
		int foundCount = 0;
		for (Testfix fix : allTestFixes) {
			if (fix.getCustomer() != null && fix.getCustomer().indexOf(customer) != -1) {
				if (createdOn.equals("all")) {// no need to match createdOnRelease
					System.out.println("Customer " + customer + " is found in testfix: " + fix.getTestfixname() + ", on " + createdOn);
					result.add(fix);
					foundCount++;
				} else {// need to match createdOnRelease
					String createdOnReleases = fix.getCreatedon();
					if (createdOnReleases.indexOf(createdOn) != -1) {
						System.out.println("Customer " + customer + " is found in testfix: " + fix.getTestfixname() + ", on " + createdOn);
						result.add(fix);
						foundCount++;
					}

				}

			}
		}

		if (foundCount == 0) {
			System.out.println("Customer is not found in the test fix list");
		}
		return result;
	}
	
	public List<Testfix> searchByDeveloper(String developer, String createdOn) {
		System.out.println("*************Searching developer**********************" + developer + "***on " + createdOn);
		List<Testfix> result = new ArrayList<Testfix>();
		if(developer == null || createdOn == null){
			return result;
		}

		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		TestFixDAO dao = (TestFixDAO) ctx.getBean("testfixDAO");
		List<Testfix> allTestFixes = dao.getTestfixs();
		int foundCount = 0;
		for (Testfix fix : allTestFixes) {
			if (fix.getSentby() != null && fix.getSentby().indexOf(developer) != -1) {
				if (createdOn.equals("all")) {// no need to match createdOnRelease
					System.out.println("Developer " + developer + " is found in testfix: " + fix.getTestfixname() + ", on " + createdOn);
					result.add(fix);
					foundCount++;
				} else {// need to match createdOnRelease
					String createdOnReleases = fix.getCreatedon();
					if (createdOnReleases.indexOf(createdOn) != -1) {
						System.out.println("Developer " + developer + " is found in testfix: " + fix.getTestfixname() + ", on " + createdOn);
						result.add(fix);
						foundCount++;
					}

				}

			}
		}

		if (foundCount == 0) {
			System.out.println("Developer is not found in the test fix list");
		}
		return result;
	}

}
