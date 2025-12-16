package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.dto.ExercisePoint;
import com.levo.levofitnessapp.model.Set;
import com.levo.levofitnessapp.model.WorkoutExercise;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SetRepositoryTest {

    @Test
    void returnsCountForGivenWorkoutExercise() {
        SetRepository repo = mock(SetRepository.class);
        WorkoutExercise we = new WorkoutExercise();

        when(repo.countByWorkoutExerciseId(we)).thenReturn(4);

        int count = repo.countByWorkoutExerciseId(we);

        assertEquals(4, count);
        verify(repo, times(1)).countByWorkoutExerciseId(we);
    }

    @Test
    void returnsOrderedSetsForWorkoutExercise() {
        SetRepository repo = mock(SetRepository.class);
        WorkoutExercise we = new WorkoutExercise();

        Set s1 = new Set(1L, we, 1, 50, 10);
        Set s2 = new Set(2L, we, 2, 60, 8);

        when(repo.findByWorkoutExerciseIdOrderBySetNumberAsc(we)).thenReturn(List.of(s1, s2));

        Iterable<Set> result = repo.findByWorkoutExerciseIdOrderBySetNumberAsc(we);

        assertNotNull(result);
        List<Set> list = (List<Set>) result;
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).getSetNumber());
        assertEquals(2, list.get(1).getSetNumber());
        verify(repo).findByWorkoutExerciseIdOrderBySetNumberAsc(we);
    }

    @Test
    void findExerciseWeightPointsReturnsPointsOrderedByDate() {
        SetRepository repo = mock(SetRepository.class);

        long exerciseId = 11L;
        ExercisePoint p1 = new ExercisePoint(LocalDateTime.of(2020,1,1,0,0), 50);
        ExercisePoint p2 = new ExercisePoint(LocalDateTime.of(2020,2,1,0,0), 60);

        when(repo.findExerciseWeightPoints(exerciseId)).thenReturn(List.of(p1, p2));

        List<ExercisePoint> points = repo.findExerciseWeightPoints(exerciseId);

        assertNotNull(points);
        assertEquals(2, points.size());
        assertEquals(p1, points.get(0));
        assertEquals(p2, points.get(1));
        assertTrue(points.get(0).date().isBefore(points.get(1).date()) || points.get(0).date().isEqual(points.get(1).date()));
        verify(repo).findExerciseWeightPoints(exerciseId);
    }

    @Test
    void findExerciseWeightPointsReturnsEmptyWhenNoData() {
        SetRepository repo = mock(SetRepository.class);
        when(repo.findExerciseWeightPoints(999L)).thenReturn(List.of());

        List<ExercisePoint> points = repo.findExerciseWeightPoints(999L);

        assertNotNull(points);
        assertTrue(points.isEmpty());
        verify(repo).findExerciseWeightPoints(999L);
    }

    @Test
    void findExerciseWeightPointsPropagatesException() {
        SetRepository repo = mock(SetRepository.class);
        when(repo.findExerciseWeightPoints(null)).thenThrow(new RuntimeException("db error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.findExerciseWeightPoints(null));
        assertEquals("db error", ex.getMessage());
        verify(repo).findExerciseWeightPoints(null);
    }
}

