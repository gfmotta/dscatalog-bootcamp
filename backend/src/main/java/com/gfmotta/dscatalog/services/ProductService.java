package com.gfmotta.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gfmotta.dscatalog.dto.ProductDTO;
import com.gfmotta.dscatalog.entities.Product;
import com.gfmotta.dscatalog.repositories.ProductRepository;
import com.gfmotta.dscatalog.services.exceptions.DatabaseException;
import com.gfmotta.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Transactional(readOnly = true)
	public Page<ProductDTO> pagedFindAll(PageRequest pageRequest) {
		Page<Product> productPage = repository.findAll(pageRequest);
		return productPage.map(x -> new ProductDTO(x));
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product product = obj.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
		return new ProductDTO(product, product.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product product = new Product();
		//product.setName(dto.getName());
		product = repository.save(product);
		return new ProductDTO(product);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
			Product product = repository.getOne(id);
			//product.setName(dto.getName());
			product = repository.save(product);
			return new ProductDTO(product);
		} 
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id não encontrado");
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id não encontrado");
		}
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Violação de integridade: Você não pode deletar esse produto no momento");
		}
	}
}
