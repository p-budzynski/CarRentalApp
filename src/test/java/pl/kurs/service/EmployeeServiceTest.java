package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.kurs.entity.Employee;
import pl.kurs.entity.Position;
import pl.kurs.exception.DataNotFoundException;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepositoryMock;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void shouldReturnEmployeeById() {
        //given
        Employee testEmployee = createTestEmployee();
        when(employeeRepositoryMock.findById(1L)).thenReturn(Optional.of(testEmployee));

        //when
        Employee result = employeeService.getEmployeeById(1L);

        //then
        assertThat(result).isEqualTo(testEmployee);
    }

    @Test
    void shouldThrowWhenEmployeeNotFoundById() {
        //given
        when(employeeRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found with id: 1");
    }

    @Test
    void shouldReturnPagedEmployeesForGetAll() {
        //given
        Employee testEmployee = createTestEmployee();
        Page<Employee> page = new PageImpl<>(List.of(testEmployee));
        when(employeeRepositoryMock.findAll(PageRequest.of(0, 2))).thenReturn(page);

        //when
        Page<Employee> result = employeeService.getAll(0, 2);

        //then
        assertThat(result.getContent()).containsExactly(testEmployee);
    }

    @Test
    void shouldSaveEmployee() {
        //given
        Employee testEmployee = createTestEmployee();
        when(employeeRepositoryMock.save(testEmployee)).thenReturn(testEmployee);

        //when
        Employee result = employeeService.saveEmployee(testEmployee);

        // then
        assertThat(result).isEqualTo(testEmployee);
    }

    @Test
    void shouldUpdateEmployee() {
        //given
        Employee testEmployee = createTestEmployee();
        Employee incomingEmployee = new Employee("Jane", "Smith", new Position("CEO"), "654321000", "j.smith@gmail.com");
        incomingEmployee.setId(1L);

        when(employeeRepositoryMock.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepositoryMock.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        //when
        Employee result = employeeService.updateEmployee(incomingEmployee);

        //then
        assertThat(result.getFirstName()).isEqualTo(incomingEmployee.getFirstName());
        assertThat(result.getLastName()).isEqualTo(incomingEmployee.getLastName());
        assertThat(result.getPosition().getName()).isEqualTo(incomingEmployee.getPosition().getName());
        assertThat(result.getPhoneNumber()).isEqualTo(incomingEmployee.getPhoneNumber());
        assertThat(result.getEmail()).isEqualTo(incomingEmployee.getEmail());
    }

    @Test
    void shouldThrowWhenUpdatingEmployeeNotFound() {
        //given
        Employee testEmployee = createTestEmployee();
        when(employeeRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> employeeService.updateEmployee(testEmployee))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Employee with id: 1 not found");
    }

    @Test
    void shouldDeleteEmployeeById() {
        //when
        employeeService.deleteEmployeeById(1L);

        //then
        verify(employeeRepositoryMock).deleteById(1L);
    }

    @Test
    void shouldReturnEmployeesByFirstNameAndLastNameAndPosition() {
        //given
        Employee testEmployee = createTestEmployee();
        Page<Employee> page = new PageImpl<>(List.of(testEmployee));
        when(employeeRepositoryMock.findAllByFirstNameAndLastNameAndPosition(
                eq("Jan"), eq("Kowalski"), eq("MANAGER"), any(Pageable.class)))
                .thenReturn(page);

        //when
        Page<Employee> result = employeeService.getByFirstNameAndLastNameAndPosition("Jan", "Kowalski", "MANAGER", 0, 10);

        //then
        assertThat(result.getContent()).containsExactly(testEmployee);
        verify(employeeRepositoryMock).findAllByFirstNameAndLastNameAndPosition(
                eq("Jan"), eq("Kowalski"), eq("MANAGER"), eq(PageRequest.of(0, 10)));
    }

    private Employee createTestEmployee() {
        Employee employee = new Employee("Adam", "Smith", new Position("MECHANIC"), "600500400", "a.smith@gmail.com");
        employee.setId(1L);
        return employee;
    }

}
