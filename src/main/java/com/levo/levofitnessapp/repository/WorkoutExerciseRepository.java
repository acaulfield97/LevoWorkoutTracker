package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.WorkoutExercise;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutExerciseRepository extends CrudRepository<WorkoutExercise, Long> {

    Optional<WorkoutExercise> findByExerciseId(Exercise exerciseId);

    // find all exercise_ids for a given workout_id
    List<WorkoutExercise> findByWorkoutId(Long workoutId);

}
