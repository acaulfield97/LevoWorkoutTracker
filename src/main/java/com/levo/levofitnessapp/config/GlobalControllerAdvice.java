package com.levo.levofitnessapp.config;

import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import com.levo.levofitnessapp.service.CurrentUserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// ControllerAdvice is used for a global navbar (better than using normal controller apparently)
// Otherwise would need to get username for every page/controller
@ControllerAdvice
public class GlobalControllerAdvice {

    private final CurrentUserService currentUserService;
    private final WorkoutRepository workoutRepository;

    public GlobalControllerAdvice(CurrentUserService currentUserService, WorkoutRepository workoutRepository) {
        this.currentUserService = currentUserService;
        this.workoutRepository = workoutRepository;
    }

    // Expose username to all Thymeleaf templates
    @ModelAttribute("currentUsername")
    public String currentUsername(Authentication authentication) {
        var user = currentUserService.getCurrentUser(authentication);
        return (user != null) ? user.getUsername() : null;
    }

    // Can use user's id as a global variable
    @ModelAttribute("currentUserId")
    public Long currentUserId(Authentication authentication) {
        var user = currentUserService.getCurrentUser(authentication);
        return (user != null) ? user.getId() : null;
    }

    // Global variable to check whether user has active workout
    @ModelAttribute("activeWorkout")
    public Workout activeWorkout(Authentication authentication) {
        var user = currentUserService.getCurrentUser(authentication);

        return workoutRepository
                .findByUserIdAndEndedAtIsNull(user.getId())
                .orElse(null);
    }

}
