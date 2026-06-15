package br.com.omnirent.notification;

import java.util.Optional;

import br.com.omnirent.notification.context.UserNotificationData;

public interface NotificationQueryRepository {

	Optional<UserNotificationData> findNotificationData(String userId);
}
