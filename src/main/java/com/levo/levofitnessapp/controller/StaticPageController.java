package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Category;
import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.repository.CategoryRepository;
import com.levo.levofitnessapp.repository.ExerciseRepository;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;

import java.util.List;

@RestController
public class StaticPageController {

    CategoryRepository categoryRepository;
    ExerciseRepository exerciseRepository;

    public StaticPageController(CategoryRepository categoryRepository, ExerciseRepository exerciseRepository) {
        this.categoryRepository = categoryRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Autowired
    WorkoutRepository workoutRepository;

    @GetMapping("/")
    public ModelAndView landingPage() {
        return new ModelAndView("/LandingPage");
    }

    @GetMapping("/exercise")
    public ModelAndView exercisePage() {
        Iterable<Category> listOfCategories = categoryRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("/create_exercise");
        modelAndView.addObject("categories", listOfCategories);
        return modelAndView;
    }

    @GetMapping("/exercise/select-exercise")
    public ModelAndView showExercises(@RequestParam("category_id") Long categoryId) {

        //logic for getting category_id from selected button

        Iterable<Exercise> listOfExercises = exerciseRepository.findByCategoryId(categoryId);
        Iterable<Category> listOfCategories = categoryRepository.findAll();

        ModelAndView modelAndView = new ModelAndView("/create_exercise");
        modelAndView.addObject("categories", listOfCategories);
        modelAndView.addObject("exercises", listOfExercises);
        modelAndView.addObject("selectedCategoryId", categoryId);

        return modelAndView;
    }



    // NOTE: currentUserId can be found in GlobalControllerAdvice.java

    @PostMapping("/workout/start")
    public RedirectView startWorkout(@ModelAttribute("currentUserId") Long userId) {
        Workout workout = new Workout();
        workout.setUserId(userId);
        workout.setStartedAt(LocalDateTime.now());
        workoutRepository.save(workout);
        return new RedirectView("/exercise"); // this should be "/selectExercise" - need @GetMapping for /selectExercise which should return SelectExercisePage
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
