package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/exercise-history")
public class ExerciseHistoryController {

    private final WorkoutExerciseRepository workoutExerciseRepository;
    private SetRepository setRepository;

    @Autowired
    public ExerciseHistoryController(WorkoutExerciseRepository workoutExerciseRepository,
                                     SetRepository setRepository) {
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.setRepository = setRepository;
    }

    @GetMapping
    public String showHistory(@RequestParam Long exerciseId,
                              @RequestParam Long workoutExerciseId,
                              Model model) {

        var exercises = workoutExerciseRepository.findByExercise_Id(exerciseId);
        var sets = setRepository.findByWorkoutExercise_Id(workoutExerciseId);

        if (exercises.isEmpty()) {
            throw new RuntimeException("No workout history found for this exercise");
        }

        if (sets.isEmpty()) {
            throw new RuntimeException("No set history found for this workout");
        }

        model.addAttribute("exercises", exercises);
        model.addAttribute("sets", sets);

        return "SingleExerciseHistory";

    }
}


