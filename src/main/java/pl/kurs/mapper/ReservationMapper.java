package pl.kurs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.kurs.dto.ReservationDto;
import pl.kurs.entity.Reservation;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "status.value", target = "statusName")
    ReservationDto entityToDto(Reservation reservation);

    List<ReservationDto> entitiesToDtos(List<Reservation> reservations);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "status", ignore = true)
    Reservation dtoToEntity(ReservationDto reservationDto);

    @Mapping(target = "car", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "status", ignore = true)
    Reservation dtoToEntityWithId(ReservationDto reservationDto);
}
