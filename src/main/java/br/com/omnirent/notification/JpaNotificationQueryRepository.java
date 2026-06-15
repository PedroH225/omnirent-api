package br.com.omnirent.notification;

import java.util.Optional;

import org.springframework.stereotype.Repository;

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
}
