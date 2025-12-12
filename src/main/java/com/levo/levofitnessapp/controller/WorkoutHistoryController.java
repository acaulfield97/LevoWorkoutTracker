package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;

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
}


