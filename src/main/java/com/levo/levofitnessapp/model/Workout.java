package com.levo.levofitnessapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// instances of this class map to DB records
@Entity

// Those records can be found in the workout_session table
@Table(name = "workout_session")

// Getter, Setter, ToString, EqualsAndHashCode, RequiredArgsConstructor
@Data

// Constructor
@NoArgsConstructor

public class Workout {

    @Id // the following field (id) is the primary key for this entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // the value of id is generated automatically
    @Setter(AccessLevel.NONE) // don't want to set id as it's auto-generated
    private Long id;

    // rest of the instance variables
    private Long userId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String notes;

    @OneToMany(mappedBy = "workout")
    private List<WorkoutExercise> workoutExercises = new ArrayList<>();

}