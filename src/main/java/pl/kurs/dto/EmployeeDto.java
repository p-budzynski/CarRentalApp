package pl.kurs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.validation.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@XmlRootElement
public class EmployeeDto {
    @NotNull(message = "ID is required", groups = {Update.class, Delete.class})
    @Min(value = 1, message = "ID must be at least 1", groups = {Update.class, Delete.class})
    private Long id;

    @NotBlank(message = "First name must not be blank", groups = {Create.class, Update.class})
    private String firstName;

    @NotBlank(message = "Last name must not be blank", groups = {Create.class, Update.class})
    private String lastName;

    @NotNull(message = "Position ID is required", groups = {Create.class, Update.class})
    @Min(value = 1, message = "Position ID must be at least 1", groups = {Create.class, Update.class})
    private Long positionId;

    @NotBlank(message = "Phone number must not be blank", groups = {Create.class, Update.class})
    @UniqueEmployeePhone(groups = {Create.class, Update.class})
    private String phoneNumber;

    @Email(groups = {Create.class, Update.class})
    @NotBlank(message = "E-mail must not be blank", groups = {Create.class, Update.class})
    @UniqueEmployeeEmail(groups = {Create.class, Update.class})
    private String email;

    public EmployeeDto(String firstName, String lastName, Long positionId, String phoneNumber, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.positionId = positionId;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

}
