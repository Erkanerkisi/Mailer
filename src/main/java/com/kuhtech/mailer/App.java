package com.kuhtech.mailer;


import java.util.List;
import java.util.Map;
import java.util.Properties;


import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;


import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.kuhtech.mailer.Dao.InvoiceDao;
import com.kuhtech.mailer.model.Invoice;
import com.kuhtech.mailer.model.Mail;
import com.kuhtech.mailer.util.Credential;
import com.kuhtech.mailer.util.FileOperation;
import com.kuhtech.mailer.util.MessageContent;
import com.sun.mail.imap.IMAPFolder;



public class App {
public static Boolean debugmode;
	
	public static void main(String[] args) {
		
	ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

	InvoiceDao invoicedao = (InvoiceDao) context.getBean("invoiceDao");
	Credential credential = (Credential) context.getBean("credential");
	debugmode = Boolean.parseBoolean(credential.getDebugmode());

	// Imap server is being called
	Properties properties = System.getProperties();
	properties.setProperty("mail.imap.socketFactory.port", credential.getPort());
	properties.setProperty("mail.store.protocol", "imap");
	Session session = Session.getInstance(properties, null);
	// set true for debugging
	session.setDebug(false);
	try {
		Store store = session.getStore();
		try {
			/**
			 * Imap server credentials are entered here this credentials have might be added
			 * from txt file to ease configuration
			 * 
			 */
			store.connect(credential.getHost(), credential.getUsername(), credential.getPassword());
		} catch (Exception e) {
		}
		IMAPFolder inbox = (IMAPFolder) store.getFolder("Inbox");
		inbox.open(Folder.READ_ONLY);
		// Get messages
		Message messages[] = inbox.getMessages();

		MessageContent.print("Reading messages...");

		// turn all messages to process earsiv invoices
		for (int j = messages.length-2; j < messages.length; j++) {
			for (Address a : messages[j].getFrom())
				//MessageContent.print("From:" + a);
			MessageContent.print("Title: " + messages[j].getSubject());
			// Get Sequence from db
			int seq = invoicedao.getSequence();
			Mail mail = new Mail();
			mail.setFrom(MessageContent.parseEmail(messages[j]));	
			String bodyCont = null;
			Invoice invoice = new Invoice();
			/*
			 * The email's content might be String type or Multipart Text(body) and html
			 * must be one but attachmet might be more than one. Here are loops in these
			 * types and operations have been added We are inserting these attachents, html
			 * of the email or text(email text) and also email's header informations to db
			 * side
			 *
			 */
			if (messages[j].getContent() instanceof String) {
				String body = messages[j].getContent().toString();
				body = body.length() > 100 ? body.substring(0, 100) + "..." : body;
				MessageContent.print("\t\t" + MessageContent.toSingleLine(body));

			} else {
				Map output = MessageContent.processMultipart((Multipart) messages[j].getContent());
				Object[] keys = output.keySet().toArray();
				for (int i = 0; i < keys.length; i++) {

					MessageContent.print("\t" + keys[i].toString().toUpperCase()
							+ "-------------------------------------------");

					if (keys[i].toString() == "attachments") {
						MessageContent.print("Attachments");
						List attachments = (List) output.get("attachments");
						for (int k = 0; k < attachments.size(); k++) {
							int lineSeq = invoicedao.getLineSequence();
							Map attachment = (Map) attachments.get(k);
							MessageContent.print("atachment var => " + attachment.get("fileName"));
							invoicedao.insertAsBlob(attachment, seq, keys[i].toString(),lineSeq);

							/*
							 * If file is zip, unzip file and parse xml to find invoice number and vendor
							 * name
							 */
							MessageContent.print(
									"ext =>>>" + FilenameUtils.getExtension((String) attachment.get("fileName")));

							if (FilenameUtils.getExtension((String) attachment.get("fileName")).equals("html")
									&& invoice.getInvoiceNo()==null
									&& invoice.getPartyName()==null) {
								invoice = FileOperation.parseHtml(attachment);

							}

							if (FilenameUtils.getExtension((String) attachment.get("fileName")).equals("zip")
									&& (invoice.getInvoiceNo()==null
									|| invoice.getPartyName()==null)) {
								invoice = FileOperation.unzipXml(attachment);

							}

						}
					} else if (keys[i].toString() == "html") {
						int lineSeq = invoicedao.getLineSequence();
						MessageContent.print("************HTML!!!!");
						bodyCont = output.get(keys[i].toString()).toString().trim();
						MessageContent.print("\t\t[[[" + bodyCont + "]]]");
						if (!bodyCont.isEmpty()) {
						invoicedao.insertAsClob(bodyCont, keys[i].toString(), seq,lineSeq);
						}
					} else {
						int lineSeq = invoicedao.getLineSequence();
						MessageContent.print("************BODY!!!!");
						bodyCont = output.get(keys[i].toString()).toString().trim();
						MessageContent.print("\t\t[[[" + bodyCont + "]]]");
						if (!bodyCont.isEmpty()){
						invoicedao.insertAsClob(bodyCont, keys[i].toString(), seq,lineSeq);
						}
					}
				}
			}
			mail.setId(seq);
			mail.setTo("KUHEARSIV");
			//mail.setFrom(InternetAddress.toString(messages[j].getFrom()));
			mail.setSubject(messages[j].getSubject());
			mail.setInvoice_number(invoice.getInvoiceNo());
			mail.setVendor_name(invoice.getPartyName());
			invoicedao.insertInvoice(mail);
		}
	} catch (Exception e) {
		e.printStackTrace();
	}

  }

}

