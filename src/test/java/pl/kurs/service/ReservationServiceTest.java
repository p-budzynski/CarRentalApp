package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pl.kurs.dto.ReservationDto;
import pl.kurs.entity.Car;
import pl.kurs.entity.Customer;
import pl.kurs.entity.Reservation;
import pl.kurs.entity.Status;
import pl.kurs.exception.ConflictException;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.mapper.ReservationMapper;
import pl.kurs.repository.ReservationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    private static final LocalDate FROM = LocalDate.of(2025, 7, 1);
    private static final LocalDate TO = LocalDate.of(2025, 7, 10);

    @Mock
    private ReservationRepository reservationRepositoryMock;

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationMapper reservationMapperMock;

    @Mock
    private CarService carService;

    @Mock
    private CustomerService customerService;

    @Test
    void shouldReturnReservationWhenGetById() {
        //given
        Reservation testReservation = createTestReservation();
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(testReservation));

        //when
        Reservation result = reservationService.getReservationById(1L);

        //then
        assertThat(result).isEqualTo(testReservation);
    }

    @Test
    void shouldThrowWhenReservationNotFoundById() {
        //given
        when(reservationRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> reservationService.getReservationById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reservation not found with id: 99");
    }

    @Test
    void shouldReturnPagedReservationsWhenGetAll() {
        //given
        Reservation testReservation = createTestReservation();
        Page<Reservation> page = new PageImpl<>(List.of(testReservation));
        when(reservationRepositoryMock.findAll(PageRequest.of(0, 2))).thenReturn(page);

        //when
        Page<Reservation> result = reservationService.getAll(0, 2);

        //then
        assertThat(result.getContent()).containsExactly(testReservation);
    }

    @Test
    void shouldSaveReservation() {
        //given
        Reservation testReservation = createTestReservation();
        Car testCar = createTestCar();
        when(carService.findForUpdate(1L)).thenReturn(testCar);
        when(reservationRepositoryMock.existsOverlap(1L, FROM, TO)).thenReturn(false);
        when(reservationRepositoryMock.save(testReservation)).thenReturn(testReservation);

        //when
        Reservation savedReservation = reservationService.saveReservation(testReservation);

        //then
        assertThat(savedReservation).isEqualTo(testReservation);
    }

    @Test
    void shouldThrowWhenSaveReservationWithOverlap() {
        //given
        Reservation testReservation = createTestReservation();
        when(carService.findForUpdate(1L)).thenReturn(createTestCar());
        when(reservationRepositoryMock.existsOverlap(1L, FROM, TO)).thenReturn(true);

        //when then
        assertThatThrownBy(() -> reservationService.saveReservation(testReservation))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Car already booked for this date");
    }

    @Test
    void shouldUpdateReservation() {
        // given
        Reservation testReservation = createTestReservation();
        ReservationDto testReservationDto = new ReservationDto(1L, 1L, 1L, FROM.plusDays(2),
                TO.plusDays(5), new BigDecimal(500), "RESERVED");
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepositoryMock.existsOverlapExcludingReservation(1L, FROM.plusDays(2),
                TO.plusDays(5), 1L)).thenReturn(false);
        when(reservationRepositoryMock.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        Reservation updatedReservation = reservationService.updateReservation(testReservationDto);

        // then
        assertThat(updatedReservation.getStartDate()).isEqualTo(testReservationDto.getStartDate());
        assertThat(updatedReservation.getEndDate()).isEqualTo(testReservationDto.getEndDate());
        assertThat(updatedReservation.getTotalAmount()).isEqualByComparingTo(testReservationDto.getTotalAmount());
        assertThat(updatedReservation.getStatus().getValue()).isEqualTo(testReservationDto.getStatusName());
    }

    @Test
    void shouldThrowForUpdateReservationWhenReservationIsCanceled() {
        // given
        Reservation testReservation = createTestReservation();
        testReservation.setStatus(Status.CANCELED);
        ReservationDto testReservationDto = new ReservationDto(1L, 1L, 1L,
                FROM, TO, new BigDecimal(800), "RESERVED");
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(testReservation));

        // when then
        assertThatThrownBy(() -> reservationService.updateReservation(testReservationDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Cannot update canceled reservation");
    }

    @Test
    void shouldThrowForUpdateReservationWhenCarChanged() {
        // given
        Reservation testReservation = createTestReservation();
        ReservationDto testReservationDto = new ReservationDto(1L, 2L, 1L,
                FROM, TO, new BigDecimal(800), "RESERVED");
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(testReservation));

        // when then
        assertThatThrownBy(() -> reservationService.updateReservation(testReservationDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Cannot change car or customer via update. Cancel and create new reservation.");
    }

    @Test
    void shouldThrowForUpdateReservationWhenCustomerChanged() {
        // given
        Reservation testReservation = createTestReservation();
        ReservationDto testReservationDto = new ReservationDto(1L, 1L, 2L,
                FROM, TO, new BigDecimal(800), "RESERVED");
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(testReservation));

        // when then
        assertThatThrownBy(() -> reservationService.updateReservation(testReservationDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Cannot change car or customer via update. Cancel and create new reservation.");
    }

    @Test
    void shouldThrowForUpdateReservationWhenDateOverlapDetected() {
        // given
        Reservation testReservation = createTestReservation();
        ReservationDto testReservationDto = new ReservationDto(1L, 1L, 1L,
                FROM, TO, new BigDecimal(800), "RESERVED");
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepositoryMock.existsOverlapExcludingReservation(1L, FROM, TO, 1L))
                .thenReturn(true);

        // when then
        assertThatThrownBy(() -> reservationService.updateReservation(testReservationDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Car already booked");
    }

    @Test
    void shouldCancelReservationById() {
        //given
        Reservation testReservation = createTestReservation();
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepositoryMock.save(any(Reservation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        //when
        Reservation canceledReservation = reservationService.cancelReservationById(1L);

        //then
        assertThat(canceledReservation.getStatus()).isEqualTo(Status.CANCELED);
    }

    @Test
    void shouldCreateReservationSuccessfully() {
        // given
        ReservationDto testReservationDto = new ReservationDto(1L, 1L, 1L, FROM, TO,
                BigDecimal.valueOf(500), "RESERVED");

        Reservation testReservation = createTestReservation();
        Car testCar = testReservation.getCar();
        Customer testCustomer = testReservation.getCustomer();

        when(carService.getCarById(1L)).thenReturn(testCar);
        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(reservationMapperMock.dtoToEntityWithId(testReservationDto)).thenReturn(testReservation);

        // when
        Reservation result = reservationService.createReservation(testReservationDto);

        // then
        assertThat(result.getCar()).isEqualTo(testCar);
        assertThat(result.getCustomer()).isEqualTo(testCustomer);
        assertThat(result.getStatus()).isEqualTo(Status.RESERVED);
    }

    @Test
    void shouldFindActiveReservationsForDate() {
        // given
        List<Reservation> activeReservations = List.of(new Reservation(), new Reservation());

        when(reservationRepositoryMock.findActiveReservationForDate(FROM)).thenReturn(activeReservations);

        // when
        List<Reservation> result = reservationService.findActiveReservationForDate(FROM);

        // then
        assertThat(result).hasSize(2);
    }

    private Car createTestCar() {
        Car car = new Car("BMW", "135i", 2022, "P0 WOLNY", new BigDecimal(600));
        car.setId(1L);
        return car;
    }

    private Customer createTestCustomer() {
        Customer customer = new Customer("Jan", "Kowalski", "j.kowal@gmail.com", "505606707", "PPX 12512");
        customer.setId(1L);
        return customer;
    }

    private Reservation createTestReservation() {
        Reservation reservation = new Reservation(createTestCar(), createTestCustomer(), FROM, TO, new BigDecimal(5400), Status.RESERVED);
        reservation.setId(1L);
        return reservation;
    }

}
