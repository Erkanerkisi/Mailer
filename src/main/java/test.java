import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.mail.MessagingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.NodeList;

import com.kuhtech.mailer.model.Invoice;

public class test {

	public static void main(String[] args) throws IOException, MessagingException {
		unzipXml();
	}

	public static void unzipXml() throws IOException, MessagingException {

		String fileZip = "C:\\Erkan\\5E46C528-10AA-4DDF-995F-E62B3DA2F6FE.zip";
		ZipFile zipFile = new ZipFile(fileZip);
		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			String fileName = zipEntry.getName();

			if (FilenameUtils.getExtension(fileName).equalsIgnoreCase("xml")) {
				InputStream stream = zipFile.getInputStream(zipEntry);				
				
				Invoice invoice = parseXml(stream);

			}

			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();

	}

	public static Invoice parseXml(InputStream inputStream) {
		Invoice invoice = new Invoice();
		try {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputStream);
			// dbFactory.setNamespaceAware(true);
			doc.getDocumentElement().normalize();

			// System.out.println("Root element :" +
			// doc.getDocumentElement().getNodeName());

			// NodeList nList = doc.getElementsByTagName("cac:AccountingCustomerParty");

			// System.out.println("----------------------------");
			NodeList nList = doc.getDocumentElement().getChildNodes();
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				// System.out.println("\nCurrent Element :" + nNode.getNodeName());

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

					// System.out.println("Ad : " + eElement.getAttribute("cac:PartyName");
					// System.out.println("First Name : " +
					// eElement.getElementsByTagName("firstname").item(0).getTextContent());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return invoice;
	}

}
