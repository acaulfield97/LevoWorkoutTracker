package com.levo.levofitnessapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseCategoryTest {

    @Test
    void allowsSettingAndGettingIdAndName() {
        ExerciseCategory category = new ExerciseCategory();
        category.setId(123L);
        category.setName("Strength");

        assertEquals(123L, category.getId());
        assertEquals("Strength", category.getName());
    }

    @Test
    void allowsNullName() {
        ExerciseCategory category = new ExerciseCategory();
        category.setId(1L);
        category.setName(null);

        assertEquals(1L, category.getId());
        assertNull(category.getName());
    }

    @Test
    void preservesVeryLongNameValue() {
        String longName = "x".repeat(10_000);
        ExerciseCategory category = new ExerciseCategory();
        category.setName(longName);

        assertEquals(longName, category.getName());
    }
}

