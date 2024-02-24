package de.jonathan.boss.encryption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EncryptionRepository extends JpaRepository<EncryptionEntity, Long> {


	@Query("SELECT ee FROM EncryptionEntity ee WHERE ee.refEntityId = :entityId AND ee.refRowId = :id")
	public EncryptionEntity find(@Param("id")int id, @Param("entityId")int entityId);
}
