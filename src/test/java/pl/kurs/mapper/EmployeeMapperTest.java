package pl.kurs.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kurs.dto.EmployeeDto;
import pl.kurs.dto.ReservationDto;
import pl.kurs.entity.Employee;
import pl.kurs.entity.Position;
import pl.kurs.entity.Reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EmployeeMapperTest {
    private EmployeeMapper employeeMapper;
    private Employee sampleEmployee;
    private EmployeeDto sampleEmployeeDto;

    @BeforeEach
    void setUp() {
        employeeMapper = Mappers.getMapper(EmployeeMapper.class);
        Position position = new Position();
        position.setId(1L);
        sampleEmployee = new Employee("Jaś", "Fasola", position, "521522523","j.fasola@gmail.com");
        sampleEmployeeDto = new EmployeeDto("Jaś", "Fasola", 1L, "521522523","j.fasola@gmail.com");
    }

    @Test
    void shouldMapEntityToDto() {
        //when
        EmployeeDto dto = employeeMapper.entityToDto(sampleEmployee);

        //then
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(sampleEmployeeDto);
    }

    @Test
    void shouldMapEntityListToDtoList() {
        //given
        List<Employee> employees = List.of(sampleEmployee);

        //when
        List<EmployeeDto> dtos = employeeMapper.entitiesToDtos(employees);

        //then
        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(sampleEmployeeDto);
    }

    @Test
    void shouldMapDtoToEntity() {
        //when
        Employee entity = employeeMapper.dtoToEntity(sampleEmployeeDto);

        //then
        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("position")
                .isEqualTo(sampleEmployee);
    }

    @Test
    void shouldMapDtoToEntityWithId() {
        //given
        sampleEmployeeDto.setId(1L);
        sampleEmployee.setId(1L);

        //when
        Employee entity = employeeMapper.dtoToEntityWithId(sampleEmployeeDto);

        //then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("position")
                .isEqualTo(sampleEmployee);
    }

    @Test
    void shouldReturnNullPositionIdsWhenFieldsAreNull() {
        //given
        Employee employee = new Employee("Jan", "Kowalski", null, "658721349", "j.kowal@o2.pl");
        employee.setId(1L);

        //when
        EmployeeDto dto = employeeMapper.entityToDto(employee);

        //then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getPositionId()).isNull();
        assertThat(dto.getFirstName()).isEqualTo("Jan");
        assertThat(dto.getLastName()).isEqualTo("Kowalski");
        assertThat(dto.getPhoneNumber()).isEqualTo("658721349");
        assertThat(dto.getEmail()).isEqualTo("j.kowal@o2.pl");
    }

    @Test
    void shouldReturnNullWhenDtoToEntityWithIdGivenNull() {
        //when then
        assertThat(employeeMapper.dtoToEntityWithId(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityToDtoGivenNull() {
        //when then
        assertThat(employeeMapper.entityToDto(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenDtoToEntityGivenNull() {
        //when then
        assertThat(employeeMapper.dtoToEntity(null)).isNull();
    }

    @Test
    void shouldReturnEmptyListWhenEntitiesToDtosGivenNull() {
        //when then
        assertThat(employeeMapper.entitiesToDtos(null)).isNull();
    }

}
