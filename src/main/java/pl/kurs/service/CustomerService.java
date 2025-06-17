package pl.kurs.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.kurs.entity.Customer;
import pl.kurs.exception.DataNotFoundException;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.repository.CustomerRepository;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    public Page<Customer> getAll(int page, int size) {
        return customerRepository.findAll(PageRequest.of(page, size));
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Customer customer) {
        Customer customerToUpdate = customerRepository.findById(customer.getId())
                .orElseThrow(() -> new DataNotFoundException("Customer with id: " + customer.getId() + " not found"));
        BeanUtils.copyProperties(customer, customerToUpdate);
        return customerRepository.save(customerToUpdate);
    }

    public void deleteCustomerById(Long id) {
        customerRepository.deleteById(id);
    }

    public Page<Customer> getByFirstNameAndLastName(String firstName, String lastName, int page, int size) {
        return customerRepository.findAllByFirstNameAndLastName(firstName, lastName, PageRequest.of(page, size));
    }
}
