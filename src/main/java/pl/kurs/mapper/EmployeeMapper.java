package pl.kurs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.kurs.dto.EmployeeDto;
import pl.kurs.entity.Employee;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(source = "position.id", target = "positionId")
    EmployeeDto entityToDto(Employee employee);

    List<EmployeeDto> entitiesToDtos(List<Employee> employees);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "position", ignore = true)
    Employee dtoToEntity(EmployeeDto employeeDto);

    @Mapping(target = "position", ignore = true)
    Employee dtoToEntityWithId(EmployeeDto employeeDto);
}
