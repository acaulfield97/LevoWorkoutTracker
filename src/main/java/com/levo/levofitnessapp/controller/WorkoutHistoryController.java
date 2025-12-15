package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.repository.WorkoutExerciseRepository;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/history")
public class WorkoutHistoryController {

    @Autowired
    private WorkoutRepository workoutRepository;

    @GetMapping
    public String showHistory(Model model) {
        model.addAttribute("workouts", workoutRepository.findAll());
        return "WorkoutHistoryPage";
    }

    @PostMapping("/delete/{id}")
    public RedirectView deleteWorkout(@PathVariable Long id) {

        workoutRepository.findById(id).ifPresent(workoutRepository::delete);

        return new RedirectView("/history");
    }

    @GetMapping("/details/{id}")
    public String showDetails(@PathVariable Long id, Model model) {

        Workout workout = workoutRepository.findByIdWithExercises(id)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found: " + id));

        model.addAttribute("workout", workout);
        return "WorkoutExerciseHistoryPage";
    }
}


