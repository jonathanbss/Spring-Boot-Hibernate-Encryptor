package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import main.java.encryption.configuration.EncryptionConfiguration;

public class EncryptionConfigurationTest {

    @Test
    public void testCalculateArea() {
    	assertFalse(EncryptionConfiguration.isInstanceAvailable());
    	
    	EncryptionConfiguration encryptionConfiguration = EncryptionConfiguration.getInstance();
    	assertTrue(EncryptionConfiguration.isInstanceAvailable());
    	
    	assertNull(encryptionConfiguration.getConfiguration());
    	
    	encryptionConfiguration.destroy();
    	assertFalse(EncryptionConfiguration.isInstanceAvailable());
    }
}
