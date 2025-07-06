package pl.kurs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.kurs.entity.Reservation;

import java.time.LocalDate;
import java.util.List;

@Component
public class CarRentalReminderService {
    private final static String MANAGER_E_MAIL = "manager@wypozyczalnia.pl";

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private JavaMailSender mailSender;

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Reservation> tomorrowRentals = reservationService.findActiveReservationForDate(tomorrow);

        for (Reservation reservation : tomorrowRentals) {
            sendCustomerReminder(reservation);
            sendEmployeeReminder(reservation);
        }
    }

    private void sendCustomerReminder(Reservation reservation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(reservation.getCustomer().getEmail());
        message.setSubject("Car rental reminder");
        message.setText(createCustomerReminderText(reservation));
        message.setFrom("noreply@wypozyczalnia.pl");

        mailSender.send(message);
    }

    private void sendEmployeeReminder(Reservation reservation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(MANAGER_E_MAIL);
        message.setSubject("Car handover reminder");
        message.setText(createEmployeeReminderText(reservation));
        message.setFrom("system@wypozyczalnia.pl");

        mailSender.send(message);
    }

    private String createCustomerReminderText(Reservation reservation) {
        return String.format(
                "Hi %s!\n\n" +
                "We remind you about tomorrow's car rental!\n\n" +
                "Car: %s %s\n" +
                "Reservation id: %s\n\n" +
                "Please arrive on time with your documents:\n" +
                "- driving license number: %s\n\n" +
                "If you have any questions, please contact us.\n\n" +
                "Best regards,\n" +
                "Rental Team!",
                reservation.getCustomer().getFirstName(),
                reservation.getCar().getProducer(),
                reservation.getCar().getModel(),
                reservation.getId(),
                reservation.getCustomer().getDrivingLicenseNumber()
        );
    }

    private String createEmployeeReminderText(Reservation reservation) {
        return String.format(
                "Hi,\n\n" +
                "Reminder about tomorrow's car release:\n\n" +
                "Customer: %s %s\n" +
                "- phone number: %s\n" +
                "- driving license number: %s\n" +
                "Car: %s %s (registration number: %s)\n" +
                "Reservation id: %s\n\n" +
                "Rental System!",
                reservation.getCustomer().getFirstName(),
                reservation.getCustomer().getLastName(),
                reservation.getCustomer().getPhoneNumber(),
                reservation.getCustomer().getDrivingLicenseNumber(),
                reservation.getCar().getProducer(),
                reservation.getCar().getModel(),
                reservation.getCar().getRegistrationNumber(),
                reservation.getId()
        );
    }

}
