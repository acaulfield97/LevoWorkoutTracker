package com.levo.levofitnessapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

import static org.junit.jupiter.api.Assertions.*;

class StaticPageControllerTest {

    @Test
    void landingPageReturnsLandingPageAndSetsActivePageHome() {
        StaticPageController controller = new StaticPageController();
        ConcurrentModel model = new ConcurrentModel();

        String view = controller.landingPage(model);

        assertEquals("LandingPage", view);
        assertEquals("home", model.getAttribute("activePage"));
    }

    @Test
    void landingPageThrowsWhenModelIsNull() {
        StaticPageController controller = new StaticPageController();
        assertThrows(NullPointerException.class, () -> controller.landingPage(null));
    }
}

