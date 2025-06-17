package pl.kurs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.validation.Create;
import pl.kurs.validation.Delete;
import pl.kurs.validation.EndDateAfterStartDate;
import pl.kurs.validation.Update;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@XmlRootElement
@EndDateAfterStartDate(groups = {Create.class, Update.class})
public class ReservationDto {
    @NotNull(message = "ID is required", groups = {Update.class, Delete.class})
    @Min(value = 1, message = "ID must be at least 1", groups = {Update.class, Delete.class})
    private Long id;

    @NotNull(message = "Car ID is required", groups = {Create.class, Update.class})
    @Min(value = 1, message = "Car ID must be at least 1", groups = {Create.class, Update.class})
    private Long carId;

    @NotNull(message = "Customer ID is required", groups = {Create.class, Update.class})
    @Min(value = 1, message = "Customer ID must be at least 1", groups = {Create.class, Update.class})
    private Long customerId;

    @NotNull(message = "Start date is required", groups = {Create.class, Update.class})
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    @NotNull(message = "End date is required", groups = {Create.class, Update.class})
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;

    @NotNull(message = "Total amount must not be null", groups = {Create.class, Update.class})
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0", groups = {Create.class, Update.class})
    private BigDecimal totalAmount;

    @NotNull(message = "Status ID is required", groups = {Create.class, Update.class})
    @Min(value = 1, message = "Status ID must be at least 1", groups = {Create.class, Update.class})
    private Long statusId;

    public ReservationDto(Long carId, Long customerId, LocalDate startDate, LocalDate endDate, BigDecimal totalAmount, Long statusId) {
        this.carId = carId;
        this.customerId = customerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAmount = totalAmount;
        this.statusId = statusId;
    }

}
