package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/history")
public class WorkoutHistoryController {

    @Autowired
    private WorkoutRepository workoutRepository;

    @GetMapping
    public String showHistory(@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
                              @ModelAttribute("currentUserId") Long userId,
                              Model model) {

        // create workouts object and empty categories object
        List<Workout> workouts;

        // if a date is provided, filter workouts to only those on that date
        if (date != null) {
            var start = date.atStartOfDay();
            var end = date.plusDays(1).atStartOfDay();
            workouts = workoutRepository.findAllByUserIdAndStartedAtBetweenOrderByStartedAtDesc(userId, start, end);
            model.addAttribute("selectedDate", date);
        } else {
            workouts = workoutRepository.findAllByUserIdOrderByStartedAtDesc(userId);
        }

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

    @GetMapping("/details/{id}")
    public String showDetails(@PathVariable Long id,
                              @ModelAttribute("currentUserId") Long userId,
                              Model model) {

        Workout workout = workoutRepository.findByIdAndUserIdWithExercises(id, userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Workout not found: " + id));

        model.addAttribute("workout", workout);
        return "WorkoutExerciseHistoryPage";
    }

    // Show calendar view of workouts
    @GetMapping("/calendar")
    public String showCalendar(@ModelAttribute("currentUserId") Long userId, Model model) {
        var days = workoutRepository.findWorkoutDaysByUser(userId);

        // Convert to "YYYY-MM-DD" strings
        var workoutDays = days.stream().map(java.sql.Date::toString).toList();
        model.addAttribute("workoutDays", workoutDays);
        return "WorkoutCalendarPage";
    }
}



