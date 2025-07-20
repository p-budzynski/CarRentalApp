package pl.kurs.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kurs.dto.EmployeeDto;
import pl.kurs.entity.Employee;
import pl.kurs.entity.Position;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EmployeeMapperTest {
    private final EmployeeMapper employeeMapper = Mappers.getMapper(EmployeeMapper.class);

    @Test
    void shouldMapEntityToDto() {
        //given
        Employee testEmployee = createTestEmployee();
        EmployeeDto testEmployeeDto = createTestEmployeeDto();

        //when
        EmployeeDto dto = employeeMapper.entityToDto(testEmployee);

        //then
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(testEmployeeDto);
    }

    @Test
    void shouldMapEntityListToDtoList() {
        //given
        Employee testEmployee = createTestEmployee();
        EmployeeDto testEmployeeDto = createTestEmployeeDto();
        List<Employee> employees = List.of(testEmployee);

        //when
        List<EmployeeDto> dtos = employeeMapper.entitiesToDtos(employees);

        //then
        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(testEmployeeDto);
    }

    @Test
    void shouldMapDtoToEntity() {
        //when
        Employee testEmployee = createTestEmployee();
        EmployeeDto testEmployeeDto = createTestEmployeeDto();
        Employee entity = employeeMapper.dtoToEntity(testEmployeeDto);

        //then
        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("position")
                .isEqualTo(testEmployee);
    }

    @Test
    void shouldMapDtoToEntityWithId() {
        //given
        Employee testEmployee = createTestEmployee();
        testEmployee.setId(1L);
        EmployeeDto testEmployeeDto = createTestEmployeeDto();
        testEmployeeDto.setId(1L);

        //when
        Employee entity = employeeMapper.dtoToEntityWithId(testEmployeeDto);

        //then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("position")
                .isEqualTo(testEmployee);
    }

    @Test
    void shouldReturnNullPositionIdsWhenFieldsAreNull() {
        //given
        Employee testEmployee = createTestEmployee();
        testEmployee.setPosition(null);

        //when
        EmployeeDto dto = employeeMapper.entityToDto(testEmployee);

        //then
        assertThat(dto.getPositionId()).isNull();
        assertThat(dto.getFirstName()).isEqualTo(testEmployee.getFirstName());
        assertThat(dto.getLastName()).isEqualTo(testEmployee.getLastName());
        assertThat(dto.getPhoneNumber()).isEqualTo(testEmployee.getPhoneNumber());
        assertThat(dto.getEmail()).isEqualTo(testEmployee.getEmail());
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

    private Employee createTestEmployee() {
        return new Employee("Jaś", "Fasola", new Position(1L, "mechanic"), "521522523","j.fasola@gmail.com");
    }

    private EmployeeDto createTestEmployeeDto() {
        return new EmployeeDto("Jaś", "Fasola", 1L, "521522523","j.fasola@gmail.com");
    }

}
