package org.pfyu.testfix.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pfyu.testfix.data.entity.Testfix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TestFixDAOImpl implements TestFixDAO{
	
	protected static final class NamedQueries {
		protected static final String getTestfixes = "SELECT c FROM Testfix c";
	}
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
	}
	
	/* (non-Javadoc)
	 * @see org.pfyu.testfix.dao.TestFixDAO#createTestfix(org.pfyu.testfix.entity.Testfix)
	 */
	@Override
	public String createTestfix(Testfix Testfix) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		session.getTransaction().begin();
		session.save(Testfix);
		session.getTransaction().commit();
		return "";
	}

	/* (non-Javadoc)
	 * @see org.pfyu.testfix.dao.TestFixDAO#deleteTestfix(org.pfyu.testfix.entity.Testfix)
	 */
	@Override
	public String deleteTestfix(Testfix Testfix) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		session.getTransaction().begin();
		session.delete(Testfix);
		session.getTransaction().commit();
		return "";
	}

	/* (non-Javadoc)
	 * @see org.pfyu.testfix.dao.TestFixDAO#updateTestfix(org.pfyu.testfix.entity.Testfix)
	 */
	@Override
	public String updateTestfix(Testfix Testfix) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		session.getTransaction().begin();
		session.update(Testfix);
		session.getTransaction().commit();
		return "";
	}

	/* (non-Javadoc)
	 * @see org.pfyu.testfix.dao.TestFixDAO#findTestfixById(java.lang.String)
	 */
	@Override
	public Testfix findTestfixById(String id) {
		Testfix Testfix = null;
		Session session = sessionFactory.getCurrentSession();
		session.getTransaction().begin();
		Testfix = (Testfix) session.get(Testfix.class, id);
		session.getTransaction().commit();
		return Testfix;
	}

	/* (non-Javadoc)
	 * @see org.pfyu.testfix.dao.TestFixDAO#getTestfixs()
	 */
	@Override
	public List<Testfix> getTestfixs() {
		Session session = sessionFactory.getCurrentSession();
		List<Testfix> results = null;
		session.getTransaction().begin();
		Query query = session.createQuery(NamedQueries.getTestfixes);
		results = (List<Testfix>) query.list();
		session.getTransaction().commit();
		return results;
	}
	
	/**
	 * Test case1
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		
		// query
		TestFixDAO sm = (TestFixDAO)ctx.getBean("testfixDAO");
		List<Testfix> Testfixs = sm.getTestfixs();
		Testfix TestfixForUpdate = new Testfix();
		for (Testfix Testfix : Testfixs) {
			if (Testfix.getTestfixname().equals("RATLC025116679"))
				TestfixForUpdate = Testfix;
			System.out.println(Testfix.getTestfixname() + " : " + Testfix.getAparname());
		}
		System.out.println("query Testfix successfully\n");

		// update
		TestfixForUpdate.setAparname(TestfixForUpdate.getAparname()+"_");
		sm.updateTestfix(TestfixForUpdate);
		System.out.println("update Testfix successfully\n");

		// add
		Testfix newTestFix = new Testfix();
		newTestFix.setTestfixname("RATLC0251166734");
		newTestFix.setAparname("PM00988");
		sm.createTestfix(newTestFix);
		System.out.println("add Testfix successfully\n");

		// delete
		sm.deleteTestfix(newTestFix);
		System.out.println("delete Testfix successfully\n");
	}

}
