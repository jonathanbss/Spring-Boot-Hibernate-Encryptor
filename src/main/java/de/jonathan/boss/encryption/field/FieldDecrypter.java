package de.jonathan.boss.encryption.field;

import java.lang.reflect.Field;

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import de.jonathan.boss.CryptoUtils;
import de.jonathan.boss.EncryptionUtils;
import de.jonathan.boss.encryption.EncryptionMethod;
import de.jonathan.boss.encryption.FieldProperties;
import de.jonathan.boss.encryption.configuration.EncryptionConfiguration;
import de.jonathan.boss.encryption.configuration.EncryptionInterface;
import de.jonathan.boss.error.WrongKeyException;

@Component
public class FieldDecrypter {
    
    public void decrypt(Object entity, EncryptionInterface encryptionEntity) {
    	decrypt(entity, encryptionEntity.getEncryptionKey().toCharArray());
    }
    
    public void decrypt(Object entity, char[] key) {
        ReflectionUtils.doWithFields(entity.getClass(), field -> decryptField(field, entity, key), EncryptionUtils::isFieldEncrypted);
    }
    
    private void decryptField(Field field, Object entity, char[] key) {
        field.setAccessible(true);
        Object value = ReflectionUtils.getField(field, entity);
        FieldProperties properties = field.getAnnotation(FieldProperties.class);
        if (!(value instanceof String)) {
            throw new IllegalStateException("Encrypted annotation was used on a non-String field");
        }
        try {
        	char[] encryptionKey = key;
            if(properties.encryptionMethod() == EncryptionMethod.MASTER_KEY) {
            	encryptionKey = EncryptionConfiguration.getInstance().getConfiguration().getMasterKey();
            }
			ReflectionUtils.setField(field, entity, CryptoUtils.decryptWithKey(value.toString(), encryptionKey));
		} catch (WrongKeyException e) {
			e.printStackTrace();
		}
    }
}