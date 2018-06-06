package com.kuhtech.mailer.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kuhtech.mailer.model.Invoice;

public class FileOperation {

	public static Invoice unzipXml(Map attachment) throws Exception {

		MimeBodyPart mimeBodyPart = (MimeBodyPart) attachment.get("mimeBodyPart");
		final InputStream fileZip = mimeBodyPart.getInputStream();

		ZipInputStream zis = new ZipInputStream(fileZip);
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {

			String fileName = zipEntry.getName();

			if (FilenameUtils.getExtension(fileName).equalsIgnoreCase("xml")) {
				InputStream stream = zis;

				Invoice invoice = parseXml(stream);
				MessageContent.print("Fatura No =>>> " + invoice.getInvoiceNo());
				MessageContent.print("Vendor name =>>> " + invoice.getPartyName());
				return invoice;
			}

			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
		return null;

	}

	public static Invoice parseXml(InputStream inputStream) {
		Invoice invoice = new Invoice();
		try {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputStream);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getDocumentElement().getChildNodes();
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nNode.getNodeName() == "cbc:ID") {
						Element eElement = (Element) nNode;
						System.out.println("InvoiceNO:" + eElement.getChildNodes().item(0).getTextContent());
						invoice.setInvoiceNo(eElement.getChildNodes().item(0).getTextContent());
					} else if (nNode.getNodeName() == "cac:AccountingSupplierParty") {
						Element eElement = (Element) nNode;
						System.out.println("AccountingSupplierParty: "
								+ eElement.getElementsByTagName("cac:PartyName").item(0).getTextContent().trim());
						invoice.setPartyName(
								eElement.getElementsByTagName("cac:PartyName").item(0).getTextContent().trim());
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return invoice;
	}

	public static Invoice parseHtml(Map attachment) throws Exception {
		String invoiceno = "";
		org.jsoup.nodes.Document doc;
		Invoice invoice = new Invoice();

		MimeBodyPart mimeBodyPart = (MimeBodyPart) attachment.get("mimeBodyPart");
		InputStream htmlFile = mimeBodyPart.getInputStream();
		String html = mimeBodyPart.getContent().toString();
		doc = Jsoup.parse(html, "UTF-8");

		org.jsoup.select.Elements table = doc.select("table#despatchTable");
		org.jsoup.select.Elements td = table.select("tr");
		for (org.jsoup.nodes.Element tr : td) {
			org.jsoup.select.Elements tds = tr.getElementsByTag("td");
			org.jsoup.nodes.Element e = tds.get(1);
			String element = e.text();
			if (element.length() == 16) {
				invoiceno = element;
				invoice.setInvoiceNo(invoiceno);
				MessageContent.print("html den Fatura no:" + element);
			}
		}

		return invoice;
	}
      public static String getUUID () 
	{
		  
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		return randomUUIDString;
	} 
}
