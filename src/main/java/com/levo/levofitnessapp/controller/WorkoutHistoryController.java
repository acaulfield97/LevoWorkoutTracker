package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/history")
public class WorkoutHistoryController {

    @Autowired
    private WorkoutRepository workoutRepository;

    @GetMapping
    public String showHistory(Model model) {

        // create workouts object and empty categories object
        List<Workout> workouts = workoutRepository.findAllByOrderByStartedAtDesc();
        Map<Long, List<String>> workoutCategories = new HashMap<>();

        // loop through workouts to get a list of distinct category names
        for (Workout w : workouts) {
            List<String> uniqueCats =
                    (w.getWorkoutExercises() == null ? List.<String>of()
                            : w.getWorkoutExercises().stream()
                            .map(we -> we.getExercise().getCategory().getCategoryName())
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList()));
            workoutCategories.put(w.getId(), uniqueCats);
        }

        // add workout and category objects to the view
        model.addAttribute("workouts", workouts);
        model.addAttribute("workoutCategories", workoutCategories);
        return "WorkoutHistoryPage";
    }

    @PostMapping("/delete/{id}")
    public RedirectView deleteWorkout(@PathVariable Long id) {

        workoutRepository.findById(id).ifPresent(workoutRepository::delete);

        return new RedirectView("/history");
    }
}


