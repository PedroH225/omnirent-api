package br.com.omnirent.notification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.notification.context.PaymentNotificationData;
import br.com.omnirent.notification.context.RentalInUseNotificationData;
import br.com.omnirent.notification.context.RentalLateNotificationData;
import br.com.omnirent.notification.context.RentalNotificationData;
import br.com.omnirent.notification.context.UserNotificationData;
import br.com.omnirent.payment.enums.PaymentProvider;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaNotificationQueryRepository implements NotificationQueryRepository {

	private final EntityManager em;

	@Override
	public Optional<UserNotificationData> findNotificationData(String userId) {
	    return em.createQuery("""
	        SELECT new br.com.omnirent.notification.context.UserNotificationData(
	            u.id, u.username, u.email, u.userStatus, u.locale)
	        FROM User u
	        WHERE u.id = :id
	        """, UserNotificationData.class)
	        .setParameter("id", userId)
	        .getResultList()
	        .stream().findFirst();
	}

	@Override
	public Optional<RentalNotificationData> findRentalNotificationData(String rentalId) {

	    return em.createQuery("""
	        
	        SELECT i.name,
	            u.id, u.username, u.email, u.locale,
	            o.id, o.username, o.email, o.locale
	        FROM Rental r
	        JOIN r.itemSnapshot i JOIN r.owner o JOIN r.renter u
	        WHERE r.id = :id
	        """, Object[].class)
	        .setParameter("id", rentalId)
	        .getResultList().stream().findFirst()
	        .map(result -> new RentalNotificationData(
	                (String) result[0],
	                toUserData(result, 1),
	                toUserData(result, 5)
	        ));
	}
	
	@Override
	public Optional<RentalInUseNotificationData> findRentalInUseNotificationData(String rentalId) {
	    return em.createQuery("""
	        SELECT i.name,
	            u.id, u.username, u.email, u.locale,
	            o.id, o.username, o.email, o.locale,
	            r.startDate, r.endDate
	        FROM Rental r
	        JOIN r.itemSnapshot i JOIN r.owner o JOIN r.renter u
	        WHERE r.id = :id
	        """, Object[].class)
	        .setParameter("id", rentalId)
	        .getResultList().stream().findFirst()
	        .map(result -> new RentalInUseNotificationData(
	                (String) result[0],
	                toUserData(result, 1),
	                toUserData(result, 5),

	                (Instant) result[9],
	                (Instant) result[10]
	        ));
	}
	
	@Override
	public Optional<RentalLateNotificationData> findRentalLateNotificationData(String rentalId) {
	    return em.createQuery("""
	        SELECT i.name,
	            u.id, u.username, u.email, u.locale,
	            o.id, o.username, o.email, o.locale,
	            r.endDate
	        FROM Rental r
	        JOIN r.itemSnapshot i JOIN r.owner o JOIN r.renter u
	        WHERE r.id = :id
	        """, Object[].class)
	        .setParameter("id", rentalId)
	        .getResultList().stream().findFirst()
	        .map(result -> new RentalLateNotificationData(
	                (String) result[0],
	                toUserData(result, 1),
	                toUserData(result, 5),

	                (Instant) result[9]
	        ));
	}
	
	@Override
	public Optional<PaymentNotificationData> findPaymentNotificationData(String paymentId) {
		return em.createQuery("""
				SELECT i.name, p.amount, p.currency, p.externalReference.paymentProvider,
				p.externalReference.sessionUrl,
				u.id, u.username, u.email, u.locale,
				o.id, o.username, o.email, o.locale
				FROM Payment p
				JOIN p.rental r JOIN r.itemSnapshot i JOIN r.owner o JOIN r.renter u
				WHERE p.id = :id
				""", Object[].class)
				.setParameter("id", paymentId)
				.getResultList().stream().findFirst()
				.map(result -> new PaymentNotificationData(
						(String) result[0],
						(BigDecimal) result[1],
						(String) result[2],
						(PaymentProvider) result[3],
						(String) result[4],
						toUserData(result, 5),
						toUserData(result, 9)
				));
	}
	
	private UserNotificationData toUserData(Object[] result, int offset) {
	    return new UserNotificationData(
	            (String) result[offset],
	            (String) result[offset + 1],

	            (String) result[offset + 2],
	            null,
	            (String) result[offset + 3]
	    );
	}
}
