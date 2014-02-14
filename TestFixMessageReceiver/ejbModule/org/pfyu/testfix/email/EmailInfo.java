package org.pfyu.testfix.email;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class EmailInfo {
	private String fromAddress;
	private String toAddress;
	private String subject;
	private String content;
	
	public EmailInfo(String fromAddress, String toAddress, String subject, String content){
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.subject = subject;
		this.content = content;
	}
	
	public EmailInfo(File file){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			
			XPathFactory factory = XPathFactory.newInstance();
			XPath path = factory.newXPath();
			
			this.fromAddress = path.evaluate("/email/information/from", doc);
			this.toAddress = path.evaluate("/email/information/to", doc);
			this.subject = path.evaluate("/email/information/subject", doc);
			
		} catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
			e.printStackTrace();
		}
		
	}
	
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	

}
