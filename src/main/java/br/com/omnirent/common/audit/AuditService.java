package br.com.omnirent.common.audit;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class AuditService {

	private final AuditLogRepository repository;
	
    private final ObjectMapper objectMapper;

	
	public List<AuditEntry> getAuditLog(String entityId) {
		List<AuditLog> auditLogs = repository.findAllByEntityId(entityId);
		
		return auditLogs.stream()
				.map(a -> new AuditEntry(
						a.getAction(), a.getEntityId(), a.getActorId(),
						deserialize(a.getCurrentBody(), a.getAction()),
						deserialize(a.getPreviousBody(), a.getAction()),
						a.getOccurredAt()))
				.collect(Collectors.toList());
	}
	
	private AuditBody deserialize(String json, AuditAction action) {
	    if (json == null) {
	        return null;
	    }
	    
	    return objectMapper.readValue(json, action.getBodyClass());	
	}
}
