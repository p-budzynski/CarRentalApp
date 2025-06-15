package pl.kurs.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.validation.Create;
import pl.kurs.validation.Delete;
import pl.kurs.validation.Update;
import pl.kurs.validation.YearInRange;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@XmlRootElement
public class CarDto {
    @NotNull(message = "ID is required", groups = {Update.class, Delete.class})
    @Min(value = 1, message = "ID must be at least 1", groups = {Update.class, Delete.class})
    private Long id;

    @NotBlank(message = "Producer must not be blank", groups = {Create.class, Update.class})
    private String producer;

    @NotBlank(message = "Model must not be blank", groups = {Create.class, Update.class})
    private String model;

    @NotNull(message = "Year of production must not be null", groups = {Create.class, Update.class})
    @YearInRange
    private Integer yearOfProduction;

    @NotBlank(message = "Registration number must not be blank", groups = {Create.class, Update.class})
    private String registrationNumber;

    @NotNull(message = "Price per day must not be null", groups = {Create.class, Update.class})
    @DecimalMin(value = "0.01", message = "Price per day must be greater than 0", groups = {Create.class, Update.class})
    private BigDecimal pricePerDay;

    public CarDto(String producer, String model, Integer yearOfProduction, String registrationNumber, BigDecimal pricePerDay) {
        this.producer = producer;
        this.model = model;
        this.yearOfProduction = yearOfProduction;
        this.registrationNumber = registrationNumber;
        this.pricePerDay = pricePerDay;
    }
}
