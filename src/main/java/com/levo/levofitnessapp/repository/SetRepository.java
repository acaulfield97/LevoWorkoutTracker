package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.dto.ExercisePoint;
import com.levo.levofitnessapp.model.Set;
import com.levo.levofitnessapp.model.WorkoutExercise;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SetRepository extends CrudRepository<Set, Long> {
    int countByWorkoutExerciseId(WorkoutExercise workoutExercise);

    // Order sets by setNumber ascending
    Iterable<Set> findByWorkoutExerciseIdOrderBySetNumberAsc(WorkoutExercise workoutExercise);

    @Query("""
          SELECT new com.levo.levofitnessapp.dto.ExercisePoint(w.startedAt, MAX(s.weightKg))
          FROM Set s
          JOIN s.workoutExerciseId we
          JOIN we.workout w
          WHERE we.exercise.id = :exerciseId
          GROUP BY w.startedAt
          ORDER BY w.startedAt ASC
        """)
    List<ExercisePoint> findExerciseWeightPoints(@Param("exerciseId") Long exerciseId);

}
