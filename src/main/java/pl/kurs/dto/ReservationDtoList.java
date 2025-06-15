package pl.kurs.dto;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlRootElement
@XmlSeeAlso({ReservationDto.class})
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDtoList {
    private List<ReservationDto> entities;

    @XmlAnyElement
    public List<ReservationDto> getEntities() {
        return entities;
    }
}
