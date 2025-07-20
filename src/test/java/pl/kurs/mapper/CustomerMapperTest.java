package pl.kurs.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kurs.dto.CustomerDto;
import pl.kurs.entity.Customer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerMapperTest {
    private final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

    @Test
    void shouldMapEntityToDto() {
        //given
        Customer testCustomer = createTestCustomer();
        CustomerDto testCustomerDto = createTestCustomerDto();

        //when
        CustomerDto dto = customerMapper.entityToDto(testCustomer);

        //then
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(testCustomerDto);
    }

    @Test
    void shouldMapEntityListToDtoList() {
        //given
        Customer testCustomer = createTestCustomer();
        CustomerDto testCustomerDto = createTestCustomerDto();
        List<Customer> customers = List.of(testCustomer);

        //when
        List<CustomerDto> dtos = customerMapper.entitiesToDtos(customers);

        //then
        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(testCustomerDto);
    }

    @Test
    void shouldMapDtoToEntity() {
        //when
        Customer testCustomer = createTestCustomer();
        CustomerDto testCustomerDto = createTestCustomerDto();
        Customer entity = customerMapper.dtoToEntity(testCustomerDto);

        //then
        assertThat(entity)
                .usingRecursiveComparison()
                .isEqualTo(testCustomer);
    }

    @Test
    void shouldMapDtoToEntityWithId() {
        //given
        Customer testCustomer = createTestCustomer();
        testCustomer.setId(1L);
        CustomerDto testCustomerDto = createTestCustomerDto();
        testCustomerDto.setId(1L);

        //when
        Customer entity = customerMapper.dtoToEntityWithId(testCustomerDto);

        //then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity)
                .usingRecursiveComparison()
                .isEqualTo(testCustomer);
    }

    @Test
    void shouldReturnNullWhenDtoToEntityWithIdGivenNull() {
        //when then
        assertThat(customerMapper.dtoToEntityWithId(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityToDtoGivenNull() {
        //when then
        assertThat(customerMapper.entityToDto(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenDtoToEntityGivenNull() {
        //when then
        assertThat(customerMapper.dtoToEntity(null)).isNull();
    }

    @Test
    void shouldReturnEmptyListWhenEntitiesToDtosGivenNull() {
        //when then
        assertThat(customerMapper.entitiesToDtos(null)).isNull();
    }

    private Customer createTestCustomer() {
        return new Customer("Anna", "Mała", "a.mala@gmail.com", "521522523", "WXX 47215");
    }

    private CustomerDto createTestCustomerDto() {
        return new CustomerDto("Anna", "Mała", "a.mala@gmail.com", "521522523", "WXX 47215");
    }

}
