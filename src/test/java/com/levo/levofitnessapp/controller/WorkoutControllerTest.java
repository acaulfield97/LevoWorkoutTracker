package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Test only the WorkoutController, mocking out other components
@WebMvcTest(
        controllers = WorkoutController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.levo.levofitnessapp.config.GlobalControllerAdvice.class
        )
)
// Disable security filters for simplicity
@AutoConfigureMockMvc(addFilters = false)
class WorkoutControllerTest {

    // MockMvc to simulate HTTP requests
    @Autowired
    private MockMvc mockMvc;

    // Mocked WorkoutRepository
    @MockitoBean
    private WorkoutRepository workoutRepository;

    // Test starting a workout
    @Test
    void startWorkout_createsWorkout_savesAndRedirectsToExercise() throws Exception {

        // Simulate a user with ID 1 starting a workout
        long userId = 1L;
        when(workoutRepository.save(any(Workout.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Perform POST request to start workout
        mockMvc.perform(post("/workout/start")
                        .flashAttr("currentUserId", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exercise"));

        // Capture the Workout that was saved so we can assert fields were set
        var workoutCaptor = org.mockito.ArgumentCaptor.forClass(Workout.class);
        verify(workoutRepository).save(workoutCaptor.capture());

        // Assert that the saved workout has the correct userId and a non-null startedAt
        Workout saved = workoutCaptor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getStartedAt()).isNotNull();
    }

    // Test finishing a workout when an active workout exists
    @Test
    void finishWorkout_whenActiveWorkoutExists_setsEndedAt_savesAndRedirectsHome() throws Exception {

        // Simulate a user with ID 1 finishing a workout
        long userId = 1L;
        Workout active = new Workout();
        active.setUserId(userId);

        // Mock repository to return an active workout
        when(workoutRepository.findByUserIdAndEndedAtIsNull(userId))
                .thenReturn(Optional.of(active));
        when(workoutRepository.save(any(Workout.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Perform POST request to finish workout
        mockMvc.perform(post("/workout/finish")
                        .flashAttr("currentUserId", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(workoutRepository).findByUserIdAndEndedAtIsNull(userId);

        // Capture the Workout that was saved so we can assert endedAt was set
        var workoutCaptor = org.mockito.ArgumentCaptor.forClass(Workout.class);
        verify(workoutRepository).save(workoutCaptor.capture());

        // Assert that the saved workout has a non-null endedAt
        Workout saved = workoutCaptor.getValue();
        assertThat(saved.getEndedAt()).isNotNull();
    }

    // Test finishing a workout when no active workout exists
    @Test
    void finishWorkout_whenNoActiveWorkout_doesNotSave_butStillRedirectsHome() throws Exception {

        // Simulate a user with ID 1 finishing a workout
        long userId = 1L;
        when(workoutRepository.findByUserIdAndEndedAtIsNull(userId))
                .thenReturn(Optional.empty());

        // Perform POST request to finish workout
        mockMvc.perform(post("/workout/finish")
                        .flashAttr("currentUserId", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // Verify that no save was attempted since there was no active workout
        verify(workoutRepository).findByUserIdAndEndedAtIsNull(userId);
        verify(workoutRepository, never()).save(any(Workout.class));
    }


    // Test deleting a workout when it exists
    @Test
    void deleteWorkout_whenFound_deletesAndRedirectsHome() throws Exception {

        // Simulate deleting a workout with ID 10
        long workoutId = 10L;
        Workout w = mock(Workout.class);
        when(workoutRepository.findById(workoutId))
                .thenReturn(Optional.of(w));

        // Perform POST request to delete workout
        mockMvc.perform(post("/workout/delete/{id}", workoutId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // Verify that the workout was found and deleted
        verify(workoutRepository).findById(workoutId);
        verify(workoutRepository).delete(w);
    }


    // Test deleting a workout when it does not exist
    @Test
    void deleteWorkout_whenNotFound_doesNotDelete_butStillRedirectsHome() throws Exception {

        // Simulate deleting a workout with ID 999 that does not exist
        long workoutId = 999L;
        when(workoutRepository.findById(workoutId))
                .thenReturn(Optional.empty());

        // Perform POST request to delete workout
        mockMvc.perform(post("/workout/delete/{id}", workoutId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // Verify that no delete was attempted since the workout was not found
        verify(workoutRepository).findById(workoutId);
        verify(workoutRepository, never()).delete(any());
    }
}
