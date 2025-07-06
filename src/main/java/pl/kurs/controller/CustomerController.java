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
import pl.kurs.dto.CustomerDto;
import pl.kurs.entity.Customer;
import pl.kurs.mapper.CustomerMapper;
import pl.kurs.service.CustomerService;
import pl.kurs.validation.Create;
import pl.kurs.validation.Update;

@Validated
@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController {
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "10";
    private static final int MAX_SIZE = 100;

    private CustomerService customerService;
    private CustomerMapper customerMapper;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CustomerDto> getById(@PathVariable("id") @Min(value = 1, message = "ID must be greater than zero!") Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customerMapper.entityToDto(customer));
    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Page<CustomerDto> getAll(@RequestParam(defaultValue = DEFAULT_PAGE) @Min(0) int page,
                                    @RequestParam(defaultValue = DEFAULT_SIZE) @Min(1) @Max(MAX_SIZE) int size) {

        Page<Customer> customers = customerService.getAll(page, size);
        return customers.map(customerMapper::entityToDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto createCustomer(@RequestBody @Validated(Create.class) CustomerDto customerDto) {
        Customer customer = customerMapper.dtoToEntity(customerDto);
        Customer savedCustomer = customerService.saveCustomer(customer);
        return customerMapper.entityToDto(savedCustomer);
    }

    @PutMapping
    public CustomerDto updateCustomer(@RequestBody @Validated(Update.class) CustomerDto customerDto) {
        Customer customer = customerMapper.dtoToEntityWithId(customerDto);
        Customer updatedCustomer = customerService.updateCustomer(customer);
        return customerMapper.entityToDto(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") @Min(value = 1, message = "ID must be greater than zero!") Long id) {
        customerService.deleteCustomerById(id);
    }

    @GetMapping("/search")
    public Page<CustomerDto> getByParams(
            @RequestParam(defaultValue = DEFAULT_PAGE) @Min(0) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) @Min(1) @Max(MAX_SIZE) int size,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName) {

        Page<Customer> customers = customerService.getByFirstNameAndLastName(firstName, lastName, page, size);
        return customers.map(customerMapper::entityToDto);
    }
}
