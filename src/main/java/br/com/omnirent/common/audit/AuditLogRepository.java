package br.com.omnirent.common.audit;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String>{

	List<AuditLog> findAllByEntityId(String entityId);
}
