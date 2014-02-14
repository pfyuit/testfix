package org.pfyu.testfix.email;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class EmailSender {
	private static Properties properties;
	private static Authenticator authenticator;
	private static Session session;

	private EmailSender() {

	}

	private static EmailSender instance;

	public static EmailSender getInstance() {
		if (instance == null) {
			instance = new EmailSender();
			init();
		}
		return instance;
	}

	private static void init() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File("C:\\TestFixData\\data.xml"));

			XPathFactory factory = XPathFactory.newInstance();
			XPath path = factory.newXPath();

			String username = path.evaluate("/email/authentication/username", doc);
			String password = path.evaluate("/email/authentication/password", doc);
			authenticator = new EmailAuthenticator(username, password);

			String host = path.evaluate("/email/property/host", doc);
			int port = Integer.parseInt(path.evaluate("/email/property/port", doc));
			boolean auth = Boolean.parseBoolean(path.evaluate("/email/property/auth", doc));
			boolean ssl = Boolean.parseBoolean(path.evaluate("/email/property/ssl", doc));
			properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", auth);
			properties.put("mail.smtp.ssl.enable", ssl);

		} catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
			e.printStackTrace();
		}

		session = Session.getDefaultInstance(properties, authenticator);
		//session.setDebug(true);
	}

	public void sendMail(EmailInfo info) {
		Message msg = new MimeMessage(session);

		try {
			Address fromAddress = new InternetAddress(info.getFromAddress());
			Address toAddress = new InternetAddress(info.getToAddress());

			msg.setFrom(fromAddress);
			msg.setRecipient(RecipientType.TO, toAddress);

			msg.setSubject(info.getSubject());
			msg.setSentDate(new Date());

			BodyPart body = new MimeBodyPart();
			body.setContent(info.getContent(), "text/html;charset=utf-8");
			Multipart part = new MimeMultipart();
			part.addBodyPart(body);

			msg.setContent(part);

			System.out.println("start sending email");

			Transport.send(msg);

			System.out.println("email sent sucessfully");

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		EmailInfo info = new EmailInfo(new File("C:\\TestFixData\\data.xml"));
		info.setSubject(info.getSubject() + new Date());
		info.setContent("<html><header/><body><b>helloworld</body></html>");
		EmailSender.getInstance().sendMail(info);
	}

}
