package com.levo.levofitnessapp.config;

import com.levo.levofitnessapp.model.User;
import com.levo.levofitnessapp.model.Workout;
import com.levo.levofitnessapp.repository.WorkoutRepository;
import com.levo.levofitnessapp.service.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalControllerAdviceTest {

    @Test
    void currentUsernameReturnsUsernameWhenUserPresent() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        GlobalControllerAdvice advice = new GlobalControllerAdvice(currentUserService, workoutRepository);

        Authentication authentication = mock(Authentication.class);
        User user = new User(1L, "auth0|1", "a@a.com", "alice");
        when(currentUserService.getCurrentUser(authentication)).thenReturn(user);

        String result = advice.currentUsername(authentication);

        assertEquals("alice", result);
    }

    @Test
    void currentUsernameReturnsNullWhenUserMissing() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        GlobalControllerAdvice advice = new GlobalControllerAdvice(currentUserService, workoutRepository);

        Authentication authentication = mock(Authentication.class);
        when(currentUserService.getCurrentUser(authentication)).thenReturn(null);

        String result = advice.currentUsername(authentication);

        assertNull(result);
    }

    @Test
    void currentUserIdReturnsIdWhenUserPresent() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        GlobalControllerAdvice advice = new GlobalControllerAdvice(currentUserService, workoutRepository);

        Authentication authentication = mock(Authentication.class);
        User user = new User(42L, "auth0|42", "bob@b.com", "bob");
        when(currentUserService.getCurrentUser(authentication)).thenReturn(user);

        Long id = advice.currentUserId(authentication);

        assertEquals(42L, id);
    }

    @Test
    void currentUserIdReturnsNullWhenUserMissing() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        GlobalControllerAdvice advice = new GlobalControllerAdvice(currentUserService, workoutRepository);

        Authentication authentication = mock(Authentication.class);
        when(currentUserService.getCurrentUser(authentication)).thenReturn(null);

        Long id = advice.currentUserId(authentication);

        assertNull(id);
    }

    @Test
    void activeWorkoutReturnsWorkoutWhenUserHasActiveWorkout() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        GlobalControllerAdvice advice = new GlobalControllerAdvice(currentUserService, workoutRepository);

        Authentication authentication = mock(Authentication.class);
        User user = new User(7L, "auth0|7", "u@u.com", "user");
        when(currentUserService.getCurrentUser(authentication)).thenReturn(user);

        Workout workout = new Workout();
        when(workoutRepository.findByUserIdAndEndedAtIsNull(7L)).thenReturn(Optional.of(workout));

        Workout result = advice.activeWorkout(authentication);

        assertSame(workout, result);
    }

    @Test
    void activeWorkoutReturnsNullWhenNoActiveWorkoutExists() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        GlobalControllerAdvice advice = new GlobalControllerAdvice(currentUserService, workoutRepository);

        Authentication authentication = mock(Authentication.class);
        User user = new User(9L, "auth0|9", "x@x.com", "x");
        when(currentUserService.getCurrentUser(authentication)).thenReturn(user);

        when(workoutRepository.findByUserIdAndEndedAtIsNull(9L)).thenReturn(Optional.empty());

        Workout result = advice.activeWorkout(authentication);

        assertNull(result);
    }

    @Test
    void activeWorkoutThrowsWhenCurrentUserIsMissing() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        WorkoutRepository workoutRepository = mock(WorkoutRepository.class);
        GlobalControllerAdvice advice = new GlobalControllerAdvice(currentUserService, workoutRepository);

        Authentication authentication = mock(Authentication.class);
        when(currentUserService.getCurrentUser(authentication)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> advice.activeWorkout(authentication));
    }
}

