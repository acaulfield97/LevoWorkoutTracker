package com.levo.levofitnessapp.config;

import com.levo.levofitnessapp.service.CurrentUserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// ControllerAdvice is used for a global navbar (better than using normal controller apparently)
// Otherwise would need to get username for every page/controller
@ControllerAdvice
public class GlobalControllerAdvice {

    private final CurrentUserService currentUserService;

    public GlobalControllerAdvice(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    // Expose username to all Thymeleaf templates
    @ModelAttribute("currentUsername")
    public String currentUsername(Authentication authentication) {
        return currentUserService.getUsername(authentication);
    }
}
