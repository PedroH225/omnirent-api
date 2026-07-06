package br.com.omnirent.common.audit;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	
	@Enumerated(EnumType.STRING)
	private AuditAction action;
	
	private String entityId;
	
	private String actorId;
	
	private String currentBody;
	
	private String previousBody;
	
	private Instant occurredAt;
}
