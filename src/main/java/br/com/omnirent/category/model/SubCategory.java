package br.com.omnirent.category.model;

import br.com.omnirent.common.model.NamedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sub_categories")
public class SubCategory extends NamedEntity {
	private static final long serialVersionUID = 1L;

}
