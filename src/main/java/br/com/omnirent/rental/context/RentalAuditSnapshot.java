package br.com.omnirent.rental.context;

import java.math.BigDecimal;

import br.com.omnirent.address.domain.AddressSnapshot;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.domain.ItemSnapshot;

public record RentalAuditSnapshot(
        String id,
        String startDate,
        String endDate,
        BigDecimal finalPrice,
        RentalStatus rentalStatus,
        RentalPeriod rentalPeriod,

        ItemSnapshot item,
        AddressSnapshot address,

        String renterId,
        String ownerId
) {}
