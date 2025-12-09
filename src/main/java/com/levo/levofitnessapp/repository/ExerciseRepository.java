package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ExerciseRepository extends CrudRepository<Exercise, Long>{
    Iterable<Exercise> findByCategoryId(Long categoryId);
}
