package main.java.encryption.configuration;

public class Configuration {
	private char[] masterKey;
	
	public Configuration setMasterkey(char[] masterKey) {
		this.masterKey = masterKey;
		return this;
	}
	
	public char[] getMasterKey() {
		return masterKey;
	}
}
