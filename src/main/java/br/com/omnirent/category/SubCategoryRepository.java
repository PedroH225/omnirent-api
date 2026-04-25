package br.com.omnirent.category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.category.dto.SubCategoryResDTO;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, String> {
	
	@Query("""
			SELECT new br.com.omnirent.category.dto.SubCategoryResDTO(sc.id, sc.name, c.name)
			FROM SubCategory sc JOIN sc.category c
			WHERE c.name = :name
			""")
	List<SubCategoryResDTO> findAllSubByCategoryName(@Param("name")String categoryName);
	
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
	
	@Query("""
			SELECT new br.com.omnirent.category.dto.SubCategoryResDTO(sc.id, sc.name, c.name)
			FROM SubCategory sc JOIN sc.category c
			WHERE sc.id = :id
			""")
	Optional<SubCategoryResDTO> findSubById(@Param("id")String id);
	
	@Query("SELECT COUNT(sc) > 0 FROM SubCategory sc WHERE sc.id = :id")
	boolean verifySubCategory(@Param("id")String subCategoryId);

}
