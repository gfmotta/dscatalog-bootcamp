package com.gfmotta.dscatalog.resources;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfmotta.dscatalog.dto.ProductDTO;
import com.gfmotta.dscatalog.services.ProductService;
import com.gfmotta.dscatalog.services.exceptions.DatabaseException;
import com.gfmotta.dscatalog.services.exceptions.ResourceNotFoundException;
import com.gfmotta.dscatalog.tests.Factory;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objMapper;
	
	@MockBean
	private ProductService service;
	
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private ProductDTO productDto;
	private PageImpl<ProductDTO> page;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		productDto = Factory.newProductDTO();
		page = new PageImpl<>(List.of(productDto));
		
		when(service.insert(ArgumentMatchers.any())).thenReturn(productDto);
		when(service.findAll(ArgumentMatchers.any())).thenReturn(page);
		when(service.findById(existingId)).thenReturn(productDto);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		when(service.update(ArgumentMatchers.eq(existingId), ArgumentMatchers.any())).thenReturn(productDto);
		when(service.update(ArgumentMatchers.eq(nonExistingId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);
		doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DatabaseException.class).when(service).delete(dependentId);
	}
		
	@Test
	public void deleteShouldReturnBadRequestWhenDependentId() throws Exception {
		ResultActions result = mockMvc.perform(delete("/products/{id}", dependentId).accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ResultActions result = mockMvc.perform(delete("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(delete("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void insertShouldCreateAndReturnProduct() throws Exception {
		String json = objMapper.writeValueAsString(productDto);
		
		ResultActions result = mockMvc.perform(post("/products")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		ResultActions result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductDtoWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldReturnProductDtoWhenIdExists() throws Exception {
		String json = objMapper.writeValueAsString(productDto);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		String json = objMapper.writeValueAsString(productDto);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
}
