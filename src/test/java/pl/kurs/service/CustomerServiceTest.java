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
    private CustomerService customerServiceMock;

    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer("John", "Doe", "john.doe@gmail.com", "500900800", "KK 151214");
        sampleCustomer.setId(1L);
    }

    @Test
    void shouldReturnCustomerById() {
        //given
        when(customerRepositoryMock.findById(1L)).thenReturn(Optional.of(sampleCustomer));

        //when
        Customer result = customerServiceMock.getCustomerById(1L);

        //then
        assertThat(result).isEqualTo(sampleCustomer);
    }

    @Test
    void shouldThrowWhenCustomerNotFoundById() {
        //given
        when(customerRepositoryMock.findById(42L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> customerServiceMock.getCustomerById(42L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with id: 42");
    }

    @Test
    void shouldReturnPagedCustomersForGetAll() {
        //given
        Page<Customer> page = new PageImpl<>(List.of(sampleCustomer));
        when(customerRepositoryMock.findAll(PageRequest.of(0, 3))).thenReturn(page);

        //when
        Page<Customer> result = customerServiceMock.getAll(0, 3);

        //then
        assertThat(result.getContent()).containsExactly(sampleCustomer);
    }

    @Test
    void shouldSaveCustomer() {
        //given
        when(customerRepositoryMock.save(sampleCustomer)).thenReturn(sampleCustomer);

        //when
        Customer saved = customerServiceMock.saveCustomer(sampleCustomer);

        //then
        assertThat(saved).isEqualTo(sampleCustomer);
        verify(customerRepositoryMock).save(sampleCustomer);
    }

    @Test
    void shouldUpdateCustomer() {
        //given
        Customer incoming = new Customer("Jane", "Smith", "jane.smith@gmail.com", "511111111", "XYZ 202020");
        incoming.setId(1L);

        when(customerRepositoryMock.findById(1L)).thenReturn(Optional.of(sampleCustomer));
        when(customerRepositoryMock.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Customer updated = customerServiceMock.updateCustomer(incoming);

        // then
        assertThat(updated.getFirstName()).isEqualTo("Jane");
        assertThat(updated.getLastName()).isEqualTo("Smith");
        assertThat(updated.getEmail()).isEqualTo("jane.smith@gmail.com");
        assertThat(updated.getPhoneNumber()).isEqualTo("511111111");
        assertThat(updated.getDrivingLicenseNumber()).isEqualTo("XYZ 202020");
        verify(customerRepositoryMock).save(sampleCustomer);
    }

    @Test
    void shouldThrowWhenUpdateCustomerNotFound() {
        //given
        when(customerRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> customerServiceMock.updateCustomer(sampleCustomer))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Customer with id: 1 not found");
    }

    @Test
    void shouldDeleteCustomerById() {
        //when
        customerServiceMock.deleteCustomerById(1L);

        //then
        verify(customerRepositoryMock).deleteById(1L);
    }

    @Test
    void shouldReturnCustomersByFirstAndLastName() {
        //given
        Page<Customer> page = new PageImpl<>(List.of(sampleCustomer));
        when(customerRepositoryMock.findAllByFirstNameAndLastName(
                eq("John"), eq("Doe"), any(Pageable.class)))
                .thenReturn(page);

        //when
        Page<Customer> result = customerServiceMock.getByFirstNameAndLastName("John", "Doe", 0, 2);

        //then
        assertThat(result.getContent()).containsExactly(sampleCustomer);
        verify(customerRepositoryMock).findAllByFirstNameAndLastName(
                eq("John"), eq("Doe"), eq(PageRequest.of(0, 2)));
    }

}
