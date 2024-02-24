package main.java.encryption.configuration;

public class Configuration {
	private Class<? extends EncryptionEntity> entityClass;
	private Class<?> repositoryClass;
	private char[] masterKey;
	
	public Configuration setEntityClass(Class<? extends EncryptionEntity> entityClass) {
		this.entityClass = entityClass;
		return this;
	}
	
	public Configuration setRepositoryClass(Class<?> repositoryClass) {
		this.repositoryClass = repositoryClass;
		return this;
	}
	
	public Configuration setMasterkey(char[] masterKey) {
		this.masterKey = masterKey;
		return this;
	}
	
	public Class<? extends EncryptionEntity> getEntityClass() {
		return entityClass;
	}
	
	public Class<?> getRepositoryClass() {
		return repositoryClass;
	}
	
	public char[] getMasterKey() {
		return masterKey;
	}
}
