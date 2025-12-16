package com.levo.levofitnessapp.service;

import com.levo.levofitnessapp.model.User;
import com.levo.levofitnessapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrentUserServiceTest {

    @Test
    void returnsNullWhenAuthenticationIsNull() {
        UserRepository userRepository = mock(UserRepository.class);
        CurrentUserService service = new CurrentUserService(userRepository);

        User result = service.getCurrentUser(null);

        assertNull(result);
        verifyNoInteractions(userRepository);
    }

    @Test
    void returnsNullWhenPrincipalIsNotOAuth2User() {
        UserRepository userRepository = mock(UserRepository.class);
        CurrentUserService service = new CurrentUserService(userRepository);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new Object());

        User result = service.getCurrentUser(authentication);

        assertNull(result);
        verifyNoInteractions(userRepository);
    }

    @Test
    void returnsNullWhenOAuth2UserHasNoSub() {
        UserRepository userRepository = mock(UserRepository.class);
        CurrentUserService service = new CurrentUserService(userRepository);

        Authentication authentication = mock(Authentication.class);
        OAuth2User oauth = mock(OAuth2User.class);
        when(oauth.getAttribute("sub")).thenReturn(null);
        when(authentication.getPrincipal()).thenReturn(oauth);

        User result = service.getCurrentUser(authentication);

        assertNull(result);
        verifyNoInteractions(userRepository);
    }

    @Test
    void returnsExistingUserWhenFoundByAuth0Id() {
        UserRepository userRepository = mock(UserRepository.class);
        CurrentUserService service = new CurrentUserService(userRepository);

        Authentication authentication = mock(Authentication.class);
        OAuth2User oauth = mock(OAuth2User.class);
        when(oauth.getAttribute("sub")).thenReturn("auth0|42");
        when(oauth.getAttribute("email")).thenReturn("bob@example.com");
        when(oauth.getAttribute("nickname")).thenReturn("bob");
        when(authentication.getPrincipal()).thenReturn(oauth);

        User found = new User(5L, "auth0|42", "bob@example.com", "bob");
        when(userRepository.findByAuth0Id("auth0|42")).thenReturn(Optional.of(found));

        User result = service.getCurrentUser(authentication);

        assertSame(found, result);
        verify(userRepository, times(1)).findByAuth0Id("auth0|42");
        verify(userRepository, never()).save(any());
    }

    @Test
    void createsAndSavesNewUserWhenNotFoundWithAttributes() {
        UserRepository userRepository = mock(UserRepository.class);
        CurrentUserService service = new CurrentUserService(userRepository);

        Authentication authentication = mock(Authentication.class);
        OAuth2User oauth = mock(OAuth2User.class);
        when(oauth.getAttribute("sub")).thenReturn("auth0|99");
        when(oauth.getAttribute("email")).thenReturn("alice@example.com");
        when(oauth.getAttribute("nickname")).thenReturn("alice");
        when(authentication.getPrincipal()).thenReturn(oauth);

        User saved = new User(10L, "auth0|99", "alice@example.com", "alice");
        when(userRepository.findByAuth0Id("auth0|99")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = service.getCurrentUser(authentication);

        assertSame(saved, result);
        verify(userRepository).findByAuth0Id("auth0|99");
        verify(userRepository).save(argThat(u -> "auth0|99".equals(u.getAuth0Id()) && "alice@example.com".equals(u.getEmail()) && "alice".equals(u.getUsername())));
    }

    @Test
    void createsAndSavesNewUserWhenNicknameIsNull() {
        UserRepository userRepository = mock(UserRepository.class);
        CurrentUserService service = new CurrentUserService(userRepository);

        Authentication authentication = mock(Authentication.class);
        OAuth2User oauth = mock(OAuth2User.class);
        when(oauth.getAttribute("sub")).thenReturn("auth0|77");
        when(oauth.getAttribute("email")).thenReturn("no.nick@example.com");
        when(oauth.getAttribute("nickname")).thenReturn(null);
        when(authentication.getPrincipal()).thenReturn(oauth);

        User saved = new User(11L, "auth0|77", "no.nick@example.com", null);
        when(userRepository.findByAuth0Id("auth0|77")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = service.getCurrentUser(authentication);

        assertSame(saved, result);
        verify(userRepository).save(argThat(u -> "auth0|77".equals(u.getAuth0Id()) && "no.nick@example.com".equals(u.getEmail()) && u.getUsername() == null));
    }
}

