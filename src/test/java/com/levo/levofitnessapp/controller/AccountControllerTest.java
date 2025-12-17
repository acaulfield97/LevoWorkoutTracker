// new file
package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.User;
import com.levo.levofitnessapp.repository.UserRepository;
import com.levo.levofitnessapp.service.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    @Test
    void returnsAccountViewAndAddsModelAttributesWhenUserPresent() throws Exception {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        UserRepository userRepository = mock(UserRepository.class);
        Authentication authentication = mock(Authentication.class);

        User user = new User(1L, "auth0|123", "alice@example.com", "alice");
        when(currentUserService.getCurrentUser(authentication)).thenReturn(user);

        AccountController controller = new AccountController(currentUserService, userRepository);
        // set @Value fields via reflection
        java.lang.reflect.Field domainField = AccountController.class.getDeclaredField("auth0Domain");
        domainField.setAccessible(true);
        domainField.set(controller, "example.okta.com");

        java.lang.reflect.Field clientField = AccountController.class.getDeclaredField("clientId");
        clientField.setAccessible(true);
        clientField.set(controller, "client-123");

        ConcurrentModel model = new ConcurrentModel();

        String view = controller.accountPage(model, authentication);

        assertEquals("account", view);
        assertSame(user, model.getAttribute("user"));
        assertEquals("example.okta.com", model.getAttribute("auth0Domain"));
        assertEquals("client-123", model.getAttribute("auth0ClientId"));
    }

    @Test
    void updateUsernameSavesNewUsernameWhenUserPresent() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        UserRepository userRepository = mock(UserRepository.class);
        Authentication authentication = mock(Authentication.class);

        User user = new User(null, "auth0|789", "bob@example.com", "bob");
        when(currentUserService.getCurrentUser(authentication)).thenReturn(user);

        AccountController controller = new AccountController(currentUserService, userRepository);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String result = controller.updateUsername(authentication, "bobby", redirectAttributes);

        assertEquals("redirect:/account?success", result);
        assertEquals("bobby", user.getUsername());
        verify(userRepository, times(1)).save(user);
        assertTrue((Boolean) redirectAttributes.getFlashAttributes().get("success"));
    }

    @Test
    void updateUsernameDoesNotSaveWhenUserMissingAndStillRedirects() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        UserRepository userRepository = mock(UserRepository.class);
        Authentication authentication = mock(Authentication.class);

        when(currentUserService.getCurrentUser(authentication)).thenReturn(null);

        AccountController controller = new AccountController(currentUserService, userRepository);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String result = controller.updateUsername(authentication, "ignored", redirectAttributes);

        assertEquals("redirect:/account?success", result);
        verify(userRepository, never()).save(any());
        assertTrue((Boolean) redirectAttributes.getFlashAttributes().get("success"));
    }
}

