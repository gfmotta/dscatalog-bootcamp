package com.gfmotta.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.gfmotta.dscatalog.dto.ProductDTO;
import com.gfmotta.dscatalog.repositories.ProductRepository;
import com.gfmotta.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIT {

	@Autowired
	private ProductService service;
	
	@Autowired
	private ProductRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long totalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 100L;
		totalProducts = 25L;
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		service.delete(existingId);
		
		Assertions.assertEquals(totalProducts - 1, repository.count());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
	
	@Test
	public void findAllShouldReturnPageWithSize10() {
		PageRequest pageInfo = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAll(pageInfo);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(0, result.getNumber());
		Assertions.assertEquals(10, result.getSize());
		Assertions.assertEquals(25, result.getTotalElements());
	}
	
	@Test
	public void findAllShouldReturnEmptyPageWhenPageDoesNotExist() {
		PageRequest pageInfo = PageRequest.of(50, 10);
		
		Page<ProductDTO> result = service.findAll(pageInfo);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() {
		PageRequest pageInfo = PageRequest.of(0, 10, Sort.by("name"));
		
		Page<ProductDTO> result = service.findAll(pageInfo);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
	}
}
