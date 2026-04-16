package br.com.omnirent.category.domain;

import java.util.List;

import br.com.omnirent.common.NamedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "categories")
public class Category extends NamedEntity {
	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "category")
	private List<SubCategory> subCategories;
}
