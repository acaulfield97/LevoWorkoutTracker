package com.levo.levofitnessapp.controller;

import ch.qos.logback.core.model.Model;
import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Set;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.repository.SetRepository;
import com.levo.levofitnessapp.repository.WorkoutExerciseRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/set")
public class SetController {

    private final SetRepository setRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public SetController(SetRepository setRepository,
                         WorkoutExerciseRepository workoutExerciseRepository) {
        this.setRepository = setRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    @GetMapping("")
    public ModelAndView setPage(@RequestParam Long workoutExerciseId) {

        ModelAndView modelAndView = new ModelAndView("/create_sets");
        modelAndView.addObject("workoutExerciseId", workoutExerciseId);

        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("No workout exercise found"));

        // Add exercise name
        Exercise exercise = workoutExercise.getExerciseId();
        modelAndView.addObject("exerciseName", exercise.getExercise_name());

        Iterable<Set> sets = setRepository.findByWorkoutExerciseId(workoutExercise);
        modelAndView.addObject("sets", sets);

        return modelAndView;
    }

    @PostMapping("")
    public ModelAndView saveSet(@RequestParam Long workoutExerciseId,
                                @RequestParam int weightKg,
                                @RequestParam int reps){

        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("No workout exercise found"));

        // Calculate next set number automatically
        int nextSetNumber = setRepository.countByWorkoutExerciseId(workoutExercise) + 1;

        Set set = new Set();

        set.setWorkoutExerciseId(workoutExercise);
        set.setSetNumber(nextSetNumber);
        set.setWeightKg(weightKg);
        set.setReps(reps);

        setRepository.save(set);

        return new ModelAndView("redirect:/set?workoutExerciseId=" + workoutExercise.getId());
    }



}
