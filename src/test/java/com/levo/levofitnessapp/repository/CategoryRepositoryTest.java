package com.levo.levofitnessapp.repository;

import com.levo.levofitnessapp.model.Category;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryRepositoryTest {

    @Test
    void returnsAllCategoriesFromFindAll() {
        CategoryRepository repo = mock(CategoryRepository.class);
        Category c1 = new Category();
        Category c2 = new Category();
        when(repo.findAll()).thenReturn(List.of(c1, c2));

        Iterable<Category> result = repo.findAll();

        assertNotNull(result);
        assertEquals(2, ((List<Category>) result).size());
        verify(repo, times(1)).findAll();
    }

    @Test
    void savePersistsAndReturnsCategory() {
        CategoryRepository repo = mock(CategoryRepository.class);
        Category cat = new Category();
        when(repo.save(cat)).thenReturn(cat);

        Category saved = repo.save(cat);

        assertSame(cat, saved);
        verify(repo, times(1)).save(cat);
    }

    @Test
    void findByIdReturnsEmptyWhenNotFound() {
        CategoryRepository repo = mock(CategoryRepository.class);
        when(repo.findById(123L)).thenReturn(Optional.empty());

        Optional<Category> opt = repo.findById(123L);

        assertTrue(opt.isEmpty());
        verify(repo).findById(123L);
    }

    @Test
    void deleteRemovesCategoryWhenExists() {
        CategoryRepository repo = mock(CategoryRepository.class);
        Category cat = new Category();
        cat.setId(7L);

        doNothing().when(repo).delete(cat);

        repo.delete(cat);

        verify(repo, times(1)).delete(cat);
    }
}

