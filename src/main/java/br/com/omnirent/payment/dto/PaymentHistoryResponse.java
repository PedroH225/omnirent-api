package br.com.omnirent.payment.dto;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditAction;

public record PaymentHistoryResponse(
		    AuditAction action,
		    String actorId,
		    PaymentHistoryEntryResponse current,
		    PaymentHistoryEntryResponse previous,
		    Instant occurredAt
		) {}