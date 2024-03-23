package org.example;

import org.example.core.Mage;
import org.example.core.MageRepository;
import org.example.infrastructure.InMemoryMageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MageRepositoryTests {
    private MageRepository sut = null;

    @BeforeEach
    public void init() {
        sut = new InMemoryMageRepository(List.of(new Mage("Gandalf", 100), new Mage("Saruman", 90)));;
    }

    @Test
    public void findByName_whenMageExists_shouldReturnMage() {
        var result = sut.findByName("Gandalf");

       assertThat(result, isPresent());
    }

    @Test
    public void findByName_whenMageDoesNotExist_shouldReturnEmpty() {
        var result = sut.findByName("Radagast");

        assertThat(result, isEmpty());
    }

    @Test
    public void delete_whenMageExists_shouldDeleteMage() {
        sut.delete("Gandalf");

        var result = sut.findByName("Gandalf");

        assertThat(result, isEmpty());
    }

    @Test
    public void delete_whenMageDoesNotExist_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> sut.delete("Radagast"));
    }

    @Test
    public void save_whenMageDoesNotExist_shouldSaveMage() {
        sut.save(new Mage("Radagast", 80));

        var result = sut.findByName("Radagast");

        assertThat(result, isPresent());
    }

    @Test
    public void save_whenMageExists_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> sut.save(new Mage("Gandalf", 100)));
    }
}
