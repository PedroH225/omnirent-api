package br.com.omnirent.category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

	@Query("""
			SELECT new br.com.omnirent.category.CategoryResponseDTO(c.id, c.name)
			FROM Category c WHERE c.id = :id
			""")
	Optional<CategoryResponseDTO> getCategoryById(String id);
	
}
