package org.pfyu.testfix.message.sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

/**
 * Servlet implementation class MessageSender
 */
@WebServlet("/MessageSender")
public class MessageSender extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public MessageSender() {
		super();
	}

	@Transactional
	private void sendMsg(String message) {
		Properties properties = new Properties();
		properties.put("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
		// properties.put("java.naming.provider.url", "iiop://localhost:7676");
		properties.put("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
		properties.put("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
		properties.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");
		properties.setProperty("org.omg.CORBA.ORBInitialPort", "3700");

		ConnectionFactory factory = null;
		javax.jms.Destination queue = null;
		try {
			Context ctx = new InitialContext(properties);
			factory = (ConnectionFactory) ctx.lookup("jms/pfyuConnectionFactory");
			queue = (Destination) ctx.lookup("jms/pfyuQueue");
		} catch (NamingException e) {
			e.printStackTrace();
		}

		try (JMSContext jmsCtx = factory.createContext();) {
			Message msg = (TextMessage) jmsCtx.createTextMessage(message);
			JMSProducer producer = jmsCtx.createProducer();
			producer.send(queue, msg);
			System.out.println("message sent");
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("TestFixMessageSender: post request received");
		int bodyLength = request.getContentLength();
		System.out.println(bodyLength);
		
		BufferedReader reader = request.getReader();
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<bodyLength;i++){
			sb.append((char)reader.read());
		}
		reader.close();
		
		sendMsg(sb.toString());
	}

}
