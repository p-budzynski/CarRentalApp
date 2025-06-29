package pl.kurs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import pl.kurs.entity.Car;
import pl.kurs.exception.DataNotFoundException;
import pl.kurs.exception.InvalidDataAccessApiUsageException;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.repository.CarRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    @Mock
    private CarRepository carRepositoryMock;

    @InjectMocks
    private CarService carServiceMock;

    private Car sampleCar;

    @BeforeEach
    void setUp() {
        sampleCar = new Car("Toyota", "Corolla", 2020, "WX 17458", new BigDecimal(140));
        sampleCar.setId(1L);
    }

    @Test
    void shouldReturnCarForGetCarById() {
        //given
        when(carRepositoryMock.findById(1L)).thenReturn(Optional.of(sampleCar));

        //when
        Car result = carServiceMock.getCarById(1L);

        //then
        assertThat(result).isEqualTo(sampleCar);
    }

    @Test
    void shouldThrowWhenNotFoundCarById() {
        //given
        when(carRepositoryMock.findById(5L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> carServiceMock.getCarById(5L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Car not found with id: 5");
    }

    @Test
    void shouldReturnPagedCarsForGetAll() {
        //given
        Page<Car> page = new PageImpl<>(List.of(sampleCar));
        when(carRepositoryMock.findAll(PageRequest.of(0, 5))).thenReturn(page);

        //when
        Page<Car> result = carServiceMock.getAll(0, 5);

        //then
        assertThat(result.getContent()).containsExactly(sampleCar);
    }

    @Test
    void shouldThrowWhenSortFieldInvalid() {
        //given when then
        assertThatThrownBy(() -> carServiceMock.getAllSorted("nonexistentField", Sort.Direction.ASC, 0, 5))
                .isInstanceOf(InvalidDataAccessApiUsageException.class)
                .hasMessageContaining("Invalid sort field: nonexistentField");
    }

    @Test
    void shouldSaveCar() {
        //given
        when(carRepositoryMock.save(sampleCar)).thenReturn(sampleCar);

        //when
        Car saved = carServiceMock.saveCar(sampleCar);

        //then
        assertThat(saved).isEqualTo(sampleCar);
        verify(carRepositoryMock).save(sampleCar);
    }

    @Test
    void shouldUpdateCarWhenExists() {
        //given
        Car incoming = new Car("Honda", "Civic", 2022, "WI 91827", new BigDecimal(160));
        incoming.setId(1L);
        when(carRepositoryMock.findById(1L)).thenReturn(Optional.of(sampleCar));
        when(carRepositoryMock.save(any(Car.class))).thenAnswer(inv -> inv.getArgument(0));

        //when
        Car updated = carServiceMock.updateCar(incoming);

        //then
        assertThat(updated.getProducer()).isEqualTo("Honda");
        assertThat(updated.getModel()).isEqualTo("Civic");
        assertThat(updated.getYearOfProduction()).isEqualTo(2022);
        assertThat(updated.getRegistrationNumber()).isEqualTo("WI 91827");
        assertThat(updated.getPricePerDay()).isEqualTo(BigDecimal.valueOf(160));
        verify(carRepositoryMock).save(sampleCar);
    }

    @Test
    void shouldThrowWhenUpdateCarAndNotFound() {
        //given
        when(carRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> carServiceMock.updateCar(sampleCar))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Car with id: 1 not found");
    }

    @Test
    void shouldDeleteCarById() {
        //when
        carServiceMock.deleteCarById(1L);

        //then
        verify(carRepositoryMock).deleteById(1L);
    }

    @Test
    void shouldReturnSortedPageForGetAllSorted() {
        //given
        Page<Car> page = new PageImpl<>(List.of(sampleCar));
        when(carRepositoryMock.findAll(any(Pageable.class))).thenReturn(page);

        //when
        Page<Car> result = carServiceMock.getAllSorted("producer", Sort.Direction.ASC, 0, 5);

        //then
        assertThat(result.getContent()).containsExactly(sampleCar);
        verify(carRepositoryMock).findAll((argThat((Pageable p) -> {
            p.getSort().getOrderFor("producer");
            return true;
        })));
    }

    @Test
    void shouldReturnPageWithAvailableCarsBySearchingForProducerAndModel() {
        //given
        Page<Car> page = new PageImpl<>(List.of(sampleCar));
        when(carRepositoryMock.findByProducerAndModel("Toyota", "Corolla", PageRequest.of(0, 5)))
                .thenReturn(page);

        //when
        Page<Car> result = carServiceMock.getByProducerAndModelAndAvailable("Toyota", "Corolla", null, null, 0, 5);

        //then
        assertThat(result.getContent()).containsExactly(sampleCar);
        verify(carRepositoryMock).findByProducerAndModel("Toyota", "Corolla", PageRequest.of(0, 5));
    }

    @Test
    void shouldReturnPageWithAvailableCarsBySearchingForProducerAndModelAndStartDateAndEndDateNull() {
        //given
        LocalDate start = LocalDate.now();
        Page<Car> page = new PageImpl<>(List.of(sampleCar));
        when(carRepositoryMock.findByProducerAndModel(eq("Toyota"), eq("Corolla"), any(Pageable.class)))
                .thenReturn(page);

        // when
        carServiceMock.getByProducerAndModelAndAvailable("Toyota", "Corolla", start, null, 0, 5);
    }

    @Test
    void shouldReturnPageWithAvailableCarsBySearchingForProducerAndModelAndDatesAreProvided() {
        //given
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(5);
        Page<Car> page = new PageImpl<>(List.of(sampleCar));
        when(carRepositoryMock.findAvailableCars("Toyota", "Corolla", start, end, PageRequest.of(0, 5)))
                .thenReturn(page);

        //when
        Page<Car> result = carServiceMock.getByProducerAndModelAndAvailable("Toyota", "Corolla", start, end, 0, 5);

        //then
        assertThat(result.getContent()).containsExactly(sampleCar);
        verify(carRepositoryMock).findAvailableCars("Toyota", "Corolla", start, end, PageRequest.of(0, 5));
    }

    @Test
    void shouldReturnCarWhenFindForUpdateExists() {
        //given
        when(carRepositoryMock.findForUpdate(1L)).thenReturn(Optional.of(sampleCar));

        //when
        Car result = carServiceMock.findForUpdate(1L);

        //then
        assertThat(result).isEqualTo(sampleCar);
    }

    @Test
    void shouldThrowWhenFindForUpdateNotFound() {
        //given
        when(carRepositoryMock.findForUpdate(1L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> carServiceMock.findForUpdate(1L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Car with id: 1 not found");
    }
}
