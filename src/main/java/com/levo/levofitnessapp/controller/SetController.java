package com.levo.levofitnessapp.controller;

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

@Controller
@RequestMapping("/set")
public class SetController {

    // Repositories
    private final SetRepository setRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    // Constructor
    public SetController(SetRepository setRepository,
                         WorkoutExerciseRepository workoutExerciseRepository) {
        this.setRepository = setRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    // return the create sets page for a specific workout exercise
    @GetMapping("")
    public ModelAndView setPage(@RequestParam Long workoutExerciseId,
                                @RequestParam(required = false) Long setId) {

        // create model and find workout exercise
        ModelAndView mav = new ModelAndView("create_sets");
        WorkoutExercise we = workoutExerciseRepository.findById(workoutExerciseId).orElseThrow();

        // add objects to model - workout exercise, existing sets, and exercise name and id
        mav.addObject("workoutExerciseId", workoutExerciseId);
        mav.addObject("exerciseName", we.getExercise().getExerciseName());
        mav.addObject("exerciseId", we.getExercise().getId());
        mav.addObject("sets", setRepository.findByWorkoutExerciseIdOrderBySetNumberAsc(we));

        // if setId is provided, add the selected set to the model for editing
        if (setId != null) {
            Set selectedSet = setRepository.findById(setId).orElseThrow();
            mav.addObject("selectedSet", selectedSet);
        }

        return mav;
    }


    // Save or update a set
    @PostMapping("")
    public ModelAndView saveSet(@RequestParam Long workoutExerciseId,
                                @RequestParam Long exerciseId,
                                @RequestParam int weightKg,
                                @RequestParam int reps,
                                @RequestParam(required = false) Long setId) { // <-- added setId

        // Find the associated workout exercise
        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("No workout exercise found"));
        Set set;

        // Check if we're updating an existing set or creating a new one
        if (setId != null) {
            // Updating existing set
            set = setRepository.findById(setId)
                    .orElseThrow(() -> new RuntimeException("Set not found"));
            set.setWeightKg(weightKg);
            set.setReps(reps);
            // Optionally, you can leave setNumber as-is
        } else {
            // Creating new set
            set = new Set();
            set.setWorkoutExerciseId(workoutExercise);
            int nextSetNumber = setRepository.countByWorkoutExerciseId(workoutExercise) + 1;
            set.setSetNumber(nextSetNumber);
            set.setWeightKg(weightKg);
            set.setReps(reps);
        }

        // Save the set
        setRepository.save(set);

        return new ModelAndView(
                "redirect:/set?workoutExerciseId=" + workoutExercise.getId()
                        + "&exerciseId=" + workoutExercise.getExercise().getId()
        );
    }

    // Delete a set
    @PostMapping("/delete")
    public ModelAndView deleteSet(@RequestParam Long workoutExerciseId,
                                  @RequestParam Long setId) {

        // Find the workout exercise and the set to delete
        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("WorkoutExercise not found"));
        Set setToDelete = setRepository.findById(setId)
                .orElseThrow(() -> new RuntimeException("Set not found"));

        // Delete the set
        setRepository.delete(setToDelete);

        // Fetch remaining sets in order
        Iterable<Set> remainingSets =
                setRepository.findByWorkoutExerciseIdOrderBySetNumberAsc(workoutExercise);

        // Renumber in order
        int setNumber = 1;
        for (Set set : remainingSets) {
            set.setSetNumber(setNumber++);
        }

        // Save updated sets
        setRepository.saveAll(remainingSets);

        return new ModelAndView(
                "redirect:/set?workoutExerciseId=" + workoutExerciseId
        );
    }


}

