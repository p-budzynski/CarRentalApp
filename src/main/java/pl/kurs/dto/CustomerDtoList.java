package pl.kurs.dto;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlRootElement
@XmlSeeAlso({CustomerDto.class})
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDtoList {
    private List<CustomerDto> entities;

    @XmlAnyElement
    public List<CustomerDto> getEntities() {
        return entities;
    }
}
