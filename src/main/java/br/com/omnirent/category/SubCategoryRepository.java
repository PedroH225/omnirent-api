package br.com.omnirent.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.category.dto.SubCategoryResDTO;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, String> {
	List<SubCategory> findAllByCategoryName(String categoryName);
	
	@Query("""
			SELECT new br.com.omnirent.category.dto.SubCategoryResDTO(sc.id, sc.name, c.name)
			FROM SubCategory sc JOIN sc.category c
			WHERE c.id = :id
			""")
	List<SubCategoryResDTO> findSubByCategoryId(String id);
	
	@Query("""
			SELECT new br.com.omnirent.category.dto.SubCategoryResDTO(sc.id, sc.name, c.name)
			FROM SubCategory sc JOIN sc.category c
			""")
	List<SubCategoryResDTO> findAllSubCat();

}
