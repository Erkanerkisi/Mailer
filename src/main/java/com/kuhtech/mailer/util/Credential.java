package com.kuhtech.mailer.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:config.properties")
public class Credential {

	@Value("${host}")
	private String host;
	
	@Value("${nameuser}")
	private String username;
	
	@Value("${password}")
	private String password;
	
	@Value("${port}")
	private String port;
	
	@Value("${debugmode}")
	private String debugmode;
	
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDebugmode() {
		return debugmode;
	}
	public void setDebugmode(String debugmode) {
		this.debugmode = debugmode;
	}

	
	

}
