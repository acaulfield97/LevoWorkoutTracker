package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Category;
import com.levo.levofitnessapp.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}
