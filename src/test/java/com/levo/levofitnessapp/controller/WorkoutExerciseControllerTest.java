package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Set;
import com.levo.levofitnessapp.model.User;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.repository.ExerciseRepository;
import com.levo.levofitnessapp.repository.SetRepository;
import com.levo.levofitnessapp.repository.WorkoutExerciseRepository;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import com.levo.levofitnessapp.service.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(WorkoutExerciseController.class)
class WorkoutExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkoutExerciseRepository workoutExerciseRepository;

    @MockBean
    private WorkoutRepository workoutRepository;

    @MockBean
    private ExerciseRepository exerciseRepository;

    @MockBean
    private SetRepository setRepository;

    @MockBean
    private CurrentUserService currentUserService;

    @BeforeEach
    void setupCurrentUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test-user");
        when(currentUserService.getCurrentUser(any())).thenReturn(user);
    }

    @Test
    void createWorkoutExercise_redirectsToSet() throws Exception {
        Long userId = 1L;
        Long exerciseId = 2L;
        Long savedId = 42L;

        Workout workout = new Workout();
        // workout id has no setter (AccessLevel.NONE), not needed for the controller

        Exercise exercise = new Exercise();
        exercise.setId(exerciseId);

        // Make repository return the active workout and the exercise
        when(workoutRepository.findByUserIdAndEndedAtIsNull(userId)).thenReturn(Optional.of(workout));
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

        // Simulate save sets an id on the entity and returns it
        when(workoutExerciseRepository.save(any(WorkoutExercise.class))).thenAnswer(invocation -> {
            WorkoutExercise we = invocation.getArgument(0);
            we.setId(savedId);
            return we;
        });

        mockMvc.perform(post("/workout-exercise")
                        .requestAttr("currentUserId", userId)
                        .param("exerciseId", String.valueOf(exerciseId))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/set?workoutExerciseId=" + savedId));

        verify(workoutExerciseRepository).save(any(WorkoutExercise.class));
    }

    @Test
    void viewWorkoutExercise_showsEditExerciseView() throws Exception {
        Long id = 10L;
        WorkoutExercise we = new WorkoutExercise();
        we.setId(id);

        // ensure nested exercise is present so Thymeleaf expressions (exercise.exerciseName) don't NPE
        Exercise exercise = new Exercise();
        exercise.setId(5L);
        exercise.setExerciseName("Squat");
        we.setExercise(exercise);

        when(workoutExerciseRepository.findById(id)).thenReturn(Optional.of(we));

        mockMvc.perform(get("/workout-exercise/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("edit_exercise"))
                .andExpect(model().attributeExists("workoutExercise"));
    }

    @Test
    void updateWorkoutExerciseNotes_redirectsBack() throws Exception {
        Long id = 11L;
        String notes = "some notes";
        WorkoutExercise we = new WorkoutExercise();
        we.setId(id);

        when(workoutExerciseRepository.findById(id)).thenReturn(Optional.of(we));

        mockMvc.perform(post("/workout-exercise/update")
                        .param("id", String.valueOf(id))
                        .param("notes", notes)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/workout-exercise/" + id));

        // verify the repository save was called and the entity was updated
        verify(workoutExerciseRepository).save(any(WorkoutExercise.class));
    }

    @Test
    void updateSet_redirectsToWorkoutExercise() throws Exception {
        Long setId = 21L;
        Long weId = 22L;

        // Use the domain Set (not java.util.Set)
        com.levo.levofitnessapp.model.Set set = new com.levo.levofitnessapp.model.Set();
        set.setId(setId);
        WorkoutExercise we = new WorkoutExercise();
        we.setId(weId);

        set.setWorkoutExerciseId(we);
        set.setReps(5);
        set.setWeightKg(50);

        when(setRepository.findById(setId)).thenReturn(Optional.of(set));

        mockMvc.perform(post("/workout-exercise/set/update")
                        .param("setId", String.valueOf(setId))
                        .param("reps", String.valueOf(8))
                        .param("weightKg", String.valueOf(60))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/workout-exercise/" + weId));

        verify(setRepository).save(any(com.levo.levofitnessapp.model.Set.class));
    }

    @Test
    void deleteWorkoutExercise_redirectsToHistory() throws Exception {
        Long id = 33L;

        mockMvc.perform(post("/workout-exercise/delete/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/history"));

        verify(workoutExerciseRepository).deleteById(eq(id));
    }
}
