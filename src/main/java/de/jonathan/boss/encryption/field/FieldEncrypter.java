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

@Component
public class FieldEncrypter {
    
    public void encrypt(Object[] state, String[] propertyNames, Object entity, EncryptionInterface encryptionEntity) {
    	encrypt(state, propertyNames, entity, encryptionEntity.getEncryptionKey().toCharArray());
    }
    
    public void encrypt(Object[] state, String[] propertyNames, Object entity, char[] key) {
    	ReflectionUtils.doWithFields(entity.getClass(), field -> encryptField(field, state, propertyNames, key), EncryptionUtils::isFieldEncrypted);
    }
    
    private void encryptField(Field field, Object[] state, String[] propertyNames, char[] key) {
        int propertyIndex = EncryptionUtils.getPropertyIndex(field.getName(), propertyNames);
        FieldProperties properties = field.getAnnotation(FieldProperties.class);
        Object currentValue = state[propertyIndex];
        if (!(currentValue instanceof String) && field.getType() != String.class) {
            throw new IllegalStateException("Encrypted annotation was used on a non-String field");
        }
        char[] encryptionKey = key;
        if(properties.encryptionMethod() == EncryptionMethod.MASTER_KEY) {
        	encryptionKey = EncryptionConfiguration.getInstance().getConfiguration().getMasterKey();
        }
    	state[propertyIndex] = CryptoUtils.encryptWithKey(currentValue == null ? "" : currentValue.toString(), encryptionKey);
    }
}