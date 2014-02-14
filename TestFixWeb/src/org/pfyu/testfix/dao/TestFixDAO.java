package org.pfyu.testfix.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.pfyu.testfix.data.entity.Testfix;

public interface TestFixDAO {

	public abstract String createTestfix(Testfix Testfix) throws Exception;

	public abstract String deleteTestfix(Testfix Testfix) throws Exception;

	public abstract String updateTestfix(Testfix Testfix) throws Exception;

	public abstract Testfix findTestfixById(String id);

	public abstract List<Testfix> getTestfixs();

}