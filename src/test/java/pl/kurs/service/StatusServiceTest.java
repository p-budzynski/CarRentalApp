package pl.kurs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.entity.Status;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.exception.StatusNotFoundException;
import pl.kurs.repository.StatusRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatusServiceTest {

    @Mock
    private StatusRepository statusRepositoryMock;

    @InjectMocks
    private StatusService statusServiceMock;

    private Status sampleStatus;

    @BeforeEach
    void setUp() {
        sampleStatus = new Status("CANCELLED");
        sampleStatus.setId(1L);
    }

    @Test
    void shouldReturnStatusForGetStatusById() {
        //given
        when(statusRepositoryMock.findById(1L)).thenReturn(Optional.of(sampleStatus));

        //when
        Status result = statusServiceMock.getStatusById(1L);

        //then
        assertThat(result).isEqualTo(sampleStatus);
        verify(statusRepositoryMock).findById(1L);
    }

    @Test
    void shouldThrowWhenStatusNotFoundById() {
        //given
        when(statusRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> statusServiceMock.getStatusById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Status not found with id: 99");
    }

    @Test
    void shouldReturnStatusWhenNameExists() {
        //given
        when(statusRepositoryMock.findByName("CANCELLED")).thenReturn(Optional.of(sampleStatus));

        //when
        Status result = statusServiceMock.findByName("CANCELLED");

        //then
        assertThat(result).isEqualTo(sampleStatus);
        verify(statusRepositoryMock).findByName("CANCELLED");
    }

    @Test
    void shouldThrowWhenStatusNameNotFound() {
        //given
        when(statusRepositoryMock.findByName("CANCELLED")).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> statusServiceMock.findByName("CANCELLED"))
                .isInstanceOf(StatusNotFoundException.class)
                .hasMessageContaining("Status CANCELLED not found");
    }
}
