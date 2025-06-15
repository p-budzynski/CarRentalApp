package pl.kurs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.kurs.dto.CustomerDto;
import pl.kurs.entity.Customer;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDto entityToDto(Customer customer);

    List<CustomerDto> entitiesToDtos(List<Customer> customers);

    @Mapping(target = "id", ignore = true)
    Customer dtoToEntity(CustomerDto customerDto);

    Customer dtoToEntityWithId(CustomerDto customerDto);
}
