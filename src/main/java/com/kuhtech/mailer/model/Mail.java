package com.kuhtech.mailer.model;

import java.sql.Clob;

/**
 * @author DBOptimize This class is entity class of the main table
 *         xxah_earsiv_mailer
 *
 */
public class Mail {

	public Mail() {
	}
	private int id;

	private String from;
	private String to;
	private String subject;
	private String invoice_number;
	private String vendor_name;


	public String getInvoice_number() {
		return invoice_number;
	}

	public void setInvoice_number(String invoice_number) {
		this.invoice_number = invoice_number;
	}

	public String getVendor_name() {
		return vendor_name;
	}

	public void setVendor_name(String vendor_name) {
		this.vendor_name = vendor_name;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
