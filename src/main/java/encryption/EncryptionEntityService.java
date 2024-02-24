package main.java.encryption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import main.java.encryption.repository.EncryptionEntity;
import main.java.encryption.repository.EncryptionRepository;

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
