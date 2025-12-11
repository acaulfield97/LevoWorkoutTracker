package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.WorkoutExercise;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WorkoutExerciseRepository extends CrudRepository<WorkoutExercise, Long> {

    Optional<WorkoutExercise> findByExerciseId(Exercise exerciseId);
}
