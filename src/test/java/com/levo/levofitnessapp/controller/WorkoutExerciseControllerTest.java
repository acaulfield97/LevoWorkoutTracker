package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Set;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.repository.ExerciseRepository;
import com.levo.levofitnessapp.repository.SetRepository;
import com.levo.levofitnessapp.repository.WorkoutExerciseRepository;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WorkoutExerciseControllerTest {

    @Test
    void redirectsToSetWhenActiveWorkoutExistsAndExerciseExists() {
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        Workout workout = new Workout();
        try {
            java.lang.reflect.Field workoutIdField = Workout.class.getDeclaredField("id");
            workoutIdField.setAccessible(true);
            workoutIdField.set(workout, 3L);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        Exercise exercise = new Exercise();
        exercise.setId(5L);

        WorkoutExercise saved = new WorkoutExercise();
        saved.setId(7L);
        saved.setWorkout(workout);
        saved.setExercise(exercise);

        when(workoutRepository.findByUserIdAndEndedAtIsNull(42L)).thenReturn(Optional.of(workout));
        when(exerciseRepository.findById(5L)).thenReturn(Optional.of(exercise));
        when(workoutExerciseRepository.save(any(WorkoutExercise.class))).thenAnswer(invocation -> {
            WorkoutExercise arg = invocation.getArgument(0);
            arg.setId(7L);
            return arg;
        });

        WorkoutExerciseController controller = new WorkoutExerciseController(workoutExerciseRepository, workoutRepository, exerciseRepository, setRepository);

        ModelAndView mav = controller.saveWorkoutExercise(42L, 5L);

        assertEquals("redirect:/set?workoutExerciseId=7", mav.getViewName());
        verify(workoutExerciseRepository).save(any(WorkoutExercise.class));
    }

    @Test
    void throwsWhenNoActiveWorkoutFoundOnSave() {
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        when(workoutRepository.findByUserIdAndEndedAtIsNull(1L)).thenReturn(Optional.empty());

        WorkoutExerciseController controller = new WorkoutExerciseController(workoutExerciseRepository, workoutRepository, exerciseRepository, setRepository);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.saveWorkoutExercise(1L, 2L));
        assertEquals("No active workout found", ex.getMessage());
        verifyNoInteractions(exerciseRepository);
        verifyNoInteractions(workoutExerciseRepository);
    }

    @Test
    void throwsWhenExerciseNotFoundOnSave() {
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        Workout workout = new Workout();
        when(workoutRepository.findByUserIdAndEndedAtIsNull(2L)).thenReturn(Optional.of(workout));
        when(exerciseRepository.findById(99L)).thenReturn(Optional.empty());

        WorkoutExerciseController controller = new WorkoutExerciseController(workoutExerciseRepository, workoutRepository, exerciseRepository, setRepository);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.saveWorkoutExercise(2L, 99L));
        assertEquals("Exercise not found", ex.getMessage());
        verify(workoutExerciseRepository, never()).save(any());
    }

    @Test
    void viewWorkoutExerciseReturnsModelAndViewWithAllAttributesWhenNoSetSelected() {
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        Workout workout = new Workout();
        try {
            java.lang.reflect.Field workoutIdField = Workout.class.getDeclaredField("id");
            workoutIdField.setAccessible(true);
            workoutIdField.set(workout, 11L);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        workout.setEndedAt(null);

        Exercise exercise = new Exercise();
        exercise.setId(12L);
        exercise.setExerciseName("Squat");

        WorkoutExercise we = new WorkoutExercise();
        we.setId(9L);
        we.setWorkout(workout);
        we.setExercise(exercise);

        Set s1 = new Set();
        s1.setId(1L);
        Set s2 = new Set();
        s2.setId(2L);
        List<Set> sets = Arrays.asList(s1, s2);

        when(workoutExerciseRepository.findById(9L)).thenReturn(Optional.of(we));
        when(setRepository.findByWorkoutExerciseIdOrderBySetNumberAsc(we)).thenReturn(sets);

        WorkoutExerciseController controller = new WorkoutExerciseController(workoutExerciseRepository, workoutRepository, exerciseRepository, setRepository);

        ModelAndView mav = controller.viewWorkoutExercise(9L, null);

        assertEquals("EditWorkoutExercisePage", mav.getViewName());
        assertSame(we, mav.getModel().get("workoutExercise"));
        assertSame(workout, mav.getModel().get("workout"));
        assertEquals(9L, mav.getModel().get("workoutExerciseId"));
        assertEquals("Squat", mav.getModel().get("exerciseName"));
        assertEquals(12L, mav.getModel().get("exerciseId"));
        assertSame(sets, mav.getModel().get("sets"));
        assertNull(mav.getModel().get("selectedSet"));
        assertNull(mav.getModel().get("completedDate"));
    }

    @Test
    void viewWorkoutExerciseIncludesSelectedSetWhenSetIdProvided() {
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        Workout workout = new Workout();
        try {
            java.lang.reflect.Field workoutIdField = Workout.class.getDeclaredField("id");
            workoutIdField.setAccessible(true);
            workoutIdField.set(workout, 21L);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        workout.setEndedAt(LocalDateTime.now());

        Exercise exercise = new Exercise();
        exercise.setId(22L);
        exercise.setExerciseName("Bench");

        WorkoutExercise we = new WorkoutExercise();
        we.setId(19L);
        we.setWorkout(workout);
        we.setExercise(exercise);

        Set selected = new Set();
        selected.setId(20L);

        when(workoutExerciseRepository.findById(19L)).thenReturn(Optional.of(we));
        when(setRepository.findByWorkoutExerciseIdOrderBySetNumberAsc(we)).thenReturn(Arrays.asList());
        when(setRepository.findById(20L)).thenReturn(Optional.of(selected));

        WorkoutExerciseController controller = new WorkoutExerciseController(workoutExerciseRepository, workoutRepository, exerciseRepository, setRepository);

        ModelAndView mav = controller.viewWorkoutExercise(19L, 20L);

        assertEquals("EditWorkoutExercisePage", mav.getViewName());
        assertSame(selected, mav.getModel().get("selectedSet"));
        assertEquals(workout.getEndedAt(), mav.getModel().get("completedDate"));
    }

    @Test
    void viewWorkoutExerciseThrowsWhenNotFound() {
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        when(workoutExerciseRepository.findById(5L)).thenReturn(Optional.empty());

        WorkoutExerciseController controller = new WorkoutExerciseController(workoutExerciseRepository, workoutRepository, exerciseRepository, setRepository);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.viewWorkoutExercise(5L, null));
        assertEquals("WorkoutExercise not found", ex.getMessage());
    }

    @Test
    void deleteWorkoutExerciseDeletesAndRedirects() {
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        WorkoutExercise we = new WorkoutExercise();
        we.setId(33L);

        when(workoutExerciseRepository.findById(33L)).thenReturn(Optional.of(we));

        WorkoutExerciseController controller = new WorkoutExerciseController(workoutExerciseRepository, workoutRepository, exerciseRepository, setRepository);

        ModelAndView mav = controller.deleteWorkoutExercise(33L);

        verify(workoutExerciseRepository).delete(we);
        assertEquals("redirect:/exercise", mav.getViewName());
    }

    @Test
    void deleteWorkoutExerciseThrowsWhenNotFound() {
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        when(workoutExerciseRepository.findById(44L)).thenReturn(Optional.empty());

        WorkoutExerciseController controller = new WorkoutExerciseController(workoutExerciseRepository, workoutRepository, exerciseRepository, setRepository);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.deleteWorkoutExercise(44L));
        assertEquals("WorkoutExercise not found", ex.getMessage());
    }
}
