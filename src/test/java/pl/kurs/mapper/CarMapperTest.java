package pl.kurs.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kurs.dto.CarDto;
import pl.kurs.entity.Car;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CarMapperTest {

    private CarMapper carMapper;
    private Car sampleCar;
    private CarDto sampleCarDto;

    @BeforeEach
    void setUp() {
        carMapper = Mappers.getMapper(CarMapper.class);

        sampleCar = new Car("Toyota", "Corolla", 2024, "WX98765", new BigDecimal(150));
        sampleCarDto = new CarDto("Toyota", "Corolla", 2024, "WX98765", new BigDecimal(150));
    }

    @Test
    void shouldMapEntityToDto() {
        //when
        CarDto dto = carMapper.entityToDto(sampleCar);

        //then
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(sampleCarDto);
    }

    @Test
    void shouldMapEntityListToDtoList() {
        //given
        List<Car> cars = List.of(sampleCar);

        //when
        List<CarDto> dtos = carMapper.entitiesToDtos(cars);

        //then
        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(sampleCarDto);
    }

    @Test
    void shouldMapDtoToEntity() {
        //when
        Car entity = carMapper.dtoToEntity(sampleCarDto);

        //then
        assertThat(entity)
                .usingRecursiveComparison()
                .isEqualTo(sampleCar);
    }

    @Test
    void shouldMapDtoToEntityWithId() {
        //given
        sampleCarDto.setId(1L);
        sampleCar.setId(1L);

        //when
        Car entity = carMapper.dtoToEntityWithId(sampleCarDto);

        //then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity)
                .usingRecursiveComparison()
                .isEqualTo(sampleCar);
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

}
