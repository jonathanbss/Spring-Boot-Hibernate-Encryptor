package main.java.encryption.configuration;

public class EncryptionConfiguration {
	
	private static EncryptionConfiguration encryptionConfiguration;
	private Configuration configuration;
	
	private EncryptionConfiguration() {
		
	}
	
	public static EncryptionConfiguration getInstance() {
		if(encryptionConfiguration == null) {
			encryptionConfiguration = new EncryptionConfiguration();
		}
		
		return encryptionConfiguration;
	}
	
	public static boolean isInstanceAvailable() {
		return encryptionConfiguration != null;
	}
	
	public void destroy() {
		encryptionConfiguration = null;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
