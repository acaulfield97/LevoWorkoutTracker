package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;


public class ExerciseController {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @PostMapping("/add-exercise")
    public RedirectView addExercise(Exercise exercise){



        exerciseRepository.save(exercise);
        return new RedirectView("/exercise-set");
    }

}
