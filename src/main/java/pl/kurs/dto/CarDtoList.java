package pl.kurs.dto;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlRootElement
@XmlSeeAlso({CarDto.class})
@NoArgsConstructor
@AllArgsConstructor
public class CarDtoList {
    private List<CarDto> entities;

    @XmlAnyElement
    public List<CarDto> getEntities() {
        return entities;
    }
}
