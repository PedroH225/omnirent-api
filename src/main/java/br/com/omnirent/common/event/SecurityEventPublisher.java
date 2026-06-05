package br.com.omnirent.common.event;

public interface SecurityEventPublisher {

	void publish(SecurityEvent event);
}
