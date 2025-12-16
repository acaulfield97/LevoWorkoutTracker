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
        """)
    Optional<Workout> findByIdWithExercises(@Param("workoutId") Long workoutId);

    // query to get distinct days when workouts were performed
    @Query(value = """
          SELECT DISTINCT DATE(started_at) AS day
          FROM workout_session
          ORDER BY day DESC
        """, nativeQuery = true)
    List<java.sql.Date> findWorkoutDays();

    // find all workouts between start and end date ordered by startedAt descending
    @Query("""
      SELECT w
      FROM Workout w
      WHERE w.startedAt >= :start AND w.startedAt < :end
      ORDER BY w.startedAt DESC
    """)
    List<Workout> findAllByStartedAtBetweenOrderByStartedAtDesc(
            @Param("start") java.time.LocalDateTime start,
            @Param("end") java.time.LocalDateTime end
    );
}
