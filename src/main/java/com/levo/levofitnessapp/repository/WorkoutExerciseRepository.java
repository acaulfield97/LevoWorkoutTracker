package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.model.WorkoutExercise;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkoutExerciseRepository extends CrudRepository<WorkoutExercise, Long> {
    List<WorkoutExercise> findByExercise_Id(Long exerciseId);
}
