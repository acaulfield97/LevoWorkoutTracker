package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Set;
import com.levo.levofitnessapp.model.WorkoutExercise;
import org.springframework.data.repository.CrudRepository;

public interface SetRepository extends CrudRepository<Set, Long> {
    int countByWorkoutExerciseId(WorkoutExercise workoutExercise);
}
