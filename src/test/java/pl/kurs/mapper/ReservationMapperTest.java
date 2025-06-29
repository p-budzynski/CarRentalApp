package pl.kurs.mapper;

import org.junit.jupiter.api.BeforeEach;
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
    private ReservationMapper reservationMapper;
    private Reservation sampleReservation;
    private ReservationDto sampleReservationDto;

    @BeforeEach
    void setUp() {
        reservationMapper = Mappers.getMapper(ReservationMapper.class);
        Car car = new Car();
        car.setId(1L);
        Customer customer = new Customer();
        customer.setId(1L);
        Status status = new Status();
        status.setId(1L);

        sampleReservation = new Reservation(car, customer, LocalDate.of(2025, 1, 20),
                LocalDate.of(2025,1,25), new BigDecimal(1500), status);
        sampleReservationDto = new ReservationDto(1L, 1L, LocalDate.of(2025, 1, 20),
                LocalDate.of(2025,1,25), new BigDecimal(1500), 1L);
    }

    @Test
    void shouldMapEntityToDto() {
        //when
        ReservationDto dto = reservationMapper.entityToDto(sampleReservation);

        //then
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(sampleReservationDto);
    }

    @Test
    void shouldMapEntityListToDtoList() {
        //given
        List<Reservation> reservations = List.of(sampleReservation);

        //when
        List<ReservationDto> dtos = reservationMapper.entitiesToDtos(reservations);

        //then
        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(sampleReservationDto);
    }

    @Test
    void shouldMapDtoToEntity() {
        //when
        Reservation entity = reservationMapper.dtoToEntity(sampleReservationDto);

        //then
        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("car")
                .ignoringFields("customer")
                .ignoringFields("status")
                .isEqualTo(sampleReservation);
    }

    @Test
    void shouldMapDtoToEntityWithId() {
        //given
        sampleReservationDto.setId(1L);
        sampleReservation.setId(1L);

        //when
        Reservation entity = reservationMapper.dtoToEntityWithId(sampleReservationDto);

        //then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("car")
                .ignoringFields("customer")
                .ignoringFields("status")
                .isEqualTo(sampleReservation);
    }

    @Test
    void shouldReturnNullCarCustomerStatusIdsWhenFieldsAreNull() {
        //given
        Reservation reservation = new Reservation(null, null, LocalDate.of(2025,5,5),
                LocalDate.of(2025,5,7), new BigDecimal(600), null);
        reservation.setId(1L);

        //when
        ReservationDto dto = reservationMapper.entityToDto(reservation);

        //then
        assertThat(dto.getCarId()).isNull();
        assertThat(dto.getCustomerId()).isNull();
        assertThat(dto.getStatusId()).isNull();
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

}
