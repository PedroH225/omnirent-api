package br.com.omnirent.payment.context;

import br.com.omnirent.common.enums.PaymentStatus;

public record PaymentRefundContext(
		PaymentStatus currentStatus) {}
