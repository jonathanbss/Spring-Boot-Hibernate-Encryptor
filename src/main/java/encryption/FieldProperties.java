package main.java.encryption;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldProperties {
	boolean encrypted();
	Class<?> dataType();
	EncryptionMethod encryptionMethod() default EncryptionMethod.DEFAULT;
}
