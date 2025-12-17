package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.*;
import com.levo.levofitnessapp.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExerciseControllerTest {

    @Test
    void returnsCreateExerciseViewWithCategoriesExercisesWorkoutAndSetsWhenCategorySelectedAndActiveWorkoutExists() {
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        ExerciseController controller = new ExerciseController(exerciseRepository, categoryRepository, workoutRepository, workoutExerciseRepository, setRepository);

        Long categoryId = 5L;
        Long userId = 11L;

        Category cat = new Category();
        Iterable<Category> allCategories = List.of(cat);
        when(categoryRepository.findAll()).thenReturn(allCategories);

        Exercise ex = new Exercise();
        List<Exercise> categoryExercises = List.of(ex);
        when(exerciseRepository.findByCategoryId(categoryId)).thenReturn(categoryExercises);

        Workout workout = new Workout();
        try {
            java.lang.reflect.Field workoutIdField = Workout.class.getDeclaredField("id");
            workoutIdField.setAccessible(true);
            workoutIdField.set(workout, 99L);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        when(workoutRepository.findByUserIdAndEndedAtIsNull(userId)).thenReturn(Optional.of(workout));

        WorkoutExercise we = new WorkoutExercise();
        we.setId(42L);
        List<WorkoutExercise> workoutExercises = List.of(we);
        when(workoutExerciseRepository.findByWorkoutId(workout.getId())).thenReturn(workoutExercises);

        Set set1 = new Set(1L, we, 1, 10, 20);
        when(setRepository.findByWorkoutExerciseIdOrderBySetNumberAsc(we)).thenReturn(List.of(set1));

        ModelAndView mv = controller.exercisePage(categoryId, null, userId);

        assertEquals("/create_exercise", mv.getViewName());
        assertSame(allCategories, mv.getModel().get("categories"));
        assertSame(categoryExercises, mv.getModel().get("exercises"));
        assertEquals(categoryId, mv.getModel().get("selectedCategoryId"));
        assertSame(workout, mv.getModel().get("workout"));
        assertSame(workoutExercises, mv.getModel().get("workoutExercises"));
        Map<?,?> setsMap = (Map<?,?>) mv.getModel().get("setsMap");
        assertTrue(setsMap.containsKey(we.getId()));
        assertIterableEquals(List.of(set1), (Iterable<?>) setsMap.get(we.getId()));
    }

    @Test
    void returnsCreateExerciseViewWithEmptyExercisesAndNoWorkoutWhenNoCategorySelected() {
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        ExerciseController controller = new ExerciseController(exerciseRepository, categoryRepository, workoutRepository, workoutExerciseRepository, setRepository);

        Long userId = 7L;
        Iterable<Category> allCategories = List.of(new Category());
        when(categoryRepository.findAll()).thenReturn(allCategories);
        when(workoutRepository.findByUserIdAndEndedAtIsNull(userId)).thenReturn(Optional.empty());

        ModelAndView mv = controller.exercisePage(null, null, userId);

        assertEquals("/create_exercise", mv.getViewName());
        assertSame(allCategories, mv.getModel().get("categories"));
        Iterable<?> exercises = (Iterable<?>) mv.getModel().get("exercises");
        assertFalse(exercises.iterator().hasNext());
        assertNull(mv.getModel().get("workout"));
        Map<?,?> setsMap = (Map<?,?>) mv.getModel().get("setsMap");
        assertTrue(setsMap.isEmpty());
    }

    @Test
    void addExerciseSavesNewExerciseAndRedirectsWhenCategoryExists() {
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        ExerciseController controller = new ExerciseController(exerciseRepository, categoryRepository, workoutRepository, workoutExerciseRepository, setRepository);

        Long categoryId = 3L;
        String name = "NewExercise";
        Category cat = new Category();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(cat));

        RedirectView rv = controller.addExercise(name, categoryId, 1L);

        assertEquals("/exercise", rv.getUrl());
        verify(exerciseRepository, times(1)).save(argThat(e -> name.equals(((Exercise)e).getExerciseName()) && ((Exercise)e).getCategory() == cat));
    }

    @Test
    void addExerciseThrowsWhenCategoryMissing() {
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        ExerciseController controller = new ExerciseController(exerciseRepository, categoryRepository, workoutRepository, workoutExerciseRepository, setRepository);

        Long missingCategoryId = 88L;
        when(categoryRepository.findById(missingCategoryId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> controller.addExercise("x", missingCategoryId, 1L));
    }

    @Test
    void showExerciseSetReturnsCreateSetsViewWhenExerciseAndActiveWorkoutExist() {
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        ExerciseController controller = new ExerciseController(exerciseRepository, categoryRepository, workoutRepository, workoutExerciseRepository, setRepository);

        Long exerciseId = 21L;
        Long userId = 8L;

        Exercise exercise = new Exercise();
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

        Workout workout = new Workout();
        try {
            java.lang.reflect.Field workoutIdField = Workout.class.getDeclaredField("id");
            workoutIdField.setAccessible(true);
            workoutIdField.set(workout, 55L);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        when(workoutRepository.findByUserIdAndEndedAtIsNull(userId)).thenReturn(Optional.of(workout));

        ModelAndView mv = controller.showExerciseSet(exerciseId, userId);

        assertEquals("/create_sets", mv.getViewName());
        assertSame(exercise, mv.getModel().get("exercise"));
        assertEquals(workout.getId(), mv.getModel().get("workoutId"));
    }

    @Test
    void showExerciseSetThrowsWhenExerciseNotFoundOrNoActiveWorkout() {
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        ExerciseController controller = new ExerciseController(exerciseRepository, categoryRepository, workoutRepository, workoutExerciseRepository, setRepository);

        Long missingExerciseId = 99L;
        Long userId = 5L;

        when(exerciseRepository.findById(missingExerciseId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> controller.showExerciseSet(missingExerciseId, userId));

        Exercise existingExercise = new Exercise();
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(existingExercise));
        when(workoutRepository.findByUserIdAndEndedAtIsNull(userId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> controller.showExerciseSet(1L, userId));
    }

    @Test
    void includesSelectedExerciseIdWhenProvided() {
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        ExerciseController controller = new ExerciseController(exerciseRepository, categoryRepository, workoutRepository, workoutExerciseRepository, setRepository);

        Long userId = 7L;
        Long selectedExerciseId = 55L;

        Iterable<Category> allCategories = List.of(new Category());
        when(categoryRepository.findAll()).thenReturn(allCategories);
        when(workoutRepository.findByUserIdAndEndedAtIsNull(userId)).thenReturn(Optional.empty());

        ModelAndView mv = controller.exercisePage(null, selectedExerciseId, userId);

        assertEquals("/create_exercise", mv.getViewName());
        assertEquals(selectedExerciseId, mv.getModel().get("selectedExerciseId"));
    }

    @Test
    void showCreateExercisePageReturnsAddNewExerciseViewWithCategories() {
        ExerciseRepository exerciseRepository = mock(ExerciseRepository.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        WorkoutExerciseRepository workoutExerciseRepository = mock(WorkoutExerciseRepository.class);
        SetRepository setRepository = mock(SetRepository.class);

        ExerciseController controller = new ExerciseController(exerciseRepository, categoryRepository, workoutRepository, workoutExerciseRepository, setRepository);

        Iterable<Category> categories = List.of(new Category(), new Category());
        when(categoryRepository.findAll()).thenReturn(categories);

        ModelAndView mv = controller.showCreateExercisePage();

        assertEquals("/add_new_exercise", mv.getViewName());
        assertSame(categories, mv.getModel().get("categories"));
    }
}
