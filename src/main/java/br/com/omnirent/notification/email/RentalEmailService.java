package br.com.omnirent.notification.email;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.notification.context.RentalInUseNotificationData;
import br.com.omnirent.notification.context.RentalLateNotificationData;
import br.com.omnirent.notification.context.RentalNotificationData;
import br.com.omnirent.notification.context.UserNotificationData;

@Service
public class RentalEmailService {
	@Autowired
	private EmailSender emailSender;
	
	@Autowired
	private MessageService messageService;
	
	private static final String OMNI_SITE = "https://omnirent.com";
	
	public void sendRentalCreatedToOwner(RentalNotificationData notificationData) {
		sendEmailToActor(
				"rental.created.owner", notificationData, notificationData.ownerData());
	}

	public void sendRentalCreatedToRenter(RentalNotificationData notificationData) {
		sendEmailToActor(
				"rental.created.renter", notificationData, notificationData.renterData());
	}
	
	public void sendRentalConfirmedToOwner(RentalNotificationData notificationData) {
		sendEmailToActor(
				"rental.confirmed.owner", notificationData, notificationData.ownerData());
	}

	public void sendRentalConfirmedToRenter(RentalNotificationData notificationData) {
		sendEmailToActor(
				"rental.confirmed.renter", notificationData, notificationData.renterData());
	}
	
	public void sendRentalPreparingToRenter(RentalNotificationData notificationData) {
		sendEmailToActor(
				"rental.preparing.renter", notificationData, notificationData.renterData());
	}
	
	public void sendRentalShippedToRenter(RentalNotificationData notificationData) {
		sendEmailToActor(
				"rental.shipped.renter", notificationData, notificationData.renterData());
	}
	

	public void sendRentalInUseToRenter(RentalInUseNotificationData notificationData) {
		sendRentalInUseToActor(
				"rental.in_use.renter", notificationData, notificationData.renterData());
	}
	
	public void sendRentalInUseToOwner(RentalInUseNotificationData notificationData) {
		sendRentalInUseToActor(
				"rental.in_use.owner", notificationData, notificationData.ownerData());
	}
	
	public void sendRentalReturnRequestedToOwner(RentalNotificationData notificationData) {
		sendEmailToActor(
				"rental.return_requested.owner", notificationData, notificationData.ownerData());
	}

	public void sendRentalReturnRequestedToRenter(RentalNotificationData notificationData) {
		sendEmailToActor(
				"rental.return_requested.renter", notificationData, notificationData.renterData());
	}
	
	public void sendRentalReturnShippedToOwner(RentalNotificationData notificationData) {
		sendEmailToActor(
				"rental.return_shipped.owner", notificationData, notificationData.ownerData());
	}

	public void sendRentalReturnShippedToRenter(RentalNotificationData notificationData) {
		sendEmailToActor(
				"rental.return_shipped.renter", notificationData, notificationData.renterData());
	}
	
	public void sendRentalReturnedToOwner(RentalNotificationData notificationData) {
		sendEmailToActor(
				"rental.returned.owner", notificationData, notificationData.ownerData());
	}

	public void sendRentalReturnedToRenter(RentalNotificationData notificationData) {
		sendEmailToActor(
				"rental.returned.renter", notificationData, notificationData.renterData());
	}
	
	public void sendRentalLateToOwner(RentalLateNotificationData notificationData) {
		sendRentalLateToActor(
				"rental.late.owner", notificationData, notificationData.ownerData());
	}

	public void sendRentalLateToRenter(RentalLateNotificationData notificationData) {
		sendRentalLateToActor(
				"rental.late.renter", notificationData, notificationData.renterData());
	}
	
	private void sendEmailToActor(
			String messageKey, RentalNotificationData rentalData, UserNotificationData targetUserData) {
		Locale userLocale = Locale.forLanguageTag(targetUserData.locale());
		String username = 
				resolveUsername(targetUserData.username(), userLocale);
		String itemName = rentalData.itemName();

		EmailMessage message = new EmailMessage(
				targetUserData.email(),
				messageService.get(buildSubject(messageKey), userLocale),
				messageService.get(buildBody(messageKey), userLocale, username, itemName),
				buildFooter(userLocale)
				);
		
		emailSender.send(message);
	}
	
	private void sendRentalInUseToActor(
			String messageKey, RentalInUseNotificationData rentalData, UserNotificationData targetUserData) {
		Locale userLocale = Locale.forLanguageTag(targetUserData.locale());
		String username = 
				resolveUsername(targetUserData.username(), userLocale);
		String itemName = rentalData.itemName();
		
		String startDate =
		        formatDateTime(rentalData.startDate(), userLocale);
		String endDate =
		        formatDateTime(rentalData.endDate(), userLocale);

		EmailMessage message = new EmailMessage(
				targetUserData.email(),
				messageService.get(buildSubject(messageKey), userLocale),
				messageService.get(buildBody(messageKey), userLocale, username, itemName, startDate, endDate),
				buildFooter(userLocale)
				);
		
		emailSender.send(message);
	}
	
	private void sendRentalLateToActor(
			String messageKey, RentalLateNotificationData rentalData, UserNotificationData targetUserData) {
		Locale userLocale = Locale.forLanguageTag(targetUserData.locale());
		String username = 
				resolveUsername(targetUserData.username(), userLocale);
		String itemName = rentalData.itemName();
		
		String endDate =
		        formatDateTime(rentalData.endDate(), userLocale);

		EmailMessage message = new EmailMessage(
				targetUserData.email(),
				messageService.get(buildSubject(messageKey), userLocale),
				messageService.get(buildBody(messageKey), userLocale, username, itemName, endDate),
				buildFooter(userLocale)
				);
		
		emailSender.send(message);
	}
	
	private String buildBody(String messageKey) {
		return "email.body." + messageKey;
	}
	
	private String buildSubject(String messageKey) {
		return "email.subject." + messageKey;
	}
	
	private String buildTo(String messageKey) {
		return "email.to." + messageKey;
	}
	
	private String buildFooter(Locale locale) {
		return messageService.get("email.footer", locale, OMNI_SITE);
	}
	
	private String resolveUsername(String username, Locale locale) {
		if (StringUtils.isBlank(username)) {
			return messageService.get(buildTo("null.username"), locale);
		}
		return username;
	}
	
	private String formatDateTime(
	        LocalDateTime dateTime,
	        Locale locale) {

	    String pattern = locale.getLanguage().equals("pt")
	            ? "dd/MM/yyyy 'às' HH:mm"
	            : "MMM d, yyyy 'at' h:mm a";

	    return dateTime.format(
	            DateTimeFormatter.ofPattern(pattern, locale)
	    );
	}
}
