package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.dto.ExercisePoint;
import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;


@Controller
@RequestMapping("/exercise-history")
public class ExerciseHistoryController {

    // Repositories
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final SetRepository setRepository;

    // Constructor
    @Autowired
    public ExerciseHistoryController(WorkoutExerciseRepository workoutExerciseRepository,
                                     ExerciseRepository exerciseRepository,
                                     SetRepository setRepository) {
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.exerciseRepository = exerciseRepository;
        this.setRepository = setRepository;
    }

    // Show exercise history for a specific exercise
    @GetMapping
    public String showHistory(@RequestParam Long exerciseId,
                              @ModelAttribute("currentUserId") Long userId,
                              Model model) {

        // get exercise - used for exercise name in page title
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + exerciseId));

        // get all workout exercises for the specific exercise type
        var workoutExercise = workoutExerciseRepository.findByExerciseIdAndUserWithSets(exerciseId, userId);

        // sort newest to oldest
        workoutExercise.sort(
                Comparator.comparing((WorkoutExercise we) -> we.getWorkout().getStartedAt())
                        .reversed()
        );

        // add to model
        model.addAttribute("workoutExercises", workoutExercise);
        model.addAttribute("exercise", exercise);

        return "SingleExerciseHistory";

    }


    // Show analytics for a specific exercise
    @GetMapping("/{exerciseId}/analytics")
    public String exerciseAnalytics(@PathVariable Long exerciseId, Model model) {

        // load exercise
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow();

        // create a list of data points for the exercise
        List<ExercisePoint> points = setRepository.findExerciseWeightPoints(exerciseId);

        // add to model
        model.addAttribute("exercise", exercise);
        model.addAttribute("points", points);
        return "ExerciseAnalyticsPage";
    }
}