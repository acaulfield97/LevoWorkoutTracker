package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Workout;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends CrudRepository<Workout, Long> {

    // find active workout for a user
    Optional<Workout> findByUserIdAndEndedAtIsNull(Long userId); // workout is "active" if endedAt is null

    // find all workouts ordered by startedAt descending
    List<Workout> findAllByOrderByStartedAtDesc();

    // find all workouts for a user between start and end date ordered by startedAt descending
    List<Workout> findAllByUserIdAndStartedAtBetweenOrderByStartedAtDesc(Long userId, java.time.LocalDateTime start, java.time.LocalDateTime end);

    // find all workouts for a user ordered by startedAt descending
    List<Workout> findAllByUserIdOrderByStartedAtDesc(Long userId);

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

    // query to get distinct days when workouts were performed
    @Query(value = """
            SELECT DISTINCT DATE(started_at) AS day
            FROM workout_session
            WHERE user_id = :userId
            ORDER BY day DESC
            """, nativeQuery = true)
    List<java.sql.Date> findWorkoutDaysByUser(@Param("userId") Long userId);


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



