package main.java.encryption.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import main.java.encryption.EncryptionMethod;
import main.java.encryption.FieldProperties;
import main.java.encryption.configuration.EncryptionInterface;

@Entity
public class EncryptionEntity implements EncryptionInterface {
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column
	private int refEntityId;
	@Column(length = 1000)
	@FieldProperties(encrypted = true, dataType = String.class, encryptionMethod = EncryptionMethod.MASTER_KEY)
	private String value;
	@Column
	private int refRowId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRefEntityId() {
		return refEntityId;
	}
	public void setRefEntityId(int refEntityId) {
		this.refEntityId = refEntityId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getRefRowId() {
		return refRowId;
	}
	public void setRefRowId(int refRowId) {
		this.refRowId = refRowId;
	}
	@Override
	public String getEncryptionKey() {
		return getValue();
	}
}
