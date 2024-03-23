package org.example;

import org.example.core.Mage;
import org.example.core.MageController;
import org.example.core.MageRepository;
import org.example.infrastructure.MageControllerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class MageControllerTests {
    private MageController mageController;
    private MageRepository mageRepositoryMock;

    @BeforeEach
    public void init() {
        mageRepositoryMock = mock(MageRepository.class);
        mageController = new MageControllerImpl(mageRepositoryMock);
    }

    @Test
    public void find_whenMageExists_shouldReturnMage() {
        when(mageRepositoryMock.findByName("Gandalf")).thenReturn(Optional.of(new Mage("Gandalf", 100)));

        var result = mageController.find("Gandalf");

        assertThat(result, is("Mage{name='Gandalf', level=100}"));
    }

    @Test
    public void find_whenMageDoesNotExist_shouldReturnNotFound() {
        when(mageRepositoryMock.findByName("Radagast")).thenReturn(Optional.empty());

        var result = mageController.find("Radagast");

        assertThat(result, is("not found"));
    }

    @Test
    public void delete_whenMageExists_shouldDeleteMage() {
        var result = mageController.delete("Gandalf");

        verify(mageRepositoryMock).delete("Gandalf");
        assertThat(result, is("done"));
    }

    @Test
    public void delete_whenMageDoesNotExist_shouldReturnNotFound() {
        doThrow(new IllegalArgumentException()).when(mageRepositoryMock).delete("Radagast");

        var result = mageController.delete("Radagast");

        assertThat(result, is("not found"));
    }

    @Test
    public void save_whenMageIsValid_shouldSaveMage() {
        var result = mageController.save("Radagast", "80");

        verify(mageRepositoryMock).save(new Mage("Radagast", 80));
        assertThat(result, is("done"));
    }

    @Test
    public void save_whenMageIsInvalid_shouldReturnBadRequest() {
        doThrow(new IllegalArgumentException()).when(mageRepositoryMock).save(new Mage("Radagast", 80));

        var result = mageController.save("Radagast", "80");

        assertThat(result, is("bad request"));
    }
}
