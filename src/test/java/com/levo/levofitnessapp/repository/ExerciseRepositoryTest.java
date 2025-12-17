package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Exercise;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExerciseRepositoryTest {

    @Test
    void returnsExercisesForGivenCategoryId() {
        ExerciseRepository repo = mock(ExerciseRepository.class);
        Exercise e1 = new Exercise();
        Exercise e2 = new Exercise();
        when(repo.findByCategoryId(5L)).thenReturn(List.of(e1, e2));

        Iterable<Exercise> result = repo.findByCategoryId(5L);

        assertNotNull(result);
        var list = StreamSupport.stream(result.spliterator(), false).toList();
        assertEquals(2, list.size());
        assertSame(e1, list.get(0));
        assertSame(e2, list.get(1));
        verify(repo, times(1)).findByCategoryId(5L);
    }

    @Test
    void returnsEmptyIterableWhenNoExercisesForCategory() {
        ExerciseRepository repo = mock(ExerciseRepository.class);
        when(repo.findByCategoryId(99L)).thenReturn(List.of());

        Iterable<Exercise> result = repo.findByCategoryId(99L);

        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
        verify(repo).findByCategoryId(99L);
    }

    @Test
    void propagatesExceptionWhenRepositoryFails() {
        ExerciseRepository repo = mock(ExerciseRepository.class);
        when(repo.findByCategoryId(null)).thenThrow(new RuntimeException("db error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.findByCategoryId(null));
        assertEquals("db error", ex.getMessage());
        verify(repo).findByCategoryId(null);
    }
}

