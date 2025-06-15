package pl.kurs.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.entity.Position;
import pl.kurs.validation.Create;
import pl.kurs.validation.Delete;
import pl.kurs.validation.Update;

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

    @JsonIgnore
    private Position position;

    @NotBlank(message = "Phone number must not be blank", groups = {Create.class, Update.class})
    private String phoneNumber;

    @NotBlank(message = "E-mail must not be blank", groups = {Create.class, Update.class})
    private String email;

    public EmployeeDto(String firstName, String lastName, Position position, String phoneNumber, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

}
