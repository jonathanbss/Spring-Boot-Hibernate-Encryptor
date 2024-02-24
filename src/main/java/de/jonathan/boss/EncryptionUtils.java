package de.jonathan.boss;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.springframework.core.annotation.AnnotationUtils;

import de.jonathan.boss.encryption.FieldProperties;

public abstract class EncryptionUtils {
    
	public static boolean isFieldEncrypted(Field field) {
    	Annotation anno = AnnotationUtils.findAnnotation(field, FieldProperties.class);
    	if(anno == null) {
    		return false;
    	}
        FieldProperties fieldProperties = (FieldProperties) anno;
        return fieldProperties.encrypted();
    }
    
	public static int getPropertyIndex(String name, String[] properties) {
        for (int i = 0; i < properties.length; i++) {
            if (name.equals(properties[i])) {
                return i;
            }
        }
        throw new IllegalArgumentException("No property was found for name " + name);
    }
}