package pl.kurs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "cars")
@NoArgsConstructor
@Getter
@Setter
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String producer;

    @Column(nullable = false)
    private String model;

    @Column(name = "year_of_production", nullable = false)
    private Integer yearOfProduction;

    @Column(name = "registration_number", length = 10, nullable = false, unique = true)
    private String registrationNumber;

    @Column(name = "price_per_day", precision = 6, scale = 2, nullable = false)
    private BigDecimal pricePerDay;

    @OneToMany(mappedBy = "car", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnore
    private List<Reservation> reservations;

    public Car(String producer, String model, Integer yearOfProduction, String registrationNumber, BigDecimal pricePerDay) {
        this.producer = producer;
        this.model = model;
        this.yearOfProduction = yearOfProduction;
        this.registrationNumber = registrationNumber;
        this.pricePerDay = pricePerDay;
    }

    @Override
    public String toString() {
        return "Car [id: " + id + ", producer: " + producer + ", model: " + model + ", year of production: " + yearOfProduction +
               ",\nregistration number: " + registrationNumber + ", price per day: " + pricePerDay + "]";
    }
}
