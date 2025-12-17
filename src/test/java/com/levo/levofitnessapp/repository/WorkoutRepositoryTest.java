package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Workout;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkoutRepositoryTest {

    @Test
    void returnsActiveWorkoutWhenPresent() {
        WorkoutRepository repo = mock(WorkoutRepository.class);
        Workout w = new Workout();
        w.setUserId(7L);
        when(repo.findByUserIdAndEndedAtIsNull(7L)).thenReturn(Optional.of(w));

        Optional<Workout> opt = repo.findByUserIdAndEndedAtIsNull(7L);

        assertTrue(opt.isPresent());
        assertSame(w, opt.get());
        verify(repo, times(1)).findByUserIdAndEndedAtIsNull(7L);
    }

    @Test
    void returnsEmptyWhenNoActiveWorkout() {
        WorkoutRepository repo = mock(WorkoutRepository.class);
        when(repo.findByUserIdAndEndedAtIsNull(99L)).thenReturn(Optional.empty());

        Optional<Workout> opt = repo.findByUserIdAndEndedAtIsNull(99L);

        assertTrue(opt.isEmpty());
        verify(repo).findByUserIdAndEndedAtIsNull(99L);
    }

    @Test
    void returnsAllWorkoutsOrderedByStartedAtDesc() {
        WorkoutRepository repo = mock(WorkoutRepository.class);
        Workout newer = new Workout();
        newer.setStartedAt(LocalDateTime.now().minusDays(1));
        Workout older = new Workout();
        older.setStartedAt(LocalDateTime.now().minusDays(10));

        when(repo.findAllByOrderByStartedAtDesc()).thenReturn(List.of(newer, older));

        List<Workout> list = repo.findAllByOrderByStartedAtDesc();

        assertNotNull(list);
        assertEquals(2, list.size());
        assertTrue(list.get(0).getStartedAt().isAfter(list.get(1).getStartedAt()) || list.get(0).getStartedAt().isEqual(list.get(1).getStartedAt()));
        verify(repo).findAllByOrderByStartedAtDesc();
    }

    @Test
    void returnsWorkoutsForUserBetweenDates() {
        WorkoutRepository repo = mock(WorkoutRepository.class);
        Long userId = 3L;
        LocalDateTime start = LocalDateTime.of(2023,1,1,0,0);
        LocalDateTime end = LocalDateTime.of(2023,12,31,23,59);

        Workout w1 = new Workout();
        w1.setStartedAt(LocalDateTime.of(2023,6,1,10,0));

        when(repo.findAllByUserIdAndStartedAtBetweenOrderByStartedAtDesc(userId, start, end)).thenReturn(List.of(w1));

        List<Workout> res = repo.findAllByUserIdAndStartedAtBetweenOrderByStartedAtDesc(userId, start, end);

        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(w1, res.get(0));
        verify(repo).findAllByUserIdAndStartedAtBetweenOrderByStartedAtDesc(userId, start, end);
    }

    @Test
    void findByIdAndUserIdWithExercisesReturnsWorkoutWhenPresent() {
        WorkoutRepository repo = mock(WorkoutRepository.class);
        Workout w = new Workout();
        when(repo.findByIdAndUserIdWithExercises(5L, 2L)).thenReturn(Optional.of(w));

        Optional<Workout> opt = repo.findByIdAndUserIdWithExercises(5L, 2L);

        assertTrue(opt.isPresent());
        assertSame(w, opt.get());
        verify(repo).findByIdAndUserIdWithExercises(5L, 2L);
    }

    @Test
    void findWorkoutDaysByUserReturnsDatesInDescOrder() {
        WorkoutRepository repo = mock(WorkoutRepository.class);
        Date d1 = Date.valueOf("2023-12-01");
        Date d2 = Date.valueOf("2023-11-01");
        when(repo.findWorkoutDaysByUser(4L)).thenReturn(List.of(d1, d2));

        List<Date> days = repo.findWorkoutDaysByUser(4L);

        assertNotNull(days);
        assertEquals(2, days.size());
        assertTrue(days.get(0).compareTo(days.get(1)) >= 0);
        verify(repo).findWorkoutDaysByUser(4L);
    }

    @Test
    void findAllByStartedAtBetweenOrderByStartedAtDescReturnsEmptyWhenNone() {
        WorkoutRepository repo = mock(WorkoutRepository.class);
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();
        when(repo.findAllByStartedAtBetweenOrderByStartedAtDesc(start, end)).thenReturn(List.of());

        List<Workout> res = repo.findAllByStartedAtBetweenOrderByStartedAtDesc(start, end);

        assertNotNull(res);
        assertTrue(res.isEmpty());
        verify(repo).findAllByStartedAtBetweenOrderByStartedAtDesc(start, end);
    }
}

