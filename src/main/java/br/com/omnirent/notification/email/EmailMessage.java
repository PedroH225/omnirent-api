package br.com.omnirent.notification.email;

public record EmailMessage(
		String to,
		String subject,
		String body,
		String footer,
		boolean isHtml
		) {
	public EmailMessage(
			String to,
			String subject,
			String body,
			String footer) {
        this(to, subject, body, footer, false);
	}
}
