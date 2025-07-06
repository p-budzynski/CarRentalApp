package pl.kurs.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.kurs.dto.EmployeeDto;
import pl.kurs.entity.Employee;
import pl.kurs.mapper.EmployeeMapper;
import pl.kurs.service.EmployeeService;
import pl.kurs.validation.Create;
import pl.kurs.validation.Update;

@Validated
@RestController
@RequestMapping("/employees")
@AllArgsConstructor
public class EmployeeController {
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "10";
    private static final int MAX_SIZE = 100;

    private EmployeeService employeeService;
    private EmployeeMapper employeeMapper;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EmployeeDto> getById(@PathVariable("id") @Min(value = 1, message = "ID must be greater than zero!") Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employeeMapper.entityToDto(employee));
    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Page<EmployeeDto> getAll(@RequestParam(defaultValue = DEFAULT_PAGE) @Min(0) int page,
                                    @RequestParam(defaultValue = DEFAULT_SIZE) @Min(1) @Max(MAX_SIZE) int size) {
        Page<Employee> employees = employeeService.getAll(page, size);
        return employees.map(employeeMapper::entityToDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto createEmployee(@RequestBody @Validated(Create.class) EmployeeDto employeeDto) {
        Employee employee = employeeService.createEmployee(employeeDto);
        Employee savedEmployee = employeeService.saveEmployee(employee);
        return employeeMapper.entityToDto(savedEmployee);
    }

    @PutMapping
    public EmployeeDto updateEmployee(@RequestBody @Validated(Update.class) EmployeeDto employeeDto) {
        Employee employee = employeeService.createEmployee(employeeDto);
        Employee updatedEmployee = employeeService.updateEmployee(employee);
        return employeeMapper.entityToDto(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") @Min(value = 1, message = "ID must be greater than zero!") Long id) {
        employeeService.deleteEmployeeById(id);
    }

    @GetMapping("/search")
    public Page<EmployeeDto> getByParams(
            @RequestParam(defaultValue = DEFAULT_PAGE) @Min(0) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) @Min(1) @Max(MAX_SIZE) int size,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "position", required = false) String position) {

        Page<Employee> employees = employeeService.getByFirstNameAndLastNameAndPosition(firstName, lastName, position, page, size);
        return employees.map(employeeMapper::entityToDto);
    }

}
