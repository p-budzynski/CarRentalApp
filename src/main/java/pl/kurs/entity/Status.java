package pl.kurs.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.kurs.exception.StatusNotFoundException;

@AllArgsConstructor
@Getter
public enum Status {
    RESERVED("RESERVED"),
    RENTED("RENTED"),
    FINISHED("FINISHED"),
    CANCELED("CANCELED");

    private final String value;

    public static Status fromString(String statusName) {
        if (statusName == null || statusName.trim().isEmpty()) {
            throw new StatusNotFoundException("Reservation status cannot be empty.");
        }

        return switch (statusName.trim().toUpperCase()) {
            case "RESERVED" -> RESERVED;
            case "RENTED" -> RENTED;
            case "FINISHED" -> FINISHED;
            case "CANCELED" -> CANCELED;
            default -> throw new StatusNotFoundException("Unknown reservation status: " + statusName);
        };
    }

    @Override
    public String toString() {
        return value;
    }

}
