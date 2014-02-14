package org.pfyu.testfix.page;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class TestFixPageDataModel extends ListDataModel<TestFixPageData> implements SelectableDataModel<TestFixPageData>, Serializable {
	public TestFixPageDataModel() {
	}

	public TestFixPageDataModel(List<TestFixPageData> data) {
		super(data);
	}

	@Override
	public TestFixPageData getRowData(String rowKey) {
		List<TestFixPageData> testfixes = (List<TestFixPageData>) getWrappedData();

		for (TestFixPageData testfix : testfixes) {
			if (testfix.getTestFixName().equals(rowKey))
				return testfix;
		}

		return null;
	}

	@Override
	public Object getRowKey(TestFixPageData testfix) {
		return testfix.getTestFixName();
	}
}
