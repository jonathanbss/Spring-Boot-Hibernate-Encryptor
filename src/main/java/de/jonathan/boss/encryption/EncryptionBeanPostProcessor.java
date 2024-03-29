package de.jonathan.boss.encryption;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import de.jonathan.boss.encryption.configuration.EncryptionConfiguration;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

@Component
public class EncryptionBeanPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(EncryptionBeanPostProcessor.class);
    
    @PersistenceUnit
    private EntityManagerFactory emf;

    @Autowired
    private EncryptionListener encryptionListener;

    @PostConstruct
    public void registerListeners() {
    	if(!EncryptionConfiguration.isInstanceAvailable() || EncryptionConfiguration.getInstance().getConfiguration() == null) {
    		logger.error("Encryption has not been configured correctly. Please create EncryptionConfiguration-class and set Configuration field");
    		return;
    	}
    	
        SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
        registry.appendListeners(EventType.POST_LOAD, encryptionListener);
        registry.appendListeners(EventType.PRE_INSERT, encryptionListener);
        registry.appendListeners(EventType.PRE_UPDATE, encryptionListener);
        logger.info("Encryption has been successfully set up");
    }
}