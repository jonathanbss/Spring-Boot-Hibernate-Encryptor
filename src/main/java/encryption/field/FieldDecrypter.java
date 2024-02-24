package main.java.encryption.field;

import java.lang.reflect.Field;

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import main.DatabaseServiceApplication;
import main.entities.EncryptionEntity;
import main.java.EncryptionMethod;
import main.java.FieldProperties;
import main.java.encryption.EncryptionUtils;
import utils.WrongKeyException;
import utils.encryption.CryptoUtils;

@Component
public class FieldDecrypter {
    
    public void decrypt(Object entity, EncryptionEntity encryptionEntity) {
    	decrypt(entity, encryptionEntity.getValue());
    }
    
    public void decrypt(Object entity, String key) {
        ReflectionUtils.doWithFields(entity.getClass(), field -> decryptField(field, entity, key), EncryptionUtils::isFieldEncrypted);
    }
    
    private void decryptField(Field field, Object entity, String key) {
        field.setAccessible(true);
        Object value = ReflectionUtils.getField(field, entity);
        FieldProperties properties = field.getAnnotation(FieldProperties.class);
        if (!(value instanceof String)) {
            throw new IllegalStateException("Encrypted annotation was used on a non-String field");
        }
        try {
        	String encryptionKey = key;
            if(properties.encryptionMethod() == EncryptionMethod.MASTER_KEY) {
            	encryptionKey = System.getenv(DatabaseServiceApplication.MASTER_KEY_ENV_NAME);
            }
			ReflectionUtils.setField(field, entity, CryptoUtils.decryptWithKey(value.toString(), encryptionKey));
		} catch (WrongKeyException e) {
			e.printStackTrace();
		}
    }
}