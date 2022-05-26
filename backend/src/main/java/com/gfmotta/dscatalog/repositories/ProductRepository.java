package com.gfmotta.dscatalog.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gfmotta.dscatalog.entities.Category;
import com.gfmotta.dscatalog.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories categories "
			+ "WHERE (:category IS NULL OR categories IN :category) "
			+ "AND (LOWER(obj.name) LIKE LOWER(CONCAT('%',:name,'%')))")
	Page<Product> findProducts(Category category, String name, Pageable pageInfo);
	
	@Query("SELECT obj FROM Product obj JOIN FETCH obj.categories categories "
			+ "WHERE obj IN :products")
	List<Product> findProductsAndCategories(List<Product> products);
	
}
