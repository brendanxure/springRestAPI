package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.mappers.ProductMapper;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<ProductDto> getAllProducts(@RequestParam(name = "categoryId", required = false) Byte categoryId) {
       List<Product> products;
       if (categoryId != null) {
           products = productRepository.findByCategoryId(categoryId);
       }else {
           products = productRepository.findAllProductsWIthCategory();
       }
       return products.stream().map(productMapper::toDto).toList();
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto data, UriComponentsBuilder builder) {
        var category = categoryRepository.findById(data.getCategoryId()).orElse(null);

        if(category == null){
            return ResponseEntity.badRequest().build();
        }
        var product = productMapper.toEntity(data);
        product.setCategory(category);
        productRepository.save(product);
        data.setId(product.getId());

        var uri = builder.path("/products/{id}").buildAndExpand(data.getId()).toUri();

        return ResponseEntity.created(uri).body(data);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable(name = "id") Long id, @RequestBody ProductDto data) {
        var category = categoryRepository.findById(data.getCategoryId()).orElse(null);
        if(category == null){
            return ResponseEntity.badRequest().build();
        }

        var product = productRepository.findById(id).orElse(null);
        if(product == null){
            return ResponseEntity.notFound().build();
        }

        productMapper.update(data, product);
        product.setCategory(category);
        productRepository.save(product);
        data.setId(product.getId());

        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable(name = "id") Long id){
        var product = productRepository.findById(id).orElse(null);
        if(product == null){
            return ResponseEntity.notFound().build();
        }
        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }
}
