package br.com.omnirent.payment.context.audit;

import br.com.omnirent.common.audit.AuditBody;
import br.com.omnirent.common.enums.PaymentStatus;

public record PaymentStatusChangedAuditSnapshot(
		PaymentStatus status
	) implements AuditBody {}
