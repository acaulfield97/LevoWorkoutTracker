package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.User;
import com.levo.levofitnessapp.repository.UserRepository;
import com.levo.levofitnessapp.service.CurrentUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    // from application.yml
    @Value("${okta.oauth2.domain}")
    private String auth0Domain;

    @Value("${okta.oauth2.client-id}")
    private String clientId;

    public AccountController(CurrentUserService currentUserService, UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public String accountPage(Model model, Authentication authentication) {
        User user = currentUserService.getCurrentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("auth0Domain", auth0Domain);
        model.addAttribute("auth0ClientId", clientId);
        return "account";
    }

    @PostMapping("/update-username")
    public String updateUsername(Authentication authentication,
                                 @RequestParam String username) {
        var user = currentUserService.getCurrentUser(authentication);

        if (user != null) {
            user.setUsername(username);
            userRepository.save(user);
        }

        return "redirect:/account?success";
    }
}
