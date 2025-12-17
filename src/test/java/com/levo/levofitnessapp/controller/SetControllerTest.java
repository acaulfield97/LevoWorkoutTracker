package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Set;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.repository.SetRepository;
import com.levo.levofitnessapp.repository.WorkoutExerciseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class SetControllerTest {

    @Test
    void returnsCreateSetsViewWithSetsAndSelectedSetWhenSetIdProvided() {
        SetRepository setRepository = mock(SetRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);

        SetController controller = new SetController(setRepository, workoutExerciseRepository);

        Long workoutExerciseId = 10L;
        Long setId = 5L;

        Exercise exercise = new Exercise();
        exercise.setExerciseName("Squat");
        exercise.setId(2L);

        WorkoutExercise we = new WorkoutExercise();
        we.setId(workoutExerciseId);
        we.setExercise(exercise);

        Set s1 = new Set(1L, we, 1, 100, 5);
        Set selected = new Set(setId, we, 2, 110, 3);

        when(workoutExerciseRepository.findById(workoutExerciseId)).thenReturn(Optional.of(we));
        when(setRepository.findByWorkoutExerciseIdOrderBySetNumberAsc(we)).thenReturn(List.of(s1, selected));
        when(setRepository.findById(setId)).thenReturn(Optional.of(selected));

        ModelAndView mv = controller.setPage(workoutExerciseId, setId);

        assertEquals("create_sets", mv.getViewName());
        assertEquals(workoutExerciseId, mv.getModel().get("workoutExerciseId"));
        assertEquals("Squat", mv.getModel().get("exerciseName"));
        assertEquals(2L, mv.getModel().get("exerciseId"));
        assertEquals(List.of(s1, selected), mv.getModel().get("sets"));
        assertSame(selected, mv.getModel().get("selectedSet"));
    }

    @Test
    void setPageThrowsWhenWorkoutExerciseMissing() {
        SetRepository setRepository = mock(SetRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);

        SetController controller = new SetController(setRepository, workoutExerciseRepository);

        when(workoutExerciseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> controller.setPage(99L, null));
    }

    @Test
    void saveSetCreatesNewSetWhenNoSetIdAndRedirectsWithIds() {
        SetRepository setRepository = mock(SetRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);

        SetController controller = new SetController(setRepository, workoutExerciseRepository);

        Long workoutExerciseId = 20L;
        Long exerciseId = 7L;
        int weight = 85;
        int reps = 8;

        Exercise exercise = new Exercise();
        exercise.setId(exerciseId);
        exercise.setExerciseName("Bench");

        WorkoutExercise we = new WorkoutExercise();
        we.setId(workoutExerciseId);
        we.setExercise(exercise);

        when(workoutExerciseRepository.findById(workoutExerciseId)).thenReturn(Optional.of(we));
        when(setRepository.countByWorkoutExerciseId(we)).thenReturn(2);

        ModelAndView mv = controller.saveSet(workoutExerciseId, exerciseId, weight, reps, null);

        String view = mv.getViewName();
        assertTrue(view.startsWith("redirect:/set?workoutExerciseId=" + workoutExerciseId));
        verify(setRepository, times(1)).save(argThat(obj -> {
            Set s = (Set) obj;
            return s.getWorkoutExerciseId() == we && s.getSetNumber() == 3 && s.getWeightKg() == weight && s.getReps() == reps;
        }));
    }

    @Test
    void saveSetUpdatesExistingSetWhenSetIdProvided() {
        SetRepository setRepository = mock(SetRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);

        SetController controller = new SetController(setRepository, workoutExerciseRepository);

        Long workoutExerciseId = 30L;
        Long exerciseId = 4L;
        Long setId = 12L;
        int newWeight = 60;
        int newReps = 12;

        Exercise exercise = new Exercise();
        exercise.setId(exerciseId);

        WorkoutExercise we = new WorkoutExercise();
        we.setId(workoutExerciseId);
        we.setExercise(exercise);

        Set existing = new Set(setId, we, 2, 55, 10);

        when(workoutExerciseRepository.findById(workoutExerciseId)).thenReturn(Optional.of(we));
        when(setRepository.findById(setId)).thenReturn(Optional.of(existing));

        ModelAndView mv = controller.saveSet(workoutExerciseId, exerciseId, newWeight, newReps, setId);

        assertTrue(mv.getViewName().contains("redirect:/set?workoutExerciseId=" + workoutExerciseId));
        verify(setRepository, times(1)).save(existing);
        assertEquals(newWeight, existing.getWeightKg());
        assertEquals(newReps, existing.getReps());
    }

    @Test
    void saveSetThrowsWhenWorkoutExerciseMissing() {
        SetRepository setRepository = mock(SetRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);

        SetController controller = new SetController(setRepository, workoutExerciseRepository);

        when(workoutExerciseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> controller.saveSet(999L, 1L, 10, 10, null));
    }

    @Test
    void deleteSetDeletesAndRenumbersRemainingSetsAndRedirects() {
        SetRepository setRepository = mock(SetRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);

        SetController controller = new SetController(setRepository, workoutExerciseRepository);

        Long workoutExerciseId = 40L;
        Long setIdToDelete = 21L;

        Exercise exercise = new Exercise();
        exercise.setId(2L);

        WorkoutExercise we = new WorkoutExercise();
        we.setId(workoutExerciseId);
        we.setExercise(exercise);

        Set toDelete = new Set(setIdToDelete, we, 2, 70, 6);
        Set remaining1 = new Set(31L, we, 1, 60, 8);
        Set remaining2 = new Set(32L, we, 3, 80, 5);

        when(workoutExerciseRepository.findById(workoutExerciseId)).thenReturn(Optional.of(we));
        when(setRepository.findById(setIdToDelete)).thenReturn(Optional.of(toDelete));
        when(setRepository.findByWorkoutExerciseIdOrderBySetNumberAsc(we)).thenReturn(List.of(remaining1, remaining2));

        ModelAndView mv = controller.deleteSet(workoutExerciseId, setIdToDelete);

        assertTrue(mv.getViewName().startsWith("redirect:/set?workoutExerciseId=" + workoutExerciseId));
        verify(setRepository, times(1)).delete(toDelete);
        verify(setRepository, times(1)).saveAll(argThat(iter -> {
            int i = 0;
            for (Object o : (Iterable<?>) iter) {
                Set s = (Set) o;
                i++;
                if (s.getId().equals(remaining1.getId()) && s.getSetNumber() != 1) return false;
                if (s.getId().equals(remaining2.getId()) && s.getSetNumber() != 2) return false;
            }
            return i == 2;
        }));
    }

    @Test
    void deleteSetThrowsWhenWorkoutExerciseMissing() {
        SetRepository setRepository = mock(SetRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);

        SetController controller = new SetController(setRepository, workoutExerciseRepository);

        when(workoutExerciseRepository.findById(777L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> controller.deleteSet(777L, 1L));
    }

    @Test
    void deleteSetThrowsWhenSetMissing() {
        SetRepository setRepository = mock(SetRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);

        SetController controller = new SetController(setRepository, workoutExerciseRepository);

        Long workoutExerciseId = 88L;
        Long missingSetId = 999L;

        WorkoutExercise we = new WorkoutExercise();
        we.setId(workoutExerciseId);

        when(workoutExerciseRepository.findById(workoutExerciseId)).thenReturn(Optional.of(we));
        when(setRepository.findById(missingSetId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> controller.deleteSet(workoutExerciseId, missingSetId));
    }
}
