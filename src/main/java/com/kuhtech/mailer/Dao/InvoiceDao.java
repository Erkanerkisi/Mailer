package com.kuhtech.mailer.Dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.stereotype.Component;

import com.kuhtech.mailer.model.Mail;

/**
 * @author DBOptimize
 * 
 *         This class is responsible for database operations xxah_earsiv_mailer
 *         is main table to holding header information of the email Also there
 *         are functions that insert clob and blob datas to different table on
 *         db side
 * 
 */
@Component
public class InvoiceDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DefaultLobHandler lobHandler;

	public int insertInvoice(Mail v) {
		
		String query = "insert into xxah_earsiv_mailer(header_id,to_mail,from_mail,subject,invoice_number,vendor_name,creation_date) "
				+ "values(" + v.getId() + ",'" + v.getTo() + "','" + v.getFrom() + "','" + v.getSubject() + "','" + v.getInvoice_number()
				+ "','" + v.getVendor_name() + "',sysdate)";
		return jdbcTemplate.update(query);
	}

	public int getSequence() {
		int seq = (int) jdbcTemplate.queryForLong("select xxah_earsiv_idx.nextval from dual");
		return seq;

	}

	public int getLineSequence() {
		int seq = (int) jdbcTemplate.queryForLong("select xxah_earsiv_files_idx.nextval from dual");
		return seq;

	}

	public int insertAsClob(String data, String type, int seq,int lineSeq) throws IOException {

		String s = "insert into xxah_earsiv_files (header_id,line_id,type,body) values (?,?,?,?)";
		return jdbcTemplate.update(s, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, seq);
				ps.setInt(2, lineSeq);
				ps.setString(3, type);
				lobHandler.getLobCreator().setClobAsString(ps, 4, data);
				// lobHandler.getLobCreator().setBlobAsBinaryStream(ps, 2, fileAsStream,
				// fileAsStream.toString().getBytes().length);
			}
		});

	}

	public int insertAsBlob(Map attachment, int seq, String type,int lineSeq) throws IOException, MessagingException {

		MimeBodyPart mimeBodyPart = (MimeBodyPart) attachment.get("mimeBodyPart");

		final InputStream blobIs = mimeBodyPart.getInputStream();
		return jdbcTemplate.execute("INSERT INTO xxah_earsiv_files (header_id,line_id,type, attachment) VALUES (?, ?,?,?)",
				new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
					protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
						ps.setLong(1, seq);
						ps.setLong(2, lineSeq);
						ps.setString(3, type);
						// lobCreator.setClobAsCharacterStream(ps, 2, data);
						// lobHandler.getLobCreator().setClobAsString(ps, 2, "clob test");
						try {
							lobCreator.setBlobAsBinaryStream(ps, 4, blobIs, (int) mimeBodyPart.getSize());
						} catch (MessagingException e) {

							e.printStackTrace();
						}

					}
				});

	}

}
