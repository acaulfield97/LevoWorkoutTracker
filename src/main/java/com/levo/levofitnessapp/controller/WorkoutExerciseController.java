package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Set;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.repository.ExerciseRepository;
import com.levo.levofitnessapp.repository.SetRepository;
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
    private final SetRepository setRepository;

    public WorkoutExerciseController(WorkoutExerciseRepository workoutExerciseRepository,
                                     WorkoutRepository workoutRepository,
                                     ExerciseRepository exerciseRepository,
                                     SetRepository setRepository) {
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.setRepository = setRepository;
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

    @GetMapping("/{id}")
    public ModelAndView viewWorkoutExercise(@PathVariable Long id,
                                            @RequestParam(required = false) Long setId) {

        WorkoutExercise we = workoutExerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkoutExercise not found"));

        Workout workout = we.getWorkout();

        ModelAndView mav = new ModelAndView("EditWorkoutExercisePage");

        // Basic workoutExercise info
        mav.addObject("workoutExercise", we);
        mav.addObject("workout", we.getWorkout());
        mav.addObject("workoutExerciseId", we.getId());
        mav.addObject("exerciseName", we.getExercise().getExerciseName());
        mav.addObject("exerciseId", we.getExercise().getId());

        // Fetch all sets for this workoutExercise
        mav.addObject("sets", setRepository.findByWorkoutExerciseIdOrderBySetNumberAsc(we));

        // If editing a specific set
        if (setId != null) {
            Set selectedSet = setRepository.findById(setId).orElseThrow(() -> new RuntimeException("Set not found"));
            mav.addObject("selectedSet", selectedSet);
        }

        mav.addObject("completedDate", workout.getEndedAt());

        return mav;
    }

    
    @PostMapping("/delete")
    public ModelAndView deleteWorkoutExercise(
            @RequestParam Long workoutExerciseId) {

        WorkoutExercise workoutExercise =
                workoutExerciseRepository.findById(workoutExerciseId)
                        .orElseThrow(() -> new RuntimeException("WorkoutExercise not found"));

        workoutExerciseRepository.delete(workoutExercise);

        return new ModelAndView("redirect:/exercise");
    }

}
