package pl.kurs.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.kurs.entity.Employee;
import pl.kurs.exception.DataNotFoundException;
import pl.kurs.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Page<Employee> getAll(int page, int size) {
        return employeeRepository.findAll(PageRequest.of(page, size));
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Employee employee) {
        Employee employeeToUpdate = employeeRepository.findById(employee.getId())
                .orElseThrow(() -> new DataNotFoundException("Employee with id: " + employee.getId() + " not found"));
        BeanUtils.copyProperties(employee, employeeToUpdate);
        return employeeRepository.save(employeeToUpdate);
    }

    public void deleteEmployeeById(Long id) {
        employeeRepository.deleteById(id);
    }

    public Page<Employee> getByFirstNameAndLastNameAndPosition(String firstName, String lastName, String position, int page, int size) {
        return employeeRepository.findAllByFirstNameAndLastNameAndPosition(firstName, lastName, position, PageRequest.of(page, size));
    }
}
