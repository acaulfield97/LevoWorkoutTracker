package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Workout;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WorkoutRepository extends CrudRepository<Workout, Long> {
    Optional<Workout> findByUserIdAndEndedAtIsNull(Long userId); // workout is "active" if endedAt is null
}
