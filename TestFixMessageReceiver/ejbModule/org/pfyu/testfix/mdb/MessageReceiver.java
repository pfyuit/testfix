package org.pfyu.testfix.mdb;

import java.io.File;
import java.util.Date;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.pfyu.testfix.email.EmailInfo;
import org.pfyu.testfix.email.EmailSender;

/**
 * Message-Driven Bean implementation class for: MessageReceiver
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "pfyuQueue") }, mappedName = "jms/pfyuQueue")
public class MessageReceiver implements MessageListener {

	/**
	 * Default constructor.
	 */
	public MessageReceiver() {
	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		System.out.println("message recieved");
		TextMessage msg = (TextMessage) message;
		try {
			System.out.println(msg.getText());
			EmailInfo info = new EmailInfo(new File("C:\\TestFixData\\data.xml"));
			info.setSubject(info.getSubject() + new Date());
			info.setContent(msg.getText());
			EmailSender.getInstance().sendMail(info);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
