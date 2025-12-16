package com.levo.levofitnessapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workout_exercise_set")
@Data   // getters, setters
@NoArgsConstructor
@AllArgsConstructor
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_exercise_id")
    private WorkoutExercise workoutExerciseId;

    @Column(name = "set_number", nullable = false)
    private int setNumber;

    @Column(name = "reps", nullable = false)
    private int reps;

    @Column(name = "weight_kg", nullable = false)
    private int weightKg;
}
