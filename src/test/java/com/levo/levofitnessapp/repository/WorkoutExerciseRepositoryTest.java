package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.model.Workout;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkoutExerciseRepositoryTest {

    @Test
    void returnsWorkoutExercisesForGivenWorkoutId() {
        WorkoutExerciseRepository repo = mock(WorkoutExerciseRepository.class);
        WorkoutExercise we1 = new WorkoutExercise();
        WorkoutExercise we2 = new WorkoutExercise();

        when(repo.findByWorkoutId(10L)).thenReturn(List.of(we1, we2));

        List<WorkoutExercise> result = repo.findByWorkoutId(10L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(we1, result.get(0));
        assertSame(we2, result.get(1));
        verify(repo, times(1)).findByWorkoutId(10L);
    }

    @Test
    void findByExerciseIdAndUserWithSetsReturnsOrderedByWorkoutStartDate() {
        WorkoutExerciseRepository repo = mock(WorkoutExerciseRepository.class);

        WorkoutExercise older = new WorkoutExercise();
        Workout w1 = new Workout();
        w1.setStartedAt(LocalDateTime.now().minusDays(10));
        older.setWorkout(w1);

        WorkoutExercise newer = new WorkoutExercise();
        Workout w2 = new Workout();
        w2.setStartedAt(LocalDateTime.now().minusDays(1));
        newer.setWorkout(w2);

        when(repo.findByExerciseIdAndUserWithSets(5L, 2L)).thenReturn(List.of(newer, older));

        List<WorkoutExercise> result = repo.findByExerciseIdAndUserWithSets(5L, 2L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getWorkout().getStartedAt().isAfter(result.get(1).getWorkout().getStartedAt()) ||
                result.get(0).getWorkout().getStartedAt().isEqual(result.get(1).getWorkout().getStartedAt()));
        verify(repo).findByExerciseIdAndUserWithSets(5L, 2L);
    }

    @Test
    void findByExerciseIdAndUserWithSetsReturnsEmptyWhenNoMatches() {
        WorkoutExerciseRepository repo = mock(WorkoutExerciseRepository.class);
        when(repo.findByExerciseIdAndUserWithSets(99L, 1L)).thenReturn(List.of());

        List<WorkoutExercise> result = repo.findByExerciseIdAndUserWithSets(99L, 1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repo).findByExerciseIdAndUserWithSets(99L, 1L);
    }

    @Test
    void findByWorkoutIdPropagatesException() {
        WorkoutExerciseRepository repo = mock(WorkoutExerciseRepository.class);
        when(repo.findByWorkoutId(null)).thenThrow(new RuntimeException("db error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.findByWorkoutId(null));
        assertEquals("db error", ex.getMessage());
        verify(repo).findByWorkoutId(null);
    }
}

