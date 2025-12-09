package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Category;
import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.repository.CategoryRepository;
import com.levo.levofitnessapp.repository.ExerciseRepository;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;

@Controller
@RequestMapping("/exercise")
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final CategoryRepository categoryRepository;
    private final WorkoutRepository workoutRepository;

    @Autowired
    public ExerciseController(ExerciseRepository exerciseRepository,
                              CategoryRepository categoryRepository,
                              WorkoutRepository workoutRepository) {
        this.exerciseRepository = exerciseRepository;
        this.categoryRepository = categoryRepository;
        this.workoutRepository = workoutRepository;
    }

    @GetMapping("")
    public ModelAndView exercisePage(@RequestParam(required = false) Long categoryId,
                                     @RequestParam(required = false) Long exerciseId) {

        // get all categories
        Iterable<Category> categories = categoryRepository.findAll();
        Iterable<Exercise> exercises = Collections.emptyList();

        // get exercises (filtered by category id) only if a category is selected
        if (categoryId != null) {
            exercises = exerciseRepository.findByCategoryId(categoryId);
        }

        ModelAndView modelAndView = new ModelAndView("/create_exercise");
        modelAndView.addObject("categories", categories);
        modelAndView.addObject("exercises", exercises);
        modelAndView.addObject("selectedCategoryId", categoryId);
        modelAndView.addObject("selectedExerciseId", exerciseId);

        return modelAndView;
    }

    @PostMapping("/add-exercise")
    public RedirectView addExercise(Exercise exercise){
        exerciseRepository.save(exercise);
        return new RedirectView("/exercise");
    }

    @GetMapping("/exercise-set")
    public ModelAndView showExerciseSet(@RequestParam Long exerciseId,
                                        @ModelAttribute("currentUserId") Long userId) {

        // get selected exercise
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // find active workout session for the user
        Workout workout = workoutRepository.findByUserIdAndEndedAtIsNull(userId)
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        ModelAndView modelAndView = new ModelAndView("/create_sets");
        modelAndView.addObject("exercise", exercise);
        modelAndView.addObject("workoutId", workout.getId());

        return modelAndView;
    }
}

