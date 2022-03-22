package com.gfmotta.dscatalog.tests;

import java.time.Instant;

import com.gfmotta.dscatalog.dto.ProductDTO;
import com.gfmotta.dscatalog.entities.Category;
import com.gfmotta.dscatalog.entities.Product;

public class Factory {
	
	public static Product newProduct() {
		Product product = new Product(1L, "Phone", "Good phone", 800.0, "https://img.com/img.png", Instant.parse("2020-08-25T00:00:00Z"));
		product.getCategories().add(new Category(2L, "Electronics"));
		return product;
	}
	
	public static ProductDTO newProductDTO() {
		Product product = newProduct();
		return new ProductDTO(product, product.getCategories());
	}
}
