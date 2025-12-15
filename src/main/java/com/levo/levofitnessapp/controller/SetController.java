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

    private final SetRepository setRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public SetController(SetRepository setRepository,
                         WorkoutExerciseRepository workoutExerciseRepository) {
        this.setRepository = setRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    @GetMapping("")
    public ModelAndView setPage(@RequestParam Long workoutExerciseId,
                                @RequestParam(required = false) Long setId) {

        ModelAndView mav = new ModelAndView("create_sets");

        WorkoutExercise we = workoutExerciseRepository.findById(workoutExerciseId).orElseThrow();

        mav.addObject("workoutExerciseId", workoutExerciseId);
        mav.addObject("exerciseName", we.getExercise().getExerciseName());
        mav.addObject("exerciseId", we.getExercise().getId());
        mav.addObject("sets", setRepository.findByWorkoutExerciseIdOrderBySetNumberAsc(we));


        if (setId != null) {
            Set selectedSet = setRepository.findById(setId).orElseThrow();
            mav.addObject("selectedSet", selectedSet);
        }

        return mav;
    }

    @PostMapping("")
    public ModelAndView saveSet(@RequestParam Long workoutExerciseId,
                                @RequestParam Long exerciseId,
                                @RequestParam int weightKg,
                                @RequestParam int reps,
                                @RequestParam(required = false) Long setId) { // <-- added setId

        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("No workout exercise found"));

        Set set;

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

        setRepository.save(set);

        return new ModelAndView(
                "redirect:/set?workoutExerciseId=" + workoutExercise.getId()
                        + "&exerciseId=" + workoutExercise.getExercise().getId()
        );
    }

    @PostMapping("/delete")
    public ModelAndView deleteSet(@RequestParam Long workoutExerciseId,
                                  @RequestParam Long setId) {

        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(workoutExerciseId)
                .orElseThrow(() -> new RuntimeException("WorkoutExercise not found"));

        Set setToDelete = setRepository.findById(setId)
                .orElseThrow(() -> new RuntimeException("Set not found"));

        setRepository.delete(setToDelete);

        // Fetch remaining sets in order
        Iterable<Set> remainingSets =
                setRepository.findByWorkoutExerciseIdOrderBySetNumberAsc(workoutExercise);

        // Renumber in order
        int setNumber = 1;
        for (Set set : remainingSets) {
            set.setSetNumber(setNumber++);
        }

        setRepository.saveAll(remainingSets);

        return new ModelAndView(
                "redirect:/set?workoutExerciseId=" + workoutExerciseId
        );
    }


}

