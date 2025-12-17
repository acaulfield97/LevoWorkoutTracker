package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.repository.ExerciseRepository;
import com.levo.levofitnessapp.repository.WorkoutExerciseRepository;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/workout-exercise")
public class WorkoutExerciseController {

    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutExerciseController(WorkoutExerciseRepository workoutExerciseRepository,
                                     WorkoutRepository workoutRepository,
                                     ExerciseRepository exerciseRepository) {
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
    }

    /**
     * Add an exercise to the active workout
     */
    @PostMapping
    public ModelAndView saveWorkoutExercise(@ModelAttribute("currentUserId") Long userId,
                                            @RequestParam Long exerciseId) {

        Workout workout = workoutRepository
                .findByUserIdAndEndedAtIsNull(userId)
                .orElseThrow(() -> new RuntimeException("No active workout found"));

        Exercise exercise = exerciseRepository
                .findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setWorkout(workout);
        workoutExercise.setExercise(exercise);

        workoutExerciseRepository.save(workoutExercise);

        return new ModelAndView(
                "redirect:/set?workoutExerciseId=" + workoutExercise.getId()
        );
    }

    /**
     * View a single workout exercise (history + sets)
     */
    @GetMapping("/{id}")
    public ModelAndView viewWorkoutExercise(@PathVariable Long id) {
        WorkoutExercise we = workoutExerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkoutExercise not found"));

        ModelAndView mav = new ModelAndView("SingleExerciseHistoryPage");

        mav.addObject("workoutExercise", we); // Ensure we pass the entire WorkoutExercise object

        return mav;
    }

}
