package com.gfmotta.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.gfmotta.dscatalog.entities.Product;
import com.gfmotta.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long totalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		totalProducts = 25L;
	}
	
	@Test
	public void findByIdShouldReturnTheObjectWhenIdExists() {
		Optional<Product> product = repository.findById(existingId);
		
		Assertions.assertTrue(product.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnEmptyWhenIdDoesNotExist() {
		Optional<Product> product = repository.findById(nonExistingId);
		
		Assertions.assertTrue(product.isEmpty());
	}
	
	@Test
	public void saveShouldPersistAndIncrementIdWhenIdIsNull() {
		Product product = Factory.newProduct();
		
		repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertTrue(product.getId() == totalProducts + 1);
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existingId);
		
		Optional<Product> result = repository.findById(existingId);
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});
	}
}
