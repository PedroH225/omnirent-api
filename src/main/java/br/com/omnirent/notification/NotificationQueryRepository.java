package br.com.omnirent.notification;

import java.util.Optional;

import br.com.omnirent.notification.context.RentalInUseNotificationData;
import br.com.omnirent.notification.context.RentalLateNotificationData;
import br.com.omnirent.notification.context.RentalNotificationData;
import br.com.omnirent.notification.context.UserNotificationData;

public interface NotificationQueryRepository {

	Optional<UserNotificationData> findNotificationData(String userId);
	
	Optional<RentalNotificationData> findRentalNotificationData(String rentalId);
	
	Optional<RentalInUseNotificationData> findRentalInUseNotificationData(String rentalId);
	
	Optional<RentalLateNotificationData> findRentalLateNotificationData(String rentalId);
}
