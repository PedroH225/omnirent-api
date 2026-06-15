package br.com.omnirent.notification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.omnirent.notification.context.RentalInUseNotificationData;
import br.com.omnirent.notification.context.RentalNotificationData;
import br.com.omnirent.notification.context.UserNotificationData;
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
	            u.id, u.username, u.email, u.locale)
	        FROM User u
	        WHERE u.id = :id
	        """, UserNotificationData.class)
	        .setParameter("id", userId)
	        .getResultList()
	        .stream().findFirst();
	}

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

	                (LocalDateTime) result[9],
	                (LocalDateTime) result[10]
	        ));
	}
	
	private UserNotificationData toUserData(Object[] result, int offset) {
	    return new UserNotificationData(
	            (String) result[offset],
	            (String) result[offset + 1],

	            (String) result[offset + 2],
	            (String) result[offset + 3]
	    );
	}
}
