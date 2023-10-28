package com.example.demospringboot.repositories;

import com.example.demospringboot.models.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, String> {

}
