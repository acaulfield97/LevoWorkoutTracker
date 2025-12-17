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
<<<<<<< Updated upstream
    }

    /**
     * Add an exercise to the active workout
     */
    @PostMapping
    public ModelAndView saveWorkoutExercise(@ModelAttribute("currentUserId") Long userId,
                                            @RequestParam Long exerciseId) {
=======
        this.setRepository = setRepository;
    }

    @PostMapping("")
    public ModelAndView saveWorkoutAndExercise(@ModelAttribute("currentUserId") Long userId,
                                               @RequestParam Long exerciseId) {
>>>>>>> Stashed changes

        Workout workout = workoutRepository
                .findByUserIdAndEndedAtIsNull(userId)
                .orElseThrow(() -> new RuntimeException("No active workout found"));

        Exercise exercise = exerciseRepository
                .findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        WorkoutExercise workoutExercise = new WorkoutExercise();
<<<<<<< Updated upstream
=======
        workoutExercise.setExercise(exercise);
>>>>>>> Stashed changes
        workoutExercise.setWorkout(workout);
        workoutExercise.setExercise(exercise);

        workoutExerciseRepository.save(workoutExercise);

        return new ModelAndView(
                "redirect:/set?workoutExerciseId=" + workoutExercise.getId()
        );
    }

<<<<<<< Updated upstream
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

=======
    @GetMapping("/{id}")
    public ModelAndView viewWorkoutExercise(@PathVariable Long id) {

        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkoutExercise not found"));

        ModelAndView mav = new ModelAndView("edit_exercise");
        mav.addObject("workoutExercise", workoutExercise);
        return mav;
    }

    @PostMapping("/update")
    public ModelAndView updateWorkoutExerciseNotes(@RequestParam Long id,
                                                   @RequestParam(required = false) String notes) {

        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkoutExercise not found"));

        workoutExercise.setNotes(notes);
        workoutExerciseRepository.save(workoutExercise);

        return new ModelAndView("redirect:/workout-exercise/" + id);
    }

    @PostMapping("/set/update")
    public ModelAndView updateSet(@RequestParam Long setId,
                                  @RequestParam int reps,
                                  @RequestParam int weightKg) {

        Set set = setRepository.findById(setId)
                .orElseThrow(() -> new RuntimeException("Set not found"));

        set.setReps(reps);
        set.setWeightKg(weightKg);
        setRepository.save(set);

        Long workoutExerciseId = set.getWorkoutExerciseId().getId();
        return new ModelAndView("redirect:/workout-exercise/" + workoutExerciseId);
    }

    @PostMapping("/delete/{id}")
    public ModelAndView deleteWorkoutExercise(@PathVariable Long id) {

        workoutExerciseRepository.deleteById(id);
        return new ModelAndView("redirect:/history");
    }
>>>>>>> Stashed changes
}
