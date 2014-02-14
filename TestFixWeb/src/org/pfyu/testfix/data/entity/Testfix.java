package org.pfyu.testfix.data.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Testfix implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String testfixname;

	private String aparname;

	private String createdon;

	private String deliveredin;

	private String modifiedclass;

	private String sentby;

	private String customer;

	private String description;

	private String pmr;

	private String notes;

	public Testfix() {
	}

	public String getTestfixname() {
		return this.testfixname;
	}

	public void setTestfixname(String testfixname) {
		this.testfixname = testfixname;
	}

	public String getAparname() {
		return this.aparname;
	}

	public void setAparname(String aparname) {
		this.aparname = aparname;
	}

	public String getCreatedon() {
		return this.createdon;
	}

	public void setCreatedon(String createdon) {
		this.createdon = createdon;
	}

	public String getDeliveredin() {
		return this.deliveredin;
	}

	public void setDeliveredin(String deliveredin) {
		this.deliveredin = deliveredin;
	}

	public String getModifiedclass() {
		return this.modifiedclass;
	}

	public void setModifiedclass(String modifiedclass) {
		this.modifiedclass = modifiedclass;
	}

	public String getSentby() {
		return this.sentby;
	}

	public void setSentby(String sentby) {
		this.sentby = sentby;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPmr() {
		return pmr;
	}

	public void setPmr(String pmr) {
		this.pmr = pmr;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
