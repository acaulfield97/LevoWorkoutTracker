package com.levo.levofitnessapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workout_exercise")
@Data
@NoArgsConstructor

public class WorkoutExercise {

    @Id // the following field (id) is the primary key for this entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // the value of id is generated automatically
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_session_id")
    private Workout workout;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    private String notes;

}
