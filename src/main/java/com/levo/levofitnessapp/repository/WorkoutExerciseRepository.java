package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.WorkoutExercise;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkoutExerciseRepository extends CrudRepository<WorkoutExercise, Long> {

    // find all exercise_ids for a given workout_id
    List<WorkoutExercise> findByWorkoutId(Long workoutId);

    // find workout exercises for a certain exercise type and join their sets
    @Query("""
  SELECT DISTINCT we
  FROM WorkoutExercise we
  JOIN FETCH we.workout w
  LEFT JOIN FETCH we.sets s
  WHERE we.exercise.id = :exerciseId
    AND w.userId = :userId
  ORDER BY w.startedAt DESC
""")
    List<WorkoutExercise> findByExerciseIdAndUserWithSets(
            @Param("exerciseId") Long exerciseId,
            @Param("userId") Long userId
    );


}
