package com.kuhtech.mailer.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeUtility;

import com.kuhtech.mailer.App;

/**
 * @author DBOptimize This class is created for organizing email and parsing
 *         body of the email
 *
 */
public class MessageContent {

	public static String getMessageContent(Message message) throws MessagingException {
		try {
			Object content = message.getContent();
			if (content instanceof Multipart) {
				StringBuffer messageContent = new StringBuffer();
				Multipart multipart = (Multipart) content;
				for (int i = 0; i < multipart.getCount(); i++) {
					Part part = multipart.getBodyPart(i);
					if (part.isMimeType("text/plain")) {
						messageContent.append(part.getContent().toString());
					}
				}
				return messageContent.toString();
			}
			return content.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Function takes email's multipart body This might be attachmet, html view or
	 * text paragraph this function creates overall map for holding these three part
	 * above.
	 * 
	 */
	public static Map processMultipart(Multipart multipart) throws Exception {
		Map output = new HashMap();
		output.put("html", "");
		output.put("text", "");
		List attachments = new ArrayList();

		for (int i = 0; i < multipart.getCount(); i++) {
			Map result = processBodyPart(multipart.getBodyPart(i));
			if (result != null) {
				if (result.containsKey("type")) {
					if (result.get("type").toString().equalsIgnoreCase("html")) {
						output.put("html", result.get("content").toString());
					} else if (result.get("type").toString().equalsIgnoreCase("text")) {
						output.put("text", result.get("content").toString());
					} else if (result.get("type").toString().equalsIgnoreCase("attachment")) {
						attachments.add(result);
					}
				}
				if (result.containsKey("html")) {
					output.put("html", result.get("html").toString());
				}
				if (result.containsKey("text")) {
					output.put("text", result.get("text").toString());
				}
				if (result.containsKey("attachments")) {
					List thisAttachments = (List) result.get("attachments");
					for (int i2 = 0; i2 < thisAttachments.size(); i2++) {
						attachments.add(thisAttachments.get(i2));
					}
				}
			}
		}
		output.put("attachments", attachments);

		return output;
	}

	/**
	 * This function individually considers type of body parts creates a map to hold
	 * types and value against it returns map
	 */
	public static Map processBodyPart(BodyPart bodyPart) throws Exception {
		if (bodyPart.isMimeType("text/html") && bodyPart.getFileName() == null) {
			Map data = new HashMap();
			data.put("type", "html");
			data.put("content", bodyPart.getContent().toString());
			return data;
		} else if (bodyPart.isMimeType("text/plain") && bodyPart.getFileName() == null) {
			Map data = new HashMap();
			data.put("type", "text");
			data.put("content", bodyPart.getContent().toString());
			return data;
		} else if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) && bodyPart.getFileName() != null) {
			try {
				Map map = new HashMap();
				map.put("type", "attachment");
				map.put("fileName", bodyPart.getFileName());
				String fileType = bodyPart.getContentType();
				map.put("fileType", fileType.contains(":") ? fileType.substring(0, fileType.indexOf(";")) : fileType);
				map.put("mimeBodyPart", bodyPart);
				return map;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (bodyPart.getContentType().contains("multipart")) {
			Map o = processMultipart((Multipart) bodyPart.getContent());
			return o;
		}
		return null;
	}

	public static String toSingleLine(String str) throws Exception {
		return str.replaceAll("\\s+", " ");
	}
	
	public static void print(String str) throws Exception {
		 if (App.debugmode) 
			 System.out.println(str);
	}

	/**
	 * This function prints header of the email
	 * 
	 * @param message
	 */
	public static void printAllHeaders(Message message) throws Exception {
		Enumeration enumeration = message.getAllHeaders();
		while (enumeration.hasMoreElements()) {
			Header header = (Header) enumeration.nextElement();
			boolean show = !header.getName().startsWith("X-") && !header.getName().equals("Received");
			show = show && !header.getName().startsWith("Authentication-") && !header.getName().startsWith("DKIM-");
			if (show) {
				MessageContent.print("\t" + header.getName() + "===" + toSingleLine(header.getValue()));
			}
		}
	}
	
	public static String parseEmail(Message messages) throws UnsupportedEncodingException {
		String email ="";
		
		Enumeration enumeration;
		try {
			enumeration = messages.getAllHeaders();
		
		while (enumeration.hasMoreElements()) {
		Header header = (Header) enumeration.nextElement();
		if (header.getName().equals("From")) {
		//MessageContent.print(MimeUtility.decodeText(header.getValue());
			final Pattern pattern = Pattern.compile("<(.+?)>"); 
			final Matcher matcher = pattern.matcher(MimeUtility.decodeText(header.getValue())); 
			matcher.find(); 
			//System.out.println("Grup1:"+matcher.group(1)); // Prints String I want to extract
			email = matcher.group(1);
		}
		}} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	    return email;	
	}
}
