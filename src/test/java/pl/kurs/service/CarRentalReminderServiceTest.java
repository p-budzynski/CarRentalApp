package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import pl.kurs.entity.Car;
import pl.kurs.entity.Customer;
import pl.kurs.entity.Reservation;
import pl.kurs.entity.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class CarRentalReminderServiceTest {

    @Mock
    private ReservationService reservationServiceMock;

    @Mock
    private JavaMailSender mailSenderMock;

    @InjectMocks
    private CarRentalReminderService reminderService;

    @Test
    void shouldSendRemindersForReservationsOnNextDay() {
        // given
        ReflectionTestUtils.setField(reminderService, "managerEmail", "manager@test.com");
        ReflectionTestUtils.setField(reminderService, "noReplyMail", "noreply@test.com");
        ReflectionTestUtils.setField(reminderService, "systemMail", "system@test.com");

        Reservation reservation = createSampleReservation();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        when(reservationServiceMock.findActiveReservationForDate(tomorrow)).thenReturn(List.of(reservation));

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // when
        reminderService.sendDailyReminders();

        // then
        verify(mailSenderMock, times(2)).send(messageCaptor.capture());

        List<SimpleMailMessage> sentMessages = messageCaptor.getAllValues();

        SimpleMailMessage customerMail = sentMessages.getFirst();
        assertThat(customerMail.getTo()).contains("john@example.com");
        assertThat(customerMail.getFrom()).isEqualTo("noreply@test.com");
        assertThat(customerMail.getSubject()).contains("Car rental reminder");
        assertThat(customerMail.getText()).contains("John");

        SimpleMailMessage employeeMail = sentMessages.get(1);
        assertThat(employeeMail.getTo()).contains("manager@test.com");
        assertThat(employeeMail.getFrom()).isEqualTo("system@test.com");
        assertThat(employeeMail.getSubject()).contains("Car handover reminder");
        assertThat(employeeMail.getText()).contains("John Doe");
        assertThat(employeeMail.getText()).contains("XYZ123");
    }

    @Test
    void shouldNotSendEmailIfNoReservations() {
        // given
        when(reservationServiceMock.findActiveReservationForDate(any())).thenReturn(Collections.emptyList());

        // when
        reminderService.sendDailyReminders();

        // then
        verify(mailSenderMock, never()).send(any(SimpleMailMessage.class));
    }

    private Reservation createSampleReservation() {
        Customer customer = new Customer("John", "Doe", "john@example.com", "123456789", "DL123456");
        Car car = new Car("Toyota", "Corolla", 2022, "XYZ123", new BigDecimal(200));

        Reservation reservation = new Reservation(car, customer, LocalDate.of(2025, 8, 1),
                LocalDate.of(2025, 8, 5), new BigDecimal(1000), Status.RESERVED);
        reservation.setId(1L);

        return reservation;
    }

}