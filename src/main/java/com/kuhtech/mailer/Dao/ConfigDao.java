package com.kuhtech.mailer.Dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.kuhtech.mailer.model.Config;

@Component
public class ConfigDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public ConfigDao() {
	}

	public Config getConfig() {
		List<Config> listConfig = jdbcTemplate.query("select * from xxah_earsiv_config", new RowMapper<Config>() {

			@Override
			public Config mapRow(ResultSet rs, int rownumber) throws SQLException {
				Config e = new Config();
				e.setHost(rs.getString(1));
				e.setUsername(rs.getString(2));
				e.setPassword(rs.getString(3));
				e.setPort(rs.getString(4));
				e.setDebugmode(rs.getString(5));
				e.setInterval(rs.getInt(6));
				e.setTimeVariation(rs.getString(7));
				e.setLog4jPath(rs.getString(8));
				return e;
			}
		});
		Config config = listConfig.get(0);
		return config;

	}

}
