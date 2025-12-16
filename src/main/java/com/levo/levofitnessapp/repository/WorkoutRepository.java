package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.model.WorkoutExercise;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends CrudRepository<Workout, Long> {
    Optional<Workout> findByUserIdAndEndedAtIsNull(Long userId); // workout is "active" if endedAt is null

    List<Workout> findAllByOrderByStartedAtDesc();

    List<Workout> findAllByUserIdOrderByStartedAtDesc(Long userId);

    @Query("""

            SELECT DISTINCT c.categoryName
       FROM Workout ws
       JOIN ws.workoutExercises we
       JOIN we.exercise e
       JOIN e.category c
       WHERE ws.id = :workoutId
       """)
    List<String> findCategoryNamesForWorkout(@Param("workoutId") Long workoutId);


    // find workout exercises for a certain exercise type and join their sets
    @Query("""
    SELECT DISTINCT wo
    FROM Workout wo
    LEFT JOIN FETCH wo.workoutExercises we
    LEFT JOIN FETCH we.exercise e
    LEFT JOIN FETCH e.category c
    WHERE wo.id = :workoutId
      AND wo.userId = :userId
    """)
    Optional<Workout> findByIdAndUserIdWithExercises(
            @Param("workoutId") Long workoutId,
            @Param("userId") Long userId
    );
        }
