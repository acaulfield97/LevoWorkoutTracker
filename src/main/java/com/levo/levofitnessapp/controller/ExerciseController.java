package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Category;
import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.model.Set;
import com.levo.levofitnessapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// got rid of unnecessary long routes (e.g. /exercise/select-exercise)
// now the /exercise route is configured by adding parameters each time a selection is made
// e.g. when a category is selected -> /exercise?categoryId=1
// and when an exercise is selected -> /exercise?categoryId=1&exerciseId=2
@Controller
@RequestMapping("/exercise") // the base route
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final CategoryRepository categoryRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final SetRepository setRepository;

    @Autowired
    public ExerciseController(ExerciseRepository exerciseRepository,
                              CategoryRepository categoryRepository,
                              WorkoutRepository workoutRepository,
                              WorkoutExerciseRepository workoutExerciseRepository,
                              SetRepository setRepository) {
        this.exerciseRepository = exerciseRepository;
        this.categoryRepository = categoryRepository;
        this.workoutRepository = workoutRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.setRepository = setRepository;
    }

    @GetMapping("")
    public ModelAndView exercisePage(@RequestParam(required = false) Long categoryId,
                                     @RequestParam(required = false) Long exerciseId,
                                     @ModelAttribute("currentUserId") Long userId) {

        // get all categories
        Iterable<Category> categories = categoryRepository.findAll();
        Iterable<Exercise> exercises = Collections.emptyList();

        // get exercises (filtered by category id) only if a category is selected
        if (categoryId != null) {
            exercises = exerciseRepository.findByCategoryId(categoryId);
        }

        // Load active workout for user
        Workout workout = workoutRepository.findByUserIdAndEndedAtIsNull(userId)
                .orElse(null);

        // Load exercises in workout
        Iterable<WorkoutExercise> workoutExercises = Collections.emptyList();
        if(workout != null) {
            workoutExercises = workoutExerciseRepository.findByWorkoutId(workout.getId());
        }

        // Load sets for each workoutExercise
        Map<Long, Iterable<Set>> setsMap = new HashMap<>();
        for (WorkoutExercise workoutExercise : workoutExercises) {
            Iterable<Set> sets = setRepository.findByWorkoutExerciseIdOrderBySetNumberAsc(workoutExercise);
            setsMap.put(workoutExercise.getId(), sets);
        }

        ModelAndView modelAndView = new ModelAndView("/create_exercise");
        modelAndView.addObject("categories", categories);
        modelAndView.addObject("exercises", exercises);
        modelAndView.addObject("selectedCategoryId", categoryId);
        modelAndView.addObject("selectedExerciseId", exerciseId);

        // objects for current workout view
        modelAndView.addObject("workout", workout);
        modelAndView.addObject("workoutExercises", workoutExercises);
        modelAndView.addObject("setsMap", setsMap);

        return modelAndView;
    }

    // page where user can create a new exercise
    @GetMapping("/new")
    public ModelAndView showCreateExercisePage() {
        ModelAndView modelAndView = new ModelAndView("/add_new_exercise");

        // pass all categories to populate the dropdown
        Iterable<Category> categories = categoryRepository.findAll();
        modelAndView.addObject("categories", categories);

        return modelAndView;
    }

    // for a user to create a new exercise
    @PostMapping("/new")
    public RedirectView addExercise(@RequestParam String exerciseName,
                                    @RequestParam Long categoryId,
                                    @ModelAttribute("currentUserId") Long userId) {

        // fetch the category
        Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found"));

        // create new exercise
        Exercise exercise = new Exercise();
        exercise.setExerciseName(exerciseName);
        exercise.setCategory(category);
        exerciseRepository.save(exercise);

        // Redirect back to /exercise with the selected category
        //return new RedirectView("/exercise?categoryId=" + categoryId);

        return new RedirectView("/exercise");
    }

    // this then routes to new sets page: /exercise/exercise-set?categoryId=1&exerciseId=2
    // possibly needs refactored for clarity???
    // e.g. instead just route to /set ? idk
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

