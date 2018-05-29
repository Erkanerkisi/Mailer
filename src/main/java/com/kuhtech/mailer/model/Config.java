package com.kuhtech.mailer.model;

public class Config {

	public Config() {
	}

	private String host;
	private String username;
	private String password;
	private String port;
	private String debugmode;
	private int interval;
	private String timeVariation;
	private String log4jPath;
	
	
	public String getLog4jPath() {
		return log4jPath;
	}
	public void setLog4jPath(String log4jPath) {
		this.log4jPath = log4jPath;
	}
	public String getTimeVariation() {
		return timeVariation;
	}
	public void setTimeVariation(String timeVariation) {
		this.timeVariation = timeVariation;
	}
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
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	

}
