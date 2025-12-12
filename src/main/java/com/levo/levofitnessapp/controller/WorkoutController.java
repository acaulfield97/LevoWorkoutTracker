package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/workout")
public class WorkoutController {

    @Autowired
    private WorkoutRepository workoutRepository;

    @PostMapping("/start")
    public RedirectView startWorkout(@ModelAttribute("currentUserId") Long userId) {
        Workout workout = new Workout();
        workout.setUserId(userId);
        workout.setStartedAt(LocalDateTime.now());
        workoutRepository.save(workout);
        return new RedirectView("/exercise");
    }

    @PostMapping("/finish")
    public RedirectView finishWorkout(@ModelAttribute("currentUserId") Long userId) {

        workoutRepository.findByUserIdAndEndedAtIsNull(userId)
                .ifPresent(workout -> {
                    workout.setEndedAt(LocalDateTime.now());
                    workoutRepository.save(workout);
                });

        return new RedirectView("/");
    }

    @PostMapping("/delete/{id}")
    public RedirectView deleteWorkout(@PathVariable Long id) {

        workoutRepository.findById(id).ifPresent(workoutRepository::delete);

        return new RedirectView("/");
    }
}

