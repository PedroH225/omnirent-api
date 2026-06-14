package br.com.omnirent.email;

public record EmailMessage(
		String to,
		String subject,
		String body
		) {}
