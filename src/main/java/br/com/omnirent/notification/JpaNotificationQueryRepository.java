package br.com.omnirent.notification;

import java.util.Optional;

import org.springframework.stereotype.Repository;

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

	@Override
	public Optional<RentalNotificationData> findRentalNotificationData(String rentalId) {
		return em.createQuery("""
	        SELECT new br.com.omnirent.notification.context.RentalNotificationData(
	            i.name, o.username, o.email, o.locale, u.username, u.email, u.locale)
	        FROM Rental r
	        JOIN r.itemSnapshot i JOIN r.owner o JOIN r.renter u
	        WHERE r.id = :id
	        """, RentalNotificationData.class)
	        .setParameter("id", rentalId)
	        .getResultList()
	        .stream().findFirst();
	}
}
