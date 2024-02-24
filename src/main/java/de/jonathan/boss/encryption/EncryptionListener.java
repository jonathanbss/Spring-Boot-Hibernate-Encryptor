package de.jonathan.boss.encryption;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

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

import de.jonathan.boss.CryptoUtils;
import de.jonathan.boss.encryption.configuration.EncryptionConfiguration;
import de.jonathan.boss.encryption.configuration.EncryptionInterface;
import de.jonathan.boss.encryption.configuration.EntityInterface;
import de.jonathan.boss.encryption.field.FieldDecrypter;
import de.jonathan.boss.encryption.field.FieldEncrypter;
import de.jonathan.boss.encryption.repository.EncryptionEntity;

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
    	EncryptionInterface encryptionEntity = findEncryptionEntity(event.getEntity(), 0);
		fieldDecrypter.decrypt(event.getEntity(), encryptionEntity);
    }
    
	@Override
    public boolean onPreInsert(PreInsertEvent event) {
        Object[] state = event.getState();
        String[] propertyNames = event.getPersister().getPropertyNames();
        Object entity = event.getEntity();
		EncryptionEntity encryptionEntity = findEncryptionEntity(event.getEntity(), event.getId());
		fieldEncrypter.encrypt(state, propertyNames, entity, encryptionEntity);
		encryptionEntityService.saveEncryptionEntity(encryptionEntity);
    	return false;
    }
    
	@Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Object[] state = event.getState();
        String[] propertyNames = event.getPersister().getPropertyNames();
        Object entity = event.getEntity();
        if(!(entity instanceof EncryptionInterface)) {
	    	EncryptionInterface encryptionEntity = findEncryptionEntity(event.getEntity(), event.getId());
			fieldEncrypter.encrypt(state, propertyNames, entity, encryptionEntity);
        } else {
        	fieldEncrypter.encrypt(state, propertyNames, entity, EncryptionConfiguration.getInstance().getConfiguration().getMasterKey());
        }
        return false;
    }
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
	private EncryptionEntity findEncryptionEntity(Object entity, Object insertId) {
    	EncryptionEntity encryptionEntity = null;
    	List<Class<?>> test = Arrays.asList(entity.getClass().getInterfaces());
		if(test.contains(EntityInterface.class)) {
			int id = (int) insertId;
    		Class<? extends EntityInterface> classLoaded = (Class<? extends EntityInterface>) entity.getClass();
    		int idValue = 0;
    		int entityId = 0;
    		if(id == 0) {
				try {
					idValue = (int) classLoaded.getMethod("getId").invoke(entity);
					entityId = (int) classLoaded.getMethod("getEntityId").invoke(entity);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
    		} else {
    			idValue = id;
    		}
    		encryptionEntity = encryptionEntityService.find(idValue, entityId);
			if(encryptionEntity == null || encryptionEntity.getId() == 0) {
				encryptionEntity = new EncryptionEntity();
				encryptionEntity.setRefEntityId(entityId);
				encryptionEntity.setRefRowId(idValue);
				encryptionEntity.setValue(CryptoUtils.generateSecureKey());
			}
    	}
		return encryptionEntity;
	}
}