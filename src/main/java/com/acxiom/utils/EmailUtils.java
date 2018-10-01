package com.acxiom.utils;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailUtils {

	private static final Logger log = LoggerFactory.getLogger(EmailUtils.class);

	public static void send(String msgBody, List<String> addresses) {

		if (addresses.isEmpty()) {
			log.error("Email Address is empty");
			return;
		}

		PropertiesUtils p = new PropertiesUtils("application.properties");

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(p.getProperty("EMAIL_FROM")));

			for (String to : addresses) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			}
			msg.setSubject(p.getProperty("EMAIL_SUBJECT"));
			msg.setText(msgBody);
			Transport.send(msg);
			log.info("Email sent successfully…");

		} catch (AddressException e) {
			throw new RuntimeException(e);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean checkEmail(String email) {
		if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
			return false;
		}
		return true;
	}
}
