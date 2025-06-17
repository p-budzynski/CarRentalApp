package pl.kurs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "e_mail", nullable = false, unique = true)
    @Email
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    @Pattern(regexp = "\\d{9}")
    private String phoneNumber;

    @Column(name = "driving_license_number", nullable = false, unique = true)
    private String drivingLicenseNumber;

    @OneToMany(mappedBy = "customer", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Reservation> reservations;

    public Customer(String firstName, String lastName, String email, String phoneNumber, String drivingLicenseNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    @Override
    public String toString() {
        return "Customer [id: " + id + ", first name: " + firstName + ", last name: " + lastName +
               ",\ne-mail: " + email + ", phone number: " + phoneNumber + ", driving license number: " +
               drivingLicenseNumber + "]";
    }
}
