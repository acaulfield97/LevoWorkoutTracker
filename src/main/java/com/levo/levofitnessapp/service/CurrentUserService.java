package com.levo.levofitnessapp.service;

import com.levo.levofitnessapp.model.User;
import com.levo.levofitnessapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public String getUsername(Authentication authentication) {
        if (authentication == null) return null;

        Object principal = authentication.getPrincipal();

        String auth0Id = null;
        String email = null;
        String nickname = null;

        if (principal instanceof OAuth2User oauth2User) {
            auth0Id = oauth2User.getAttribute("sub");
            email = oauth2User.getAttribute("email");
            nickname = oauth2User.getAttribute("nickname");
        }
        if (auth0Id == null) return null;

        // Make final copies after assigning values
        // to fix the “variable used in lambda must be final or effectively final” compilation error.
        final String finalAuth0Id = auth0Id;
        final String finalEmail = email;
        final String finalNickname = nickname;

        User user = userRepository.findByAuth0Id(auth0Id)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setAuth0Id(finalAuth0Id);
                    newUser.setEmail(finalEmail);
                    newUser.setUsername(finalNickname);
                    return userRepository.save(newUser);
                });

        return user.getUsername();
    }
}
