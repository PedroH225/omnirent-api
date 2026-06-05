package br.com.omnirent.common.audit;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "audit_log")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuditLog {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	private String eventType;
	
	private String entityId;
	
	private String actorId;
	
	private String oldData;

	private String newData;
	
	private Instant occurredAt;
}
