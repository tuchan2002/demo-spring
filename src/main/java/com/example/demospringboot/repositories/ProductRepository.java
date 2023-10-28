package com.example.demospringboot.repositories;

import com.example.demospringboot.models.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, String> {
    Iterable<Product> findByCategoryID(String categoryID);
}
