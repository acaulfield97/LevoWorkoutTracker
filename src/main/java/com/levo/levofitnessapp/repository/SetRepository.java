package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Set;
import com.levo.levofitnessapp.model.WorkoutExercise;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SetRepository extends CrudRepository<Set, Long> {
    int countByWorkoutExerciseId(WorkoutExercise workoutExercise);
    Iterable<Set> findByWorkoutExerciseId(WorkoutExercise workoutExercise);
    List<Set> findByWorkoutExercise_Id(Long workoutExerciseId);
}
