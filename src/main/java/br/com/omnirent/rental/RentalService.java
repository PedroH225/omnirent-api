package br.com.omnirent.rental;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.enums.RentalEnums;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.CommonErrorType;
import br.com.omnirent.exception.domain.apptype.RentalErrorType;
import br.com.omnirent.item.ItemService;
import br.com.omnirent.item.context.ItemInfo;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.payment.event.PaymentRequestedEvent;
import br.com.omnirent.rental.context.RentalInUseContext;
import br.com.omnirent.rental.context.RentalStatusChangeContext;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.domain.RentalAuthorizationService;
import br.com.omnirent.rental.domain.RentalDateService;
import br.com.omnirent.rental.domain.RentalPriceService;
import br.com.omnirent.rental.dto.RentalCreatedDTO;
import br.com.omnirent.rental.dto.RentalDetailDTO;
import br.com.omnirent.rental.dto.RentalDisplayDTO;
import br.com.omnirent.rental.dto.RentalRequestDTO;
import br.com.omnirent.rental.event.RentalCanceledEvent;
import br.com.omnirent.rental.event.RentalCreatedEvent;
import br.com.omnirent.rental.event.RentalExpiredEvent;
import br.com.omnirent.rental.event.RentalInUseEvent;
import br.com.omnirent.rental.event.RentalStatusChangedEvent;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RentalService {
	
	private RentalRepository rentalRepository;
	
	private RentalQueryRepository queryRepository;
	
	private ItemService itemService;
	
	private UserService userService;
	
	private RentalAuthorizationService authorizationService;
	
	private RentalMapper mapper;
	
	private RentalDateService rentalDateService;
	
	private Clock clock;
	
	private CurrentUserProvider currentUserProvider;
	
	private MessageService messageService;
	
	private SpringDomainEventPublisher eventPublisher;
		
	private void validateTransition(RentalStatus currStatus, RentalStatus targetStatus) {
		if (!currStatus.canTransition(targetStatus)) {
			throw new ApiException(RentalErrorType.ILLEGAL_STATE_TRANSITION,
					messageService.get(currStatus.getMessageKey()),
					messageService.get(targetStatus.getMessageKey()));
		}
	}

	public RentalDisplayDTO findRentalDisplayDTO(String id) {
		RentalDisplayDTO result = queryRepository.findRentalDisplayDTO(id)
				.orElseThrow(() -> new ApiException(RentalErrorType.NOT_FOUND));
		
		return mapper.localize(result);
	}
		
	public RentalDetailDTO getRentalById(String id) {
		RentalDetailDTO result = queryRepository.findRentalDetail(id)
				.orElseThrow(() -> new ApiException(RentalErrorType.NOT_FOUND));
		
		return mapper.localize(result);
	}
	
	private RentalStatusChangeContext getStatusChangeContext(String rentId) {
		return queryRepository.getStatusChangeContext(rentId)
				.orElseThrow(() -> new ApiException(RentalErrorType.NOT_FOUND));
	}
	
	private RentalInUseContext getInUseContext(String rentId) {
		return queryRepository.getStatusInUseContext(rentId)
				.orElseThrow(() -> new ApiException(RentalErrorType.NOT_FOUND));
	}

	@Transactional
	public RentalCreatedDTO addRent(RentalRequestDTO rentalRequestDTO) {
		String userId = currentUserProvider.currentUserId();
		User renter = userService.getValidReference(userId);
				
		ItemRentedContext context = itemService.getItemRentedContext(rentalRequestDTO.itemId());
				
		ItemInfo itemInfo = context.getItemInfo();
		
		authorizationService.requireNotOwner(userId, context.getOwnerId());
		authorizationService.canCreateRental(userId, itemInfo.getId());
		
		RentalStatus rentalStatus = RentalStatus.CREATED;
		RentalPeriod rentalPeriod = rentalRequestDTO.rentalPeriod();
		
		BigDecimal finalPrice = RentalPriceService.calculateFinalPrice(itemInfo.getBasePrice(), rentalPeriod);
		
		Rental rental = mapper.create(renter, userId, context,
			    rentalPeriod, rentalStatus,
			    finalPrice);
		
		Rental persistedRental = rentalRepository.save(rental);
		
		eventPublisher.publish(
				new RentalCreatedEvent(
						AuditAction.RENTAL_CREATED, userId, persistedRental.getId(),
						mapper.toAuditSnapshot(persistedRental), Instant.now(clock)));
		
		eventPublisher.publish(
				new PaymentRequestedEvent(rental.getId(), userId, finalPrice, "brl"));
				
		return mapper.toCreatedDto(persistedRental);
	}

	@Transactional
	public void startPreparing(String rentId) {
		RentalStatus targetStatus = RentalStatus.PREPARING;
		String currentUserId = currentUserProvider.currentUserId();
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getOwnerId()), currentUserId);
		validateTransition(currStatus, targetStatus);
		
		rentalRepository.updateRentalStatus(rentId, targetStatus);
		
		publishDefaultTransition(currentUserId, rentId, context.getRentalStatus(), targetStatus);
	}

	@Transactional
	public void ship(String rentId) {
		RentalStatus targetStatus = RentalStatus.SHIPPED;
		String currentUserId = currentUserProvider.currentUserId();
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getOwnerId()), currentUserId);
		validateTransition(currStatus, targetStatus);
		
		rentalRepository.updateRentalStatus(rentId, targetStatus);
		
		publishDefaultTransition(currentUserId, rentId, context.getRentalStatus(), targetStatus);
	}

	@Transactional
	public RentalDisplayDTO markInUse(String rentId) {
		String currentUserId = currentUserProvider.currentUserId();

		RentalStatus targetStatus = RentalStatus.IN_USE;
		RentalInUseContext context = getInUseContext(rentId);
		
		RentalStatus currStatus = context.rentalStatus();
		Set<String> actors = Set.of(context.ownerId(), context.renterId());
		
		authorizationService.requireOne(actors, currentUserId);
		
		if (currStatus.equals(RentalStatus.LATE)) {
			throw new ApiException(CommonErrorType.FORBIDDEN);
		}
		
		validateTransition(currStatus, targetStatus);
		
		Instant startDate = Instant.now(clock);
		Instant endDateTime = rentalDateService.
				calculateEndDate(startDate, context.rentalPeriod());
		
		rentalRepository.updateRentalPeriodAndStatus(rentId, targetStatus, startDate, endDateTime);
		
		RentalDisplayDTO rentalDto = findRentalDisplayDTO(rentId);
		
		publishInUseTransition(context, currentUserId, targetStatus,
				startDate, endDateTime);
		
		return rentalDto;
	}
	
	@Transactional
	public void markInUse(List<RentalInUseContext> shippedRentals) {
		RentalStatus targetStatus = RentalStatus.IN_USE;
		Instant startDate = Instant.now(clock);			
		
		for(RentalInUseContext context : shippedRentals) {
			Instant endDateTime = rentalDateService.
					calculateEndDate(startDate, context.rentalPeriod());
			rentalRepository
			.updateRentalPeriodAndStatus(context.id(), targetStatus,
					startDate, endDateTime);
			
			publishInUseTransition(context, "SYSTEM_SCHEDULER", 
					targetStatus, startDate, endDateTime);
		}
	}
	
	@Transactional
	public void renewRental(String rentalId) {
		RentalInUseContext context = getInUseContext(rentalId);

		RentalStatus targetStatus = RentalStatus.IN_USE;
		Instant startDate = Instant.now(clock);			
		Instant endDateTime = rentalDateService.
				calculateEndDate(startDate, context.rentalPeriod());
		
		rentalRepository
		.updateRentalPeriodAndStatus(rentalId, RentalStatus.IN_USE,
				startDate, endDateTime);
		
		publishInUseTransition(context, "SYSTEM_RENEWAL", targetStatus,
				startDate, endDateTime);
	}
	
	@Transactional
	public void requestReturn(String rentId) {
		RentalStatus targetStatus = RentalStatus.RETURN_REQUESTED;
		String currentUserId = currentUserProvider.currentUserId();
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getRenterId()), currentUserId);
		validateTransition(currStatus, targetStatus);
		
		rentalRepository.updateRentalStatus(rentId, targetStatus);
		
		publishDefaultTransition(currentUserId, rentId, context.getRentalStatus(), targetStatus);
	}
	
	@Transactional
	public void markReturnShipped(String rentId) {
		RentalStatus targetStatus = RentalStatus.RETURN_SHIPPED;
		String currentUserId = currentUserProvider.currentUserId();
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getRenterId()), currentUserId);
		validateTransition(currStatus, targetStatus);
		
		rentalRepository.updateRentalStatus(rentId, targetStatus);
		
		publishDefaultTransition(currentUserId, rentId, context.getRentalStatus(), targetStatus);
	}
	
	@Transactional
	public void markReturned(String rentId) {
		RentalStatus targetStatus = RentalStatus.RETURNED;
		String currentUserId = currentUserProvider.currentUserId();
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getOwnerId()), currentUserId);

		validateTransition(currStatus, targetStatus);
		
		rentalRepository.updateRentalStatus(rentId, targetStatus);
		
		publishDefaultTransition(currentUserId, rentId, context.getRentalStatus(), targetStatus);
	}
	
	@Transactional
	public void markReturned(List<RentalStatusChangeContext> returnShippedRentals) {
		RentalStatus targetStatus = RentalStatus.RETURNED;
		for (RentalStatusChangeContext context : returnShippedRentals) {
			String rentalId = context.getId();
			rentalRepository.updateRentalStatus(rentalId, targetStatus);
			
			publishDefaultTransition("SYSTEM_SCHEDULER", rentalId,
					context.getRentalStatus(), targetStatus);
		}
	}

	@Transactional
	public void cancel(String rentId) {
		RentalStatus targetStatus = RentalStatus.CANCELLED;
		String currentUserId = currentUserProvider.currentUserId();
		RentalStatusChangeContext context = getStatusChangeContext(rentId);

		RentalStatus currStatus = context.getRentalStatus();
		Set<String> actors = Set.of(context.getOwnerId(), context.getRenterId());
		
		authorizationService.requireOne(actors, currentUserId);
		
		validateTransition(currStatus, targetStatus);
		
		rentalRepository.updateRentalStatus(rentId, targetStatus);
		
		eventPublisher.publish(new RentalCanceledEvent(
				AuditAction.RENTAL_CANCELED, currentUserId, rentId,
				mapper.toStatusChangedSnapshot(targetStatus), 
				mapper.toStatusChangedSnapshot(currStatus), 
				Instant.now(clock)));
	}
	
	@Transactional
	public void expire(String rentId) {
		Instant currTime = Instant.now(clock);
		RentalStatus targetStatus = RentalStatus.EXPIRED;
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		validateTransition(currStatus, targetStatus);
		
		rentalRepository.markExpired(rentId, targetStatus, currTime);
		
		eventPublisher.publish(new RentalExpiredEvent(
				AuditAction.RENTAL_EXPIRED, "SYSTEM_SCHEDULER", rentId,
				mapper.toStatusChangedSnapshot(targetStatus), 
				mapper.toStatusChangedSnapshot(currStatus), 
				currTime));
	}

	@Transactional
	public void confirm(String rentId, RentalStatus currentStatus) {
		RentalStatus targetStatus = RentalStatus.CONFIRMED;
		
		rentalRepository.updateRentalStatus(rentId, targetStatus);
		
		publishDefaultTransition("SERVER_CONFIRMATION", rentId, currentStatus, targetStatus);
	}

	public List<RentalDisplayDTO> findUserRented() {
		String renterId = currentUserProvider.currentUserId();
		userService.requireExistence(renterId);
		
		List<RentalDisplayDTO> result = queryRepository.findUserRented(renterId);
		
		return mapper.localize(result);
	}
	
	private void publishDefaultTransition(
			String actorId, String entityId, RentalStatus oldStatus, RentalStatus newStatus) {
		eventPublisher.publish(new RentalStatusChangedEvent(
				AuditAction.RENTAL_STATUS_CHANGED, actorId, entityId,
				mapper.toStatusChangedSnapshot(newStatus), 
				mapper.toStatusChangedSnapshot(oldStatus), 
				Instant.now(clock)));
	}
	
	private void publishInUseTransition(
			RentalInUseContext context,
			String actor,
	        RentalStatus targetStatus,
	        Instant startDate,
	        Instant endDateTime) {

	    eventPublisher.publish(new RentalInUseEvent(
	            AuditAction.RENTAL_IN_USE, actor, context.id(),
	            mapper.toInUseSnapshot(targetStatus, startDate, endDateTime),
	            mapper.toInUseSnapshot(context.rentalStatus(), context.startDate(), context.endDate()),
	            Instant.now(clock)));
	}

	public List<RentalDisplayDTO> findUserRentals() {
		String ownerId = currentUserProvider.currentUserId();
		userService.requireExistence(ownerId);
		
		List<RentalDisplayDTO> result = queryRepository.findUserRentals(ownerId);
		
		return mapper.localize(result);
	}

	public RentalEnums getEnums() {
		return mapper.getLocalizedEnums();
	}
}
