package pl.kurs.service;

import org.junit.jupiter.api.BeforeEach;
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
    private EmployeeService employeeServiceMock;

    private Employee sampleEmployee;

    @BeforeEach
    void setUp() {
        sampleEmployee = new Employee("Adam", "Smith", new Position("MECHANIC"), "600500400", "a.smith@gmail.com");
        sampleEmployee.setId(1L);
    }

    @Test
    void shouldReturnEmployeeById() {
        //given
        when(employeeRepositoryMock.findById(1L)).thenReturn(Optional.of(sampleEmployee));

        //when
        Employee result = employeeServiceMock.getEmployeeById(1L);

        //then
        assertThat(result).isEqualTo(sampleEmployee);
        verify(employeeRepositoryMock).findById(1L);
    }

    @Test
    void shouldThrowWhenEmployeeNotFoundById() {
        //given
        when(employeeRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> employeeServiceMock.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found with id: 1");
    }

    @Test
    void shouldReturnPagedEmployeesForGetAll() {
        //given
        Page<Employee> page = new PageImpl<>(List.of(sampleEmployee));
        when(employeeRepositoryMock.findAll(PageRequest.of(0, 2))).thenReturn(page);

        //when
        Page<Employee> result = employeeServiceMock.getAll(0, 2);

        //then
        assertThat(result.getContent()).containsExactly(sampleEmployee);
    }

    @Test
    void shouldSaveEmployee() {
        //given
        when(employeeRepositoryMock.save(sampleEmployee)).thenReturn(sampleEmployee);

        //when
        Employee result = employeeServiceMock.saveEmployee(sampleEmployee);

        // then
        assertThat(result).isEqualTo(sampleEmployee);
        verify(employeeRepositoryMock).save(sampleEmployee);
    }

    @Test
    void shouldUpdateEmployee() {
        //given
        Employee incoming = new Employee("Jane", "Smith", new Position("CEO"), "654321000", "j.smith@gmail.com");
        incoming.setId(1L);

        when(employeeRepositoryMock.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(employeeRepositoryMock.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        //when
        Employee result = employeeServiceMock.updateEmployee(incoming);

        //then
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getPosition().getName()).isEqualTo("CEO");
        assertThat(result.getPhoneNumber()).isEqualTo("654321000");
        assertThat(result.getEmail()).isEqualTo("j.smith@gmail.com");
        verify(employeeRepositoryMock).save(sampleEmployee);
    }

    @Test
    void shouldThrowWhenUpdatingEmployeeNotFound() {
        //given
        when(employeeRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> employeeServiceMock.updateEmployee(sampleEmployee))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Employee with id: 1 not found");
    }

    @Test
    void shouldDeleteEmployeeById() {
        //when
        employeeServiceMock.deleteEmployeeById(1L);

        //then
        verify(employeeRepositoryMock).deleteById(1L);
    }

    @Test
    void shouldReturnEmployeesByFirstNameAndLastNameAndPosition() {
        //given
        Page<Employee> page = new PageImpl<>(List.of(sampleEmployee));
        when(employeeRepositoryMock.findAllByFirstNameAndLastNameAndPosition(
                eq("Jan"), eq("Kowalski"), eq("MANAGER"), any(Pageable.class)))
                .thenReturn(page);

        //when
        Page<Employee> result = employeeServiceMock.getByFirstNameAndLastNameAndPosition("Jan", "Kowalski", "MANAGER", 0, 10);

        //then
        assertThat(result.getContent()).containsExactly(sampleEmployee);
        verify(employeeRepositoryMock).findAllByFirstNameAndLastNameAndPosition(
                eq("Jan"), eq("Kowalski"), eq("MANAGER"), eq(PageRequest.of(0,10)));
    }

}
