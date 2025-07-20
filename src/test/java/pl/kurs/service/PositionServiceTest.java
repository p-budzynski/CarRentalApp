package pl.kurs.service;

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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PositionServiceTest {

    @Mock
    private PositionRepository positionRepositoryMock;

    @InjectMocks
    private PositionService positionService;

    @Test
    void shouldReturnPosition() {
        //given
        Position testPosition = new Position("MECHANIC");
        testPosition.setId(1L);
        when(positionRepositoryMock.findById(1L)).thenReturn(Optional.of(testPosition));

        //when
        Position result = positionService.findById(1L);

        //then
        assertThat(result).isEqualTo(testPosition);
    }

    @Test
    void shouldThrowWhenPositionNotFound() {
        //given
        when(positionRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> positionService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Position not found with id: 99");
    }
}
