package br.com.omnirent.security.domain;

import br.com.omnirent.common.BaseEntity;
import br.com.omnirent.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_identities")
@Data
public class ExternalIdentity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	
	@Enumerated(EnumType.STRING)
	private AuthProvider provider;
	
	private String providerUserId;
	
	private String email;
		
	private boolean emailVerified;
	
	private String avatarUrl;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
}
