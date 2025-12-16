package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.repository.ExerciseRepository;
import com.levo.levofitnessapp.repository.WorkoutExerciseRepository;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

// got rid of unnecessary long routes (e.g. /exercise/select-exercise)
// now the /exercise route is configured by adding parameters each time a selection is made
// e.g. when a category is selected -> /exercise?categoryId=1
// and when an exercise is selected -> /exercise?categoryId=1&exerciseId=2
@Controller
@RequestMapping("/workout-exercise") // the base route
public class WorkoutExerciseController {

    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;

    @Autowired
    public WorkoutExerciseController(WorkoutExerciseRepository workoutExerciseRepository,
                                     WorkoutRepository workoutRepository,
                                     ExerciseRepository exerciseRepository) {
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;

    }

    @PostMapping("")
    public ModelAndView saveWorkoutAndExercise(@ModelAttribute("currentUserId") Long userId,
                                       @RequestParam Long exerciseId){

        Workout workout = workoutRepository.findByUserIdAndEndedAtIsNull(userId)
                .orElseThrow(() -> new RuntimeException("No active workout found"));

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        WorkoutExercise workoutExercise = new WorkoutExercise();
        // Even though the fields hold Java objects (Workout and Exercise),
        // Hibernate only stores their primary keys in the database.
        // Give Hibernate full objects, and it looks at the @JoinColumn (e.g. @JoinColumn(name = "workout_session_id)
        // This tells Hibernate to store the primary key of the Workout object inside the workout_session_id column in table
        workoutExercise.setExercise(exercise);
        workoutExercise.setWorkout(workout);

        workoutExerciseRepository.save(workoutExercise);

        return new ModelAndView("redirect:/set?workoutExerciseId=" + workoutExercise.getId());
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
