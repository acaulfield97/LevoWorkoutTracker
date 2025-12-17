package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.dto.ExercisePoint;
import com.levo.levofitnessapp.model.Exercise;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.model.WorkoutExercise;
import com.levo.levofitnessapp.repository.SetRepository;
import com.levo.levofitnessapp.repository.WorkoutExerciseRepository;
import com.levo.levofitnessapp.repository.ExerciseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExerciseHistoryControllerTest {

    @Test
    void showHistoryAddsExerciseAndWorkoutExercisesSortedNewestFirst() {
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        ExerciseHistoryController controller = new ExerciseHistoryController(workoutExerciseRepository, exerciseRepository, setRepository);

        long exerciseId = 7L;
        long userId = 3L;

        Exercise exercise = new Exercise();
        when(exerciseRepository.findById(exerciseId)).thenReturn(java.util.Optional.of(exercise));

        WorkoutExercise older = new WorkoutExercise();
        Workout w1 = new Workout();
        w1.setStartedAt(LocalDateTime.now().minusDays(10));
        older.setWorkout(w1);

        WorkoutExercise newer = new WorkoutExercise();
        Workout w2 = new Workout();
        w2.setStartedAt(LocalDateTime.now().minusDays(1));
        newer.setWorkout(w2);

        List<WorkoutExercise> returned = java.util.Arrays.asList(older, newer);
        when(workoutExerciseRepository.findByExerciseIdAndUserWithSets(exerciseId, userId)).thenReturn(returned);

        ConcurrentModel model = new ConcurrentModel();

        String view = controller.showHistory(exerciseId, userId, model);

        assertEquals("SingleExerciseHistory", view);
        @SuppressWarnings("unchecked")
        List<WorkoutExercise> modelList = (List<WorkoutExercise>) model.getAttribute("workoutExercises");
        assertNotNull(modelList);
        assertEquals(newer, modelList.get(0));
        assertEquals(older, modelList.get(1));
        assertSame(exercise, model.getAttribute("exercise"));
    }

    @Test
    void showHistoryThrowsWhenExerciseNotFound() {
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        ExerciseHistoryController controller = new ExerciseHistoryController(workoutExerciseRepository, exerciseRepository, setRepository);

        long missingExerciseId = 99L;
        when(exerciseRepository.findById(missingExerciseId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> controller.showHistory(missingExerciseId, 1L, new ConcurrentModel()));
    }

    @Test
    void exerciseAnalyticsAddsPointsAndExerciseToModelWhenExerciseExists() {
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        ExerciseHistoryController controller = new ExerciseHistoryController(workoutExerciseRepository, exerciseRepository, setRepository);

        long exerciseId = 13L;
        Exercise exercise = new Exercise();
        when(exerciseRepository.findById(exerciseId)).thenReturn(java.util.Optional.of(exercise));

        ExercisePoint p1 = new ExercisePoint(LocalDateTime.now().minusDays(5), 80);
        ExercisePoint p2 = new ExercisePoint(LocalDateTime.now().minusDays(2), 90);
        List<ExercisePoint> points = List.of(p1, p2);
        when(setRepository.findExerciseWeightPoints(exerciseId)).thenReturn(points);

        ConcurrentModel model = new ConcurrentModel();

        String view = controller.exerciseAnalytics(exerciseId, model);

        assertEquals("ExerciseAnalyticsPage", view);
        assertSame(exercise, model.getAttribute("exercise"));
        assertSame(points, model.getAttribute("points"));
    }

    @Test
    void exerciseAnalyticsThrowsWhenExerciseMissing() {
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        ExerciseHistoryController controller = new ExerciseHistoryController(workoutExerciseRepository, exerciseRepository, setRepository);

        long missingExerciseId = 1234L;
        when(exerciseRepository.findById(missingExerciseId)).thenReturn(java.util.Optional.empty());

        assertThrows(NoSuchElementException.class, () -> controller.exerciseAnalytics(missingExerciseId, new ConcurrentModel()));
    }
}
