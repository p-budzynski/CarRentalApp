package pl.kurs.service;

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
    private CarService carService;

    @Test
    void shouldReturnCarForGetCarById() {
        //given
        Car testCar = createTestCar();
        when(carRepositoryMock.findById(1L)).thenReturn(Optional.of(testCar));

        //when
        Car result = carService.getCarById(1L);

        //then
        assertThat(result).isEqualTo(testCar);
    }

    @Test
    void shouldThrowWhenNotFoundCarById() {
        //given
        when(carRepositoryMock.findById(5L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> carService.getCarById(5L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Car not found with id: 5");
    }

    @Test
    void shouldReturnPagedCarsForGetAll() {
        //given
        Car testCar = createTestCar();
        Page<Car> page = new PageImpl<>(List.of(testCar));
        when(carRepositoryMock.findAll(PageRequest.of(0, 5))).thenReturn(page);

        //when
        Page<Car> result = carService.getAll(0, 5);

        //then
        assertThat(result.getContent()).containsExactly(testCar);
    }

    @Test
    void shouldSaveCar() {
        //given
        Car testCar = createTestCar();
        when(carRepositoryMock.save(testCar)).thenReturn(testCar);

        //when
        Car savedCar = carService.saveCar(testCar);

        //then
        assertThat(savedCar).isEqualTo(testCar);
    }

    @Test
    void shouldUpdateCarWhenExists() {
        //given
        Car testCar = createTestCar();
        Car incomingCar = new Car("Honda", "Civic", 2022, "WI 91827", new BigDecimal(160));
        incomingCar.setId(1L);
        when(carRepositoryMock.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepositoryMock.save(any(Car.class))).thenAnswer(inv -> inv.getArgument(0));

        //when
        Car updated = carService.updateCar(incomingCar);

        //then
        assertThat(updated.getProducer()).isEqualTo("Honda");
        assertThat(updated.getModel()).isEqualTo("Civic");
        assertThat(updated.getYearOfProduction()).isEqualTo(2022);
        assertThat(updated.getRegistrationNumber()).isEqualTo("WI 91827");
        assertThat(updated.getPricePerDay()).isEqualTo(BigDecimal.valueOf(160));
    }

    @Test
    void shouldThrowWhenUpdateCarAndNotFound() {
        //given
        Car testCar = createTestCar();
        when(carRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> carService.updateCar(testCar))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Car with id: 1 not found");
    }

    @Test
    void shouldDeleteCarById() {
        //when
        carService.deleteCarById(1L);

        //then
        verify(carRepositoryMock).deleteById(1L);
    }

    @Test
    void shouldReturnSortedPageForGetAllSorted() {
        //given
        Car testCar = createTestCar();
        Page<Car> page = new PageImpl<>(List.of(testCar));
        when(carRepositoryMock.findAll(any(Pageable.class))).thenReturn(page);

        //when
        Page<Car> result = carService.getAllSorted("producer", Sort.Direction.ASC, 0, 5);

        //then
        assertThat(result.getContent()).containsExactly(testCar);
        verify(carRepositoryMock).findAll((argThat((Pageable p) -> {
            Sort.Order producer = p.getSort().getOrderFor("producer");
            return producer.getDirection() == Sort.Direction.ASC;
        })));
    }

    @Test
    void shouldThrowWhenSortFieldInvalid() {
        //given when then
        assertThatThrownBy(() -> carService.getAllSorted("nonexistentField", Sort.Direction.ASC, 0, 5))
                .isInstanceOf(InvalidDataAccessApiUsageException.class)
                .hasMessageContaining("Invalid sort field: nonexistentField");
    }

    @Test
    void shouldReturnPageWithAvailableCarsBySearchingForProducerAndModel() {
        //given
        Car testCar = createTestCar();
        Page<Car> page = new PageImpl<>(List.of(testCar));
        when(carRepositoryMock.findByProducerAndModel("Toyota", "Corolla", PageRequest.of(0, 5)))
                .thenReturn(page);

        //when
        Page<Car> result = carService.getByProducerAndModelAndAvailable("Toyota", "Corolla", null, null, 0, 5);

        //then
        assertThat(result.getContent()).containsExactly(testCar);
    }

    @Test
    void shouldReturnPageWithAvailableCarsBySearchingForProducerAndModelAndStartDateAndEndDateNull() {
        //given
        Car testCar = createTestCar();
        LocalDate start = LocalDate.now();
        Page<Car> page = new PageImpl<>(List.of(testCar));
        when(carRepositoryMock.findByProducerAndModel(eq("Toyota"), eq("Corolla"), any(Pageable.class)))
                .thenReturn(page);

        //when
        Page<Car> result = carService.getByProducerAndModelAndAvailable("Toyota", "Corolla", start, null, 0, 5);

        //then
        assertThat(result.getContent()).containsExactly(testCar);
    }

    @Test
    void shouldReturnPageWithAvailableCarsBySearchingForProducerAndModelAndDatesAreProvided() {
        //given
        Car testCar = createTestCar();
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(5);
        Page<Car> page = new PageImpl<>(List.of(testCar));
        when(carRepositoryMock.findAvailableCars("Toyota", "Corolla", start, end, PageRequest.of(0, 5)))
                .thenReturn(page);

        //when
        Page<Car> result = carService.getByProducerAndModelAndAvailable("Toyota", "Corolla", start, end, 0, 5);

        //then
        assertThat(result.getContent()).containsExactly(testCar);
    }

    @Test
    void shouldReturnCarWhenFindForUpdateExists() {
        //given
        Car testCar = createTestCar();
        when(carRepositoryMock.findForUpdate(1L)).thenReturn(Optional.of(testCar));

        //when
        Car result = carService.findForUpdate(1L);

        //then
        assertThat(result).isEqualTo(testCar);
    }

    @Test
    void shouldThrowWhenFindForUpdateNotFound() {
        //given
        when(carRepositoryMock.findForUpdate(1L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> carService.findForUpdate(1L))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Car with id: 1 not found");
    }

    private Car createTestCar() {
        Car car = new Car("Toyota", "Corolla", 2020, "WX 17458", new BigDecimal(140));
        car.setId(1L);
        return car;
    }
}
