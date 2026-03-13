package br.com.omnirent.category;

import br.com.omnirent.common.NamedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sub_categories")
public class SubCategory extends NamedEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
}
