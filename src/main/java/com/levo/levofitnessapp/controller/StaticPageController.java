package com.levo.levofitnessapp.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class StaticPageController {

    @GetMapping("/")
    public ModelAndView landingPage() {
        return new ModelAndView("/LandingPage");
    }
}