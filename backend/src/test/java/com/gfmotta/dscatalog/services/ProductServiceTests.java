package com.gfmotta.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gfmotta.dscatalog.dto.ProductDTO;
import com.gfmotta.dscatalog.entities.Category;
import com.gfmotta.dscatalog.entities.Product;
import com.gfmotta.dscatalog.repositories.CategoryRepository;
import com.gfmotta.dscatalog.repositories.ProductRepository;
import com.gfmotta.dscatalog.services.exceptions.DatabaseException;
import com.gfmotta.dscatalog.services.exceptions.ResourceNotFoundException;
import com.gfmotta.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;

	@Mock
	private ProductRepository productRepository;
	
	@Mock
	private CategoryRepository categoryRepository;

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private ProductDTO productDto;
	private Product product;
	private PageImpl<Product> page;
	private Category category;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 2L;
		nonExistingId = 3L;
		dependentId = 4L;
		productDto = Factory.newProductDTO();
		product = Factory.newProduct();
		page = new PageImpl<>(List.of(product));
		category = new Category(2L, "Electronics");
		
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(productRepository.getOne(existingId)).thenReturn(product);
		Mockito.when(productRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);
		Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.doNothing().when(productRepository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, productDto);
		});

		Mockito.verify(productRepository).getOne(nonExistingId);
	}
	
	@Test
	public void updateShouldReturnProductDtoWhenIdExists() {
		ProductDTO dto = service.update(existingId, productDto);

		Assertions.assertNotNull(dto);
		Mockito.verify(productRepository).getOne(existingId);
		Mockito.verify(categoryRepository).getOne(existingId);
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});

		Mockito.verify(productRepository).findById(nonExistingId);
	}

	@Test
	public void findByIdShouldReturnProductDtoWhenIdExists() {
		ProductDTO dto = service.findById(existingId);

		Assertions.assertNotNull(dto);
		Mockito.verify(productRepository).findById(existingId);
	}

	@Test
	public void findAllShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);

		Page<ProductDTO> page = service.findAll(pageable);

		Assertions.assertNotNull(page);
		Mockito.verify(productRepository).findAll(pageable);
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});

		Mockito.verify(productRepository).deleteById(dependentId);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

		Mockito.verify(productRepository).deleteById(nonExistingId);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});

		Mockito.verify(productRepository).deleteById(existingId);
	}
}
