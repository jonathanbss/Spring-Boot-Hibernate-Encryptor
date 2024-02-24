package main.java.encryption;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import main.DatabaseServiceApplication;
import main.encryption.field.FieldDecrypter;
import main.encryption.field.FieldEncrypter;
import main.entities.EncryptionEntity;
import main.entities.EntityInterface;
import utils.encryption.CryptoUtils;

@Component
public class EncryptionListener implements PreInsertEventListener, PreUpdateEventListener, PostLoadEventListener {
    @Autowired
    private FieldEncrypter fieldEncrypter;
    @Autowired
    private FieldDecrypter fieldDecrypter;
    @Autowired
    private EncryptionEntityService encryptionEntityService;

	@Override
    public void onPostLoad(PostLoadEvent event) {
        if(!(event.getEntity() instanceof EncryptionEntity)) {
	    	EncryptionEntity encryptionEntity = findEncryptionEntity(event.getEntity(), 0);
			fieldDecrypter.decrypt(event.getEntity(), encryptionEntity);
        } else {
        	EncryptionEntity encryptionEntity = (EncryptionEntity) event.getEntity();
        	//Only the encryption records which are NOT the master key hash should be decrypted
        	if(encryptionEntity.getEntityId() != 0 && encryptionEntity.getRowId() != 0) {
    			fieldDecrypter.decrypt(event.getEntity(), System.getenv(DatabaseServiceApplication.MASTER_KEY_ENV_NAME));
        	}
        }
    }
    
	@Override
    public boolean onPreInsert(PreInsertEvent event) {
        Object[] state = event.getState();
        String[] propertyNames = event.getPersister().getPropertyNames();
        Object entity = event.getEntity();
        if(!(entity instanceof EncryptionEntity)) {
	    	EncryptionEntity encryptionEntity = findEncryptionEntity(event.getEntity(), event.getId());
			fieldEncrypter.encrypt(state, propertyNames, entity, encryptionEntity);
			encryptionEntityService.saveEncryptionEntity(encryptionEntity);
        } else {
        	EncryptionEntity encryptionEntity = (EncryptionEntity) event.getEntity();
        	//Only the encryption records which are NOT the master key hash should be encrypted
        	if(encryptionEntity.getEntityId() != 0 && encryptionEntity.getRowId() != 0) {
    			fieldEncrypter.encrypt(state, propertyNames, entity, System.getenv(DatabaseServiceApplication.MASTER_KEY_ENV_NAME));
        	}
    	}
    	return false;
    }
    
	@Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Object[] state = event.getState();
        String[] propertyNames = event.getPersister().getPropertyNames();
        Object entity = event.getEntity();
        if(!(entity instanceof EncryptionEntity)) {
	    	EncryptionEntity encryptionEntity = findEncryptionEntity(event.getEntity(), event.getId());
			fieldEncrypter.encrypt(state, propertyNames, entity, encryptionEntity);
        } else {
        	fieldEncrypter.encrypt(state, propertyNames, entity, System.getenv(DatabaseServiceApplication.MASTER_KEY_ENV_NAME));
        }
        return false;
    }
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
	private EncryptionEntity findEncryptionEntity(Object entity, Serializable insertId) {
    	EncryptionEntity encryptionEntity = null;
		if(entity.getClass().getSuperclass() == EntityInterface.class) {
			int id = (int) insertId;
    		Class<? extends EntityInterface> classLoaded = (Class<? extends EntityInterface>) entity.getClass();
    		int idValue = 0;
    		if(id == 0) {
				try {
					idValue = (int) classLoaded.getMethod("getId").invoke(entity);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
    		} else {
    			idValue = id;
    		}
    		int entityId = EntityInterface.getEntityId(classLoaded);
    		encryptionEntity = encryptionEntityService.find(idValue, entityId);
			if(encryptionEntity == null || encryptionEntity.getId() == 0) {
				encryptionEntity = new EncryptionEntity();
				encryptionEntity.setEntityId(entityId);
				encryptionEntity.setRowId(idValue);
				encryptionEntity.setValue(CryptoUtils.generateSecureKey());
			}
    	}
		return encryptionEntity;
	}
}