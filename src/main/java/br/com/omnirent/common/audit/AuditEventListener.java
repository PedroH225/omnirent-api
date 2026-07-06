package br.com.omnirent.common.audit;

import java.time.Clock;
import java.time.Instant;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditEventListener {

	private final AuditLogRepository auditRepository;
		
	private static final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule());
	
	@EventListener
	public void handle(AuditableEvent<?> event) {
        AuditLog auditLog = new AuditLog(
                null, event.action(), event.entityId(), event.actorId(),
                toJson(event.currentBody()), toJson(event.previousBody()),
                event.occurredAt()
        );

        auditRepository.save(auditLog);
	}
	
    private String toJson(Object data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize audit data", e);
        }
    }
	
}
