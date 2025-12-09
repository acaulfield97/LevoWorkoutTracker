package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;

@RestController
public class StaticPageController {

    @Autowired
    WorkoutRepository workoutRepository;

    @GetMapping("/")
    public ModelAndView landingPage() {
        return new ModelAndView("/LandingPage");
    }

    // NOTE: currentUserId can be found in GlobalControllerAdvice.java

    @PostMapping("/workout/start")
    public RedirectView startWorkout(@ModelAttribute("currentUserId") Long userId) {
        Workout workout = new Workout();
        workout.setUserId(userId);
        workout.setStartedAt(LocalDateTime.now());
        workoutRepository.save(workout);
        return new RedirectView("/"); // this should be "/selectExercise" - need @GetMapping for /selectExercise which should return SelectExercisePage
    }

    @PostMapping("/workout/finish")
    public RedirectView finishWorkout(@ModelAttribute("currentUserId") Long userId) {

        var workout = workoutRepository
                .findByUserIdAndEndedAtIsNull(userId)
                .orElse(null);

        if (workout != null) {
            workout.setEndedAt(LocalDateTime.now());
            workoutRepository.save(workout);
        }

        return new RedirectView("/");
    }

}
