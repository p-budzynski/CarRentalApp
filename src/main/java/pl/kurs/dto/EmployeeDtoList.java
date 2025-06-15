package pl.kurs.dto;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlRootElement
@XmlSeeAlso({EmployeeDto.class})
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDtoList {
    private List<EmployeeDto> entities;

    @XmlAnyElement
    public List<EmployeeDto> getEntities() {
        return entities;
    }
}
