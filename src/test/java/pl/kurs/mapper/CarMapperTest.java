package pl.kurs.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kurs.dto.CarDto;
import pl.kurs.entity.Car;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CarMapperTest {
    private final CarMapper carMapper = Mappers.getMapper(CarMapper.class);

    @Test
    void shouldMapEntityToDto() {
        //given
        Car testCar = createTestCar();
        CarDto testCarDto = createTestCarDto();

        //when
        CarDto dto = carMapper.entityToDto(testCar);

        //then
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(testCarDto);
    }

    @Test
    void shouldMapEntityListToDtoList() {
        //given
        Car testCar = createTestCar();
        CarDto testCarDto = createTestCarDto();
        List<Car> cars = List.of(testCar);

        //when
        List<CarDto> dtos = carMapper.entitiesToDtos(cars);

        //then
        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(testCarDto);
    }

    @Test
    void shouldMapDtoToEntity() {
        //given
        Car testCar = createTestCar();
        CarDto testCarDto = createTestCarDto();

        //when
        Car entity = carMapper.dtoToEntity(testCarDto);

        //then
        assertThat(entity)
                .usingRecursiveComparison()
                .isEqualTo(testCar);
    }

    @Test
    void shouldMapDtoToEntityWithId() {
        //given
        Car testCar = createTestCar();
        testCar.setId(1L);
        CarDto testCarDto = createTestCarDto();
        testCarDto.setId(1L);

        //when
        Car entity = carMapper.dtoToEntityWithId(testCarDto);

        //then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity)
                .usingRecursiveComparison()
                .isEqualTo(testCar);
    }

    @Test
    void shouldReturnNullWhenDtoToEntityWithIdGivenNull() {
        //when then
        assertThat(carMapper.dtoToEntityWithId(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityToDtoGivenNull() {
        //when then
        assertThat(carMapper.entityToDto(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenDtoToEntityGivenNull() {
        //when then
        assertThat(carMapper.dtoToEntity(null)).isNull();
    }

    @Test
    void shouldReturnEmptyListWhenEntitiesToDtosGivenNull() {
        //when then
        assertThat(carMapper.entitiesToDtos(null)).isNull();
    }

    private Car createTestCar() {
        return new Car("Toyota", "Corolla", 2024, "WX98765", new BigDecimal(150));
    }

    private CarDto createTestCarDto() {
        return new CarDto("Toyota", "Corolla", 2024, "WX98765", new BigDecimal(150));
    }

}
