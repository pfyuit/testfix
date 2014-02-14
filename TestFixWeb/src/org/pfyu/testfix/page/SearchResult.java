package org.pfyu.testfix.page;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.pfyu.testfix.data.entity.Testfix;
import org.pfyu.testfix.search.TestFixSearcher;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

public class SearchResult {

	private List<TestFixPageData> testFixes = new ArrayList<TestFixPageData>();
	private TestFixPageDataModel testFixDataModel = new TestFixPageDataModel(testFixes);
	private TestFixPageData selectedFix;

	public TestFixPageData getSelectedFix() {
		return selectedFix;
	}

	public void setSelectedFix(TestFixPageData selectedFix) {
		this.selectedFix = selectedFix;
	}

	public SearchResult() {
	}

	public List<TestFixPageData> getTestFixes() {
		testFixes.clear();

		FacesContext context = FacesContext.getCurrentInstance();
		Index index = (Index) context.getApplication().createValueBinding("#{index}").getValue(context);
		String className = index.getClassName();
		String createdOn = index.getCreatedOn();
		List<Testfix> testFixList = TestFixSearcher.getInstance().search(className, createdOn);

		for (Testfix fix : testFixList) {
			TestFixPageData data = new TestFixPageData();
			data.setTestFixName(fix.getTestfixname());
			data.setAparName(fix.getAparname());
			data.setClassNames(fix.getModifiedclass());
			data.setCreatedOnReleases(fix.getCreatedon());
			data.setPmr(fix.getPmr());
			data.setSendBy(fix.getSentby());
			data.setCustomer(fix.getCustomer());
			data.setDescription(fix.getDescription());
			data.setNotes(fix.getNotes());
			testFixes.add(data);
		}
		return testFixes;
	}

	public void setTestFixes(List<TestFixPageData> testFixes) {
		this.testFixes = testFixes;
	}

	public String returnToSearch() {
		return "index";
	}

	public TestFixPageDataModel getTestFixDataModel() {
		getTestFixes();
		return testFixDataModel;
	}

	public void setTestFixDataModel(TestFixPageDataModel testFixDataModel) {
		this.testFixDataModel = testFixDataModel;
	}

	public void onRowSelect(SelectEvent event) {
		FacesMessage msg = new FacesMessage("Fix Selected", ((TestFixPageData) event.getObject()).getTestFixName());
		FacesContext.getCurrentInstance().addMessage(null, msg);
		System.out.println("*****" + selectedFix.getTestFixName() + ":" + selectedFix.getAparName());
	}

	public void onRowUnselect(UnselectEvent event) {
		FacesMessage msg = new FacesMessage("Fix Unselected", ((TestFixPageData) event.getObject()).getTestFixName());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

}
