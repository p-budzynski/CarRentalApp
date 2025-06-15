package pl.kurs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.kurs.dto.CarDto;
import pl.kurs.entity.Car;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    CarDto entityToDto(Car car);

    List<CarDto> entitiesToDtos(List<Car> cars);

    @Mapping(target = "id", ignore = true)
    Car dtoToEntity(CarDto carDto);

    Car dtoToEntityWithId(CarDto carDto);
}
