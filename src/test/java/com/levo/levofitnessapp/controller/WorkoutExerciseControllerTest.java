package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.repository.ExerciseRepository;
import com.levo.levofitnessapp.repository.SetRepository;
import com.levo.levofitnessapp.repository.WorkoutExerciseRepository;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = WorkoutExerciseController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.levo.levofitnessapp.config.GlobalControllerAdvice.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class WorkoutExerciseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    WorkoutExerciseRepository workoutExerciseRepository;

    @MockitoBean
    WorkoutRepository workoutRepository;

    @MockitoBean
    ExerciseRepository exerciseRepository;

    @MockitoBean
    SetRepository setRepository;

    // Test saving a workout and exercise when an active workout and exercise exist
    @Test
    void saveWorkoutAndExercise_whenActiveWorkoutAndExerciseExist_savesAndRedirectsToSet() throws Exception {

        long userId = 1L;
        long exerciseId = 7L;
        Workout activeWorkout = new Workout();
        Exercise exercise = new Exercise();

        // Mock repository methods to return active workout and exercise
        when(workoutRepository.findByUserIdAndEndedAtIsNull(userId))
                .thenReturn(Optional.of(activeWorkout));
        when(exerciseRepository.findById(exerciseId))
                .thenReturn(Optional.of(exercise));

        // Mock saving the WorkoutExercise to return an object with ID 99
        when(workoutExerciseRepository.save(any(WorkoutExercise.class)))
                .thenAnswer(inv -> {
                    WorkoutExercise we = inv.getArgument(0);
                    ReflectionTestUtils.setField(we, "id", 99L);
                    return we;
                });

        // Perform POST request to save workout and exercise
        mockMvc.perform(post("/workout-exercise")
                        .flashAttr("currentUserId", userId)
                        .param("exerciseId", String.valueOf(exerciseId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/set?workoutExerciseId=99"));

        // Verify that the repository methods were called
        verify(workoutRepository).findByUserIdAndEndedAtIsNull(userId);
        verify(exerciseRepository).findById(exerciseId);
        verify(workoutExerciseRepository).save(any(WorkoutExercise.class));
    }

    // Test deleting a workout exercise when it exists
    @Test
    void deleteWorkoutExercise_whenFound_deletesAndRedirectsToExercise() throws Exception {
        long workoutExerciseId = 55L;

        // Mock repository method to return a WorkoutExercise
        WorkoutExercise we = mock(WorkoutExercise.class);
        when(workoutExerciseRepository.findById(workoutExerciseId))
                .thenReturn(Optional.of(we));

        // Perform POST request to delete workout exercise
        mockMvc.perform(post("/workout-exercise/delete")
                        .param("workoutExerciseId", String.valueOf(workoutExerciseId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exercise"));

        // Verify that the repository methods were called
        verify(workoutExerciseRepository).findById(workoutExerciseId);
        verify(workoutExerciseRepository).delete(we);
    }

}
