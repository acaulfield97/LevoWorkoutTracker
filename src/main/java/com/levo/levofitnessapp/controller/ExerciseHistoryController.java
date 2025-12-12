package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;


@Controller
@RequestMapping("/exercise-history")
public class ExerciseHistoryController {

    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final ExerciseRepository exerciseRepository;

    @Autowired
    public ExerciseHistoryController(WorkoutExerciseRepository workoutExerciseRepository,
                                     SetRepository setRepository,
                                     ExerciseRepository exerciseRepository1) {
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.exerciseRepository = exerciseRepository1;
    }

    @GetMapping
    public String showHistory(@RequestParam Long exerciseId,
                              Model model) {

        // get exercise - used for exercise name in page title
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + exerciseId));

        // get all workout exercises for the specific exercise type
        var workoutExercise = workoutExerciseRepository.findByExerciseIdWithSets(exerciseId);

        // sort newest to oldest
        workoutExercise.sort(
                Comparator.comparing((WorkoutExercise we) -> we.getWorkout().getStartedAt())
                        .reversed()
        );

        model.addAttribute("workoutExercises", workoutExercise);
        model.addAttribute("exercise", exercise);

        return "SingleExerciseHistory";

    }
}