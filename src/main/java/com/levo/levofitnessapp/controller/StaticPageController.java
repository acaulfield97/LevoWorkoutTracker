package com.levo.levofitnessapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticPageController {

    @GetMapping("/")
    public String landingPage(Model model) {
        model.addAttribute("activePage", "home");
        return "LandingPage";  // loads templates/LandingPage.html


    }
}
