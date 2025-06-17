package pl.kurs.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
@NoArgsConstructor
@Getter
@Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_amount", precision = 8, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    public Reservation(Car car, Customer customer, LocalDate startDate, LocalDate endDate, BigDecimal totalAmount, Status status) {
        this.car = car;
        this.customer = customer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reservation [id: " + id + ", car id: " + car.getId() + ", customer id: " + customer.getId() +
               ",\nstart date: " + startDate + ", end date: " + endDate +
               ",\ntotal amount: " + totalAmount + ", status: " + status.getName() + "]";
    }
}
