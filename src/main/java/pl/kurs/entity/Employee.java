package pl.kurs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees")
@NoArgsConstructor
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @Column(name = "phone_number", nullable = false, unique = true)
    @Pattern(regexp = "\\d{9}")
    private String phoneNumber;

    @Column(name = "e_mail", nullable = false, unique = true)
    @Email
    private String email;

    public Employee(String firstName, String lastName, Position position, String phoneNumber, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Employee [id: " + id + ", first name: " + firstName + ", last name: " + lastName +
               ",\nposition: " + position.getName() + ", phone number: " + phoneNumber + ", e-mail: " + email + "]";
    }
}
