package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Category;
import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.repository.CategoryRepository;
import com.levo.levofitnessapp.repository.ExerciseRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class StaticPageController {

    CategoryRepository categoryRepository;
    ExerciseRepository exerciseRepository;

    public StaticPageController(CategoryRepository categoryRepository, ExerciseRepository exerciseRepository) {
        this.categoryRepository = categoryRepository;
        this.exerciseRepository = exerciseRepository;
    }

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


}
