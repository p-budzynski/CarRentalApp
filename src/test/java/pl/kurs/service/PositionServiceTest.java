package pl.kurs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.entity.Position;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.repository.PositionRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PositionServiceTest {

    @Mock
    private PositionRepository positionRepositoryMock;

    @InjectMocks
    private PositionService positionServiceMock;

    private Position samplePosition;

    @BeforeEach
    void setUp() {
        samplePosition = new Position("MECHANIC");
        samplePosition.setId(1L);
    }

    @Test
    void shouldReturnPosition() {
        //given
        when(positionRepositoryMock.findById(1L)).thenReturn(Optional.of(samplePosition));

        //when
        Position result = positionServiceMock.findById(1L);

        //then
        assertThat(result).isEqualTo(samplePosition);
        verify(positionRepositoryMock).findById(1L);
    }

    @Test
    void shouldThrowWhenPositionNotFound() {
        //given
        when(positionRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> positionServiceMock.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Position not found with id: 99");
    }
}
