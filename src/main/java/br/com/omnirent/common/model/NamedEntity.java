package br.com.omnirent.common.model;

import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class NamedEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	protected String name;
	

}
