package pl.kurs.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kurs.dto.ReservationDto;
import pl.kurs.entity.Car;
import pl.kurs.entity.Customer;
import pl.kurs.entity.Reservation;
import pl.kurs.entity.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationMapperTest {
    private static final LocalDate FROM = LocalDate.of(2025, 7, 1);
    private static final LocalDate TO = LocalDate.of(2025, 7, 10);
    private final ReservationMapper reservationMapper= Mappers.getMapper(ReservationMapper.class);

    @Test
    void shouldMapEntityToDto() {
        //given
        Reservation testReservation = createTestReservation();
        ReservationDto testReservationDto = createTestReservationDto();

        //when
        ReservationDto dto = reservationMapper.entityToDto(testReservation);

        //then
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(testReservationDto);
    }

    @Test
    void shouldMapEntityListToDtoList() {
        //given
        Reservation testReservation = createTestReservation();
        ReservationDto testReservationDto = createTestReservationDto();
        List<Reservation> reservations = List.of(testReservation);

        //when
        List<ReservationDto> dtos = reservationMapper.entitiesToDtos(reservations);

        //then
        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(testReservationDto);
    }

    @Test
    void shouldMapDtoToEntity() {
        //when
        Reservation testReservation = createTestReservation();
        ReservationDto testReservationDto = createTestReservationDto();
        Reservation entity = reservationMapper.dtoToEntity(testReservationDto);

        //then
        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("car")
                .ignoringFields("customer")
                .ignoringFields("status")
                .isEqualTo(testReservation);
    }

    @Test
    void shouldMapDtoToEntityWithId() {
        //given
        Reservation testReservation = createTestReservation();
        testReservation.setId(1L);
        ReservationDto testReservationDto = createTestReservationDto();
        testReservationDto.setId(1L);

        //when
        Reservation entity = reservationMapper.dtoToEntityWithId(testReservationDto);

        //then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("car")
                .ignoringFields("customer")
                .ignoringFields("status")
                .isEqualTo(testReservation);
    }

    @Test
    void shouldReturnNullCarCustomerStatusWhenFieldsAreNull() {
        //given
        Reservation testReservation = new Reservation(null, null, FROM, TO, new BigDecimal(600), null);
        testReservation.setId(1L);

        //when
        ReservationDto dto = reservationMapper.entityToDto(testReservation);

        //then
        assertThat(dto.getCarId()).isNull();
        assertThat(dto.getCustomerId()).isNull();
        assertThat(dto.getStatusName()).isNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTotalAmount()).isEqualTo(new BigDecimal(600));
    }

    @Test
    void shouldReturnNullWhenDtoToEntityWithIdGivenNull() {
        //when then
        assertThat(reservationMapper.dtoToEntityWithId(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityToDtoGivenNull() {
        //when then
        assertThat(reservationMapper.entityToDto(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenDtoToEntityGivenNull() {
        //when then
        assertThat(reservationMapper.dtoToEntity(null)).isNull();
    }

    @Test
    void shouldReturnEmptyListWhenEntitiesToDtosGivenNull() {
        //when then
        assertThat(reservationMapper.entitiesToDtos(null)).isNull();
    }

    private Reservation createTestReservation() {
        Car car = new Car();
        car.setId(1L);
        Customer customer = new Customer();
        customer.setId(1L);
        return new Reservation(car, customer, FROM, TO, new BigDecimal(1500), Status.RESERVED);
    }

    private ReservationDto createTestReservationDto() {
        return new ReservationDto(1L, 1L, FROM, TO, new BigDecimal(1500), "RESERVED");
    }

}
