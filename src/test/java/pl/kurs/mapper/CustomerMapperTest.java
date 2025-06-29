package pl.kurs.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kurs.dto.CustomerDto;
import pl.kurs.entity.Customer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerMapperTest {
    private CustomerMapper customerMapper;
    private Customer sampleCustomer;
    private CustomerDto sampleCustomerDto;

    @BeforeEach
    void setUp() {
        customerMapper = Mappers.getMapper(CustomerMapper.class);

        sampleCustomer = new Customer("Anna", "Mała", "a.mala@gmail.com", "521522523", "WXX 47215");
        sampleCustomerDto = new CustomerDto("Anna", "Mała", "a.mala@gmail.com", "521522523", "WXX 47215");
    }

    @Test
    void shouldMapEntityToDto() {
        //when
        CustomerDto dto = customerMapper.entityToDto(sampleCustomer);

        //then
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(sampleCustomerDto);
    }

    @Test
    void shouldMapEntityListToDtoList() {
        //given
        List<Customer> customers = List.of(sampleCustomer);

        //when
        List<CustomerDto> dtos = customerMapper.entitiesToDtos(customers);

        //then
        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(sampleCustomerDto);
    }

    @Test
    void shouldMapDtoToEntity() {
        //when
        Customer entity = customerMapper.dtoToEntity(sampleCustomerDto);

        //then
        assertThat(entity)
                .usingRecursiveComparison()
                .isEqualTo(sampleCustomer);
    }

    @Test
    void shouldMapDtoToEntityWithId() {
        //given
        sampleCustomerDto.setId(1L);
        sampleCustomer.setId(1L);

        //when
        Customer entity = customerMapper.dtoToEntityWithId(sampleCustomerDto);

        //then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity)
                .usingRecursiveComparison()
                .isEqualTo(sampleCustomer);
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

}
