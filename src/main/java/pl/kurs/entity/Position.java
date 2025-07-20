package pl.kurs.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "positions")
@NoArgsConstructor
@Getter
@Setter
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Position(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Position(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
