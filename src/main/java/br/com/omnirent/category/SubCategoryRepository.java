package br.com.omnirent.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, String> {
	List<SubCategory> findAllByCategoryName(String categoryName);
	
	@Query("""
			SELECT new br.com.omnirent.category.SubCategoryResDTO(sc.id, sc.name, c.name)
			FROM SubCategory sc JOIN sc.category c
			WHERE c.id = :id
			""")
	List<SubCategoryResDTO> findSubByCategoryId(String id);
	
	@Query("""
			SELECT new br.com.omnirent.category.SubCategoryResDTO(sc.id, sc.name, c.name)
			FROM SubCategory sc JOIN sc.category c
			""")
	List<SubCategoryResDTO> findAllSubCat();

}
