package de.jonathan.boss.encryption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.jonathan.boss.encryption.repository.EncryptionEntity;
import de.jonathan.boss.encryption.repository.EncryptionRepository;

@Service
public class EncryptionEntityService {

	@Autowired
	private EncryptionRepository encryptionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void saveEncryptionEntity(EncryptionEntity encryptionEntity) {
    	encryptionRepository.save(encryptionEntity);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public EncryptionEntity find(int idValue, int entityId) {
    	return encryptionRepository.find(idValue, entityId);
    }
    
}
