package com.levo.levofitnessapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.io.IOException;

import org.mockito.ArgumentCaptor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Test
    void logoutHandlerRedirectsToIssuerLogoutWithReturnTo() throws Exception {
        SecurityConfig cfg = new SecurityConfig();

        Field issuerField = SecurityConfig.class.getDeclaredField("issuer");
        issuerField.setAccessible(true);
        issuerField.set(cfg, "https://auth.example/");

        Field clientField = SecurityConfig.class.getDeclaredField("clientId");
        clientField.setAccessible(true);
        clientField.set(cfg, "cid-123");

        Method m = SecurityConfig.class.getDeclaredMethod("logoutHandler");
        m.setAccessible(true);
        LogoutHandler handler = (LogoutHandler) m.invoke(cfg);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        Authentication auth = mock(Authentication.class);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(req));
        try {
            handler.logout(req, resp, auth);

            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(resp, times(1)).sendRedirect(captor.capture());
            String url = captor.getValue();

            assertTrue(url.startsWith("https://auth.example/v2/logout?client_id=cid-123&returnTo="));
            assertTrue(url.contains("returnTo="));
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    @Test
    void logoutHandlerWrapsIOExceptionInRuntimeException() throws Exception {
        SecurityConfig cfg = new SecurityConfig();

        Field issuerField = SecurityConfig.class.getDeclaredField("issuer");
        issuerField.setAccessible(true);
        issuerField.set(cfg, "https://auth.example/");

        Field clientField = SecurityConfig.class.getDeclaredField("clientId");
        clientField.setAccessible(true);
        clientField.set(cfg, "cid-123");

        Method m = SecurityConfig.class.getDeclaredMethod("logoutHandler");
        m.setAccessible(true);
        LogoutHandler handler = (LogoutHandler) m.invoke(cfg);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        doThrow(new IOException("boom")).when(resp).sendRedirect(anyString());
        Authentication auth = mock(Authentication.class);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(req));
        try {
            RuntimeException ex = assertThrows(RuntimeException.class, () -> handler.logout(req, resp, auth));
            assertNotNull(ex.getCause());
            assertTrue(ex.getCause() instanceof IOException);
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }
}
