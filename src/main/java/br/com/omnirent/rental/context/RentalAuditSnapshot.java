package br.com.omnirent.rental.context;

import java.math.BigDecimal;

import br.com.omnirent.address.context.AddressAuditSnapshot;
import br.com.omnirent.common.audit.AuditBody;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.context.ItemAuditSnapshot;

public record RentalAuditSnapshot(
        String id,
        String startDate,
        String endDate,
        BigDecimal finalPrice,
        RentalStatus rentalStatus,
        RentalPeriod rentalPeriod,

        ItemAuditSnapshot item,
        AddressAuditSnapshot address,

        String renterId,
        String ownerId
		) implements AuditBody {}
