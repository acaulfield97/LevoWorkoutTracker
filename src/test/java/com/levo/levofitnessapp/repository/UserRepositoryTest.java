package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

    @Test
    void returnsUserWhenAuth0IdFound() {
        UserRepository repo = mock(UserRepository.class);
        User user = new User(1L, "auth0|123", "alice@example.com", "alice");

        when(repo.findByAuth0Id("auth0|123")).thenReturn(Optional.of(user));

        Optional<User> result = repo.findByAuth0Id("auth0|123");

        assertTrue(result.isPresent());
        assertSame(user, result.get());
        verify(repo, times(1)).findByAuth0Id("auth0|123");
    }

    @Test
    void returnsEmptyWhenAuth0IdNotFound() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.findByAuth0Id("missing")).thenReturn(Optional.empty());

        Optional<User> result = repo.findByAuth0Id("missing");

        assertNotNull(result);
        assertFalse(result.isPresent());
        verify(repo).findByAuth0Id("missing");
    }

    @Test
    void propagatesRuntimeExceptionFromRepository() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.findByAuth0Id("bad")).thenThrow(new RuntimeException("db error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.findByAuth0Id("bad"));
        assertEquals("db error", ex.getMessage());
        verify(repo).findByAuth0Id("bad");
    }
}

