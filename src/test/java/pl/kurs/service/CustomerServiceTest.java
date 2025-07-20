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
import pl.kurs.entity.Customer;
import pl.kurs.exception.DataNotFoundException;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepositoryMock;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldReturnCustomerById() {
        //given
        Customer testCustomer = createTestCustomer();
        when(customerRepositoryMock.findById(1L)).thenReturn(Optional.of(testCustomer));

        //when
        Customer result = customerService.getCustomerById(1L);

        //then
        assertThat(result).isEqualTo(testCustomer);
    }

    @Test
    void shouldThrowWhenCustomerNotFoundById() {
        //given
        when(customerRepositoryMock.findById(42L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> customerService.getCustomerById(42L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with id: 42");
    }

    @Test
    void shouldReturnPagedCustomersForGetAll() {
        //given
        Customer testCustomer = createTestCustomer();
        Page<Customer> page = new PageImpl<>(List.of(testCustomer));
        when(customerRepositoryMock.findAll(PageRequest.of(0, 3))).thenReturn(page);

        //when
        Page<Customer> result = customerService.getAll(0, 3);

        //then
        assertThat(result.getContent()).containsExactly(testCustomer);
    }

    @Test
    void shouldSaveCustomer() {
        //given
        Customer testCustomer = createTestCustomer();
        when(customerRepositoryMock.save(testCustomer)).thenReturn(testCustomer);

        //when
        Customer saved = customerService.saveCustomer(testCustomer);

        //then
        assertThat(saved).isEqualTo(testCustomer);
    }

    @Test
    void shouldUpdateCustomer() {
        //given
        Customer testCustomer = createTestCustomer();
        Customer incomingCustomer = new Customer("Jane", "Smith", "jane.smith@gmail.com", "511111111", "XYZ 202020");
        incomingCustomer.setId(1L);

        when(customerRepositoryMock.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepositoryMock.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Customer updated = customerService.updateCustomer(incomingCustomer);

        // then
        assertThat(updated.getFirstName()).isEqualTo("Jane");
        assertThat(updated.getLastName()).isEqualTo("Smith");
        assertThat(updated.getEmail()).isEqualTo("jane.smith@gmail.com");
        assertThat(updated.getPhoneNumber()).isEqualTo("511111111");
        assertThat(updated.getDrivingLicenseNumber()).isEqualTo("XYZ 202020");
    }

    @Test
    void shouldThrowWhenUpdateCustomerNotFound() {
        //given
        Customer testCustomer = createTestCustomer();
        when(customerRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> customerService.updateCustomer(testCustomer))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Customer with id: 1 not found");
    }

    @Test
    void shouldDeleteCustomerById() {
        //when
        customerService.deleteCustomerById(1L);

        //then
        verify(customerRepositoryMock).deleteById(1L);
    }

    @Test
    void shouldReturnCustomersByFirstAndLastName() {
        //given
        Customer testCustomer = createTestCustomer();
        Page<Customer> page = new PageImpl<>(List.of(testCustomer));
        when(customerRepositoryMock.findAllByFirstNameAndLastName(
                eq("John"), eq("Doe"), any(Pageable.class)))
                .thenReturn(page);

        //when
        Page<Customer> result = customerService.getByFirstNameAndLastName("John", "Doe", 0, 2);

        //then
        assertThat(result.getContent()).containsExactly(testCustomer);
        verify(customerRepositoryMock).findAllByFirstNameAndLastName(
                eq("John"), eq("Doe"), eq(PageRequest.of(0, 2)));
    }

    private Customer createTestCustomer() {
        Customer customer = new Customer("John", "Doe", "john.doe@gmail.com", "500900800", "KK 151214");
        customer.setId(1L);
        return customer;
    }

}
