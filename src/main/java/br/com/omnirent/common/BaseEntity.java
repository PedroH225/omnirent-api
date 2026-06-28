package br.com.omnirent.common;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	protected String id;
	
	@CreatedDate
	protected Instant createdAt;
	
	@LastModifiedDate
	protected Instant updatedAt;

	@PrePersist
	protected void onPersist() {
		Instant currentDateTime = Instant.now();
		setCreatedAt(currentDateTime);
		setUpdatedAt(currentDateTime);
	}
	
	@PreUpdate
	protected void onUpdate() {
		Instant currentDateTime = Instant.now();
		setUpdatedAt(currentDateTime);
	}
}
	