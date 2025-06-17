package pl.kurs.dto;

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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@XmlRootElement
public class CustomerDto {
    @NotNull(message = "ID is required", groups = {Update.class, Delete.class})
    @Min(value = 1, message = "ID must be at least 1", groups = {Update.class, Delete.class})
    private Long id;

    @NotBlank(message = "First name must not be blank", groups = {Create.class, Update.class})
    private String firstName;

    @NotBlank(message = "Last name must not be blank", groups = {Create.class, Update.class})
    private String lastName;

    @NotBlank(message = "E-mail must not be blank", groups = {Create.class, Update.class})
    private String email;

    @NotBlank(message = "Phone number must not be blank", groups = {Create.class, Update.class})
    private String phoneNumber;

    @NotBlank(message = "Driving license number must not be blank", groups = {Create.class, Update.class})
    private String drivingLicenseNumber;

    public CustomerDto(String firstName, String lastName, String email, String phoneNumber, String drivingLicenseNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.drivingLicenseNumber = drivingLicenseNumber;
    }
}
