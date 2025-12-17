package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Category;
import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import com.levo.levofitnessapp.service.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;


import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Load spring context including only WorkoutHistoryController
// Also create mockmvc bean for simulating http requests, without security filters
@WebMvcTest(controllers = WorkoutHistoryController.class,
                excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.levo.levofitnessapp.config.GlobalControllerAdvice.class
        ))
@AutoConfigureMockMvc(addFilters = false)
public class WorkoutHistoryControllerTest {

    // inject mockmvc test client so we can call endpoints
    @Autowired
    private MockMvc mockMvc;

    // mock the repository bean used by the controller
    @MockitoBean
    private WorkoutRepository workoutRepository;

    // mock the current user service bean used by the controller
    @MockitoBean
    private CurrentUserService currentUserService;

    // test the showHistory method without date filter
    @Test
    void showHistoryReturnsHistoryViewWithWorkoutsAndCategories() throws Exception {

        // create a user
        long userId = 1L;

        // create and name a category
        Category cat1 = new Category();
        cat1.setCategoryName("Legs");

        // create two exercises and give them the same category
        Exercise ex1 = new Exercise();
        ex1.setCategory(cat1);
        Exercise ex2 = new Exercise();
        ex2.setCategory(cat1);

        // create two workout exercises using the exercises
        WorkoutExercise we1 = new WorkoutExercise();
        we1.setExercise(ex1);
        WorkoutExercise we2 = new WorkoutExercise();
        we2.setExercise(ex2);

        // create a workout and add the workout exercises, the id, and startedAt timestamp
        Workout w = mock(Workout.class);
        when(w.getId()).thenReturn(10L);
        when(w.getWorkoutExercises()).thenReturn(List.of(we1, we2));
        when(w.getStartedAt()).thenReturn(LocalDateTime.of(2025, 12, 1, 10, 0));

        // stub the repository to return a list of our workouts when queried for the user's workouts
        when(workoutRepository.findAllByUserIdOrderByStartedAtDesc(userId))
                .thenReturn(List.of(w));

        // perform a GET request to /history with the userId as a flash attribute
        mockMvc.perform(get("/history").flashAttr("currentUserId", userId))

                // verify the response
                .andExpect(status().isOk())
                .andExpect(view().name("WorkoutHistoryPage"))
                .andExpect(model().attributeExists("workouts"))
                .andExpect(model().attributeExists("workoutCategories"))
                .andExpect(model().attribute("workouts", hasSize(1)))
                .andExpect(model().attribute("workoutCategories",
                        allOf(
                                hasKey(10L),
                                hasEntry(is(10L), containsInAnyOrder("Legs"))
                        )
                ));

        // verify that the repository method was called once
        verify(workoutRepository).findAllByUserIdOrderByStartedAtDesc(userId);
        // verify that the date-filtered method was not called
        verify(workoutRepository, never()).findAllByUserIdAndStartedAtBetweenOrderByStartedAtDesc(anyLong(), any(), any());
    }

    // test the delete workout button when a workout exists
    @Test
    void deleteWorkout_WhenFound_deletes_andRedirectsToHistory() throws Exception {

        // create a workout to be returned by the repository
        long workoutId = 123L;
        Workout workout = mock(Workout.class);
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(workout));

        // perform POST request to delete the workout
        mockMvc.perform(post("/history/delete/{id}", workoutId))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/history"));

        // verify that the repository methods were called
        verify(workoutRepository).findById(workoutId);
        verify(workoutRepository).delete(workout);

    }

    // test the delete workout button when a workout does not exist
    @Test
    void deleteWorkout_whenNotFound_doesNotDelete_butStillRedirects() throws Exception {

        // stub the repository to return empty when searching for the workout
        long workoutId = 999L;
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.empty());

        // perform POST request to delete the workout
        mockMvc.perform(post("/history/delete/{id}", workoutId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/history"));

        // verify that findById was called but delete was not
        verify(workoutRepository).findById(workoutId);
        verify(workoutRepository, never()).delete(any());
    }

    // test the showDetails method when the workout is found
    @Test
    void showDetails_whenFound_returnsDetailsView_andAddsWorkoutToModel() throws Exception {

        // create a workout to be returned by the repository
        long workoutId = 42L;
        long userId = 1L;
        Workout workout = mock(Workout.class);

        // stub the repository to return the workout when searched by id and user id
        when(workoutRepository.findByIdAndUserIdWithExercises(workoutId, userId))
                .thenReturn(Optional.of(workout));

        // perform GET request to show details
        mockMvc.perform(get("/history/details/{id}", workoutId)
                        .flashAttr("currentUserId", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("WorkoutExerciseHistoryPage"))
                .andExpect(model().attributeExists("workout"))
                .andExpect(model().attribute("workout", sameInstance(workout)));

        // verify that the repository method was called
        verify(workoutRepository).findByIdAndUserIdWithExercises(workoutId, userId);
    }

    // test the showDetails method when the workout is not found
    @Test
    void showDetails_whenNotFound_throwsIllegalArgumentException() throws Exception {

        // stub the repository to return empty when searching for the workout
        long workoutId = 999L;
        long userId = 1L;
        when(workoutRepository.findByIdAndUserIdWithExercises(workoutId, userId))
                .thenReturn(Optional.empty());

        // perform GET request to show details and expect exception
        mockMvc.perform(get("/history/details/{id}", workoutId)
                        .flashAttr("currentUserId", userId))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        org.assertj.core.api.Assertions.assertThat(result.getResolvedException())
                                .isInstanceOf(ResponseStatusException.class)
                                .hasMessageContaining("Workout not found: 999"));

        // verify that the repository method was called
        verify(workoutRepository).findByIdAndUserIdWithExercises(workoutId, userId);
    }

    // test the showCalendar method
    @Test
    void showCalendar_returnsCalendarView_andAddsWorkoutDaysAsStrings() throws Exception {

        // stub the repository to return some workout days
        when(workoutRepository.findWorkoutDays())
                .thenReturn(List.of(Date.valueOf("2025-12-01"), Date.valueOf("2025-12-03")));

        // perform GET request to show calendar
        mockMvc.perform(get("/history/calendar"))
                .andExpect(status().isOk())
                .andExpect(view().name("WorkoutCalendarPage"))
                .andExpect(model().attributeExists("workoutDays"))
                .andExpect(model().attribute("workoutDays", contains("2025-12-01", "2025-12-03")));

        // verify that the repository method was called
        verify(workoutRepository).findWorkoutDays();
    }

}
