package pl.kurs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pl.kurs.entity.Car;
import pl.kurs.entity.Customer;
import pl.kurs.entity.Reservation;
import pl.kurs.entity.Status;
import pl.kurs.exception.ConflictException;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.repository.ReservationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepositoryMock;

    @Mock
    private StatusService statusServiceMock;

    @Mock
    private CarService carServiceMock;

    @InjectMocks
    private ReservationService reservationServiceMock;

    private Car sampleCar;
    private Customer sampleCustomer;
    private Status statusBooked;
    private Status statusCancelled;
    private Reservation sampleReservation;
    private LocalDate from;
    private LocalDate to;

    @BeforeEach
    void setUp() {
        sampleCar = new Car("BMW", "135i", 2022, "P0 WOLNY", new BigDecimal(600));
        sampleCar.setId(1L);
        sampleCustomer = new Customer("Jan", "Kowalski", "j.kowal@gmail.com", "505606707", "PPX 12512");
        sampleCustomer.setId(1L);
        statusBooked = new Status("BOOKED");
        statusBooked.setId(1L);
        statusCancelled = new Status("CANCELLED");
        statusCancelled.setId(2L);
        from = LocalDate.of(2025, 7, 1);
        to = LocalDate.of(2025, 7, 10);
        sampleReservation = new Reservation(sampleCar, sampleCustomer, from, to, new BigDecimal(5400), statusBooked);
        sampleReservation.setId(1L);
    }

    @Test
    void shouldReturnReservationWhenGetById() {
        //given
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(sampleReservation));

        //when
        Reservation result = reservationServiceMock.getReservationById(1L);

        //then
        assertThat(result).isEqualTo(sampleReservation);
    }

    @Test
    void shouldThrowWhenReservationNotFoundById() {
        //given
        when(reservationRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> reservationServiceMock.getReservationById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reservation not found with id: 99");
    }

    @Test
    void shouldReturnPagedReservationsWhenGetAll() {
        //given
        Page<Reservation> page = new PageImpl<>(List.of(sampleReservation));
        when(reservationRepositoryMock.findAll(PageRequest.of(0, 2))).thenReturn(page);

        //when
        Page<Reservation> result = reservationServiceMock.getAll(0, 2);

        //then
        assertThat(result.getContent()).containsExactly(sampleReservation);
    }

    @Test
    void shouldSaveReservation() {
        //given
        when(carServiceMock.findForUpdate(1L)).thenReturn(sampleCar);
        when(reservationRepositoryMock.existsOverlap(1L, from, to)).thenReturn(false);
        when(reservationRepositoryMock.save(sampleReservation)).thenReturn(sampleReservation);

        //when
        Reservation saved = reservationServiceMock.saveReservation(sampleReservation);

        //then
        assertThat(saved).isEqualTo(sampleReservation);
        verify(reservationRepositoryMock).save(sampleReservation);
    }

    @Test
    void shouldThrowWhenSaveReservationWithOverlap() {
        //given
        when(carServiceMock.findForUpdate(1L)).thenReturn(sampleCar);
        when(reservationRepositoryMock.existsOverlap(1L, from, to)).thenReturn(true);

        //when then
        assertThatThrownBy(() -> reservationServiceMock.saveReservation(sampleReservation))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Car already booked for this date");
    }

    @Test
    void shouldUpdateReservation() {
        //given
        Reservation incoming = new Reservation(sampleCar, sampleCustomer,
                LocalDate.of(2025, 7, 2),
                LocalDate.of(2025, 7, 8),
                new BigDecimal(3600), statusBooked);
        incoming.setId(1L);

        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(sampleReservation));
        when(carServiceMock.findForUpdate(1L)).thenReturn(sampleCar);
        when(reservationRepositoryMock.existsOverlap(1L, incoming.getStartDate(), incoming.getEndDate()))
                .thenReturn(false);
        when(reservationRepositoryMock.save(any(Reservation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        //when
        Reservation updated = reservationServiceMock.updateReservation(incoming);

        //then
        assertThat(updated.getStartDate()).isEqualTo(LocalDate.of(2025, 7, 2));
        assertThat(updated.getEndDate()).isEqualTo(LocalDate.of(2025, 7, 8));
        verify(reservationRepositoryMock).save(sampleReservation);
    }

    @Test
    void shouldThrowWhenUpdateReservationWithOverlap() {
        //given
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(sampleReservation));
        when(carServiceMock.findForUpdate(1L)).thenReturn(sampleCar);
        when(reservationRepositoryMock.existsOverlap(1L, from, to)).thenReturn(true);

        //when then
        assertThatThrownBy(() -> reservationServiceMock.updateReservation(sampleReservation))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Car already booked for this date");
    }

    @Test
    void shouldThrowWhenUpdateReservationNotFound() {
        //given
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> reservationServiceMock.updateReservation(sampleReservation))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reservation not found with id: 1");
    }

    @Test
    void shouldCancelReservationById() {
        //given
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(sampleReservation));
        when(statusServiceMock.findByName("CANCELLED")).thenReturn(statusCancelled);
        when(reservationRepositoryMock.save(any(Reservation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        //when
        Reservation cancelled = reservationServiceMock.cancelReservationById(1L);

        //then
        assertThat(cancelled.getStatus()).isEqualTo(statusCancelled);
        verify(reservationRepositoryMock).save(sampleReservation);
    }

}
