package main.java.encryption.field;

import java.lang.reflect.Field;

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import main.DatabaseServiceApplication;
import main.entities.EncryptionEntity;
import main.java.EncryptionMethod;
import main.java.FieldProperties;
import main.java.encryption.EncryptionUtils;
import utils.encryption.CryptoUtils;

@Component
public class FieldEncrypter {
    
    public void encrypt(Object[] state, String[] propertyNames, Object entity, EncryptionEntity encryptionEntity) {
    	encrypt(state, propertyNames, entity, encryptionEntity.getValue());
    }
    
    public void encrypt(Object[] state, String[] propertyNames, Object entity, String key) {
    	ReflectionUtils.doWithFields(entity.getClass(), field -> encryptField(field, state, propertyNames, key), EncryptionUtils::isFieldEncrypted);
    }
    
    private void encryptField(Field field, Object[] state, String[] propertyNames, String key) {
        int propertyIndex = EncryptionUtils.getPropertyIndex(field.getName(), propertyNames);
        FieldProperties properties = field.getAnnotation(FieldProperties.class);
        Object currentValue = state[propertyIndex];
        if (!(currentValue instanceof String) && field.getType() != String.class) {
            throw new IllegalStateException("Encrypted annotation was used on a non-String field");
        }
        String encryptionKey = key;
        if(properties.encryptionMethod() == EncryptionMethod.MASTER_KEY) {
        	encryptionKey = System.getenv(DatabaseServiceApplication.MASTER_KEY_ENV_NAME);
        }
    	state[propertyIndex] = CryptoUtils.encryptWithKey(currentValue == null ? "" : currentValue.toString(), encryptionKey);
    }
}