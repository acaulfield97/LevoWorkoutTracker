package com.levo.levofitnessapp.controller;

import com.levo.levofitnessapp.model.Category;
import com.levo.levofitnessapp.repository.CategoryRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @Test
    void returnsAllCategoriesFromRepository() {
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        CategoryController controller = new CategoryController(categoryRepository);

        Category c1 = new Category();
        Category c2 = new Category();
        Iterable<Category> categories = List.of(c1, c2);
        when(categoryRepository.findAll()).thenReturn(categories);

        Iterable<Category> result = controller.listCategories();

        assertSame(categories, result);
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void returnsEmptyIterableWhenRepositoryHasNoCategories() {
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        CategoryController controller = new CategoryController(categoryRepository);

        when(categoryRepository.findAll()).thenReturn(List.of());

        Iterable<Category> result = controller.listCategories();

        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void propagatesNullIfRepositoryReturnsNull() {
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        CategoryController controller = new CategoryController(categoryRepository);

        when(categoryRepository.findAll()).thenReturn(null);

        Iterable<Category> result = controller.listCategories();

        assertNull(result);
        verify(categoryRepository, times(1)).findAll();
    }
}

