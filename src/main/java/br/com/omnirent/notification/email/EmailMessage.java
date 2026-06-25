package br.com.omnirent.notification.email;

public record EmailMessage(
		String to,
		String subject,
		String body,
		String footer
		) {}
