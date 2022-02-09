package com.gfmotta.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gfmotta.dscatalog.dto.CategoryDTO;
import com.gfmotta.dscatalog.entities.Category;
import com.gfmotta.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		List<Category> categoryList = repository.findAll();
		return categoryList.stream()
				.map(x -> new CategoryDTO(x))
				.collect(Collectors.toList());		
	}
}
