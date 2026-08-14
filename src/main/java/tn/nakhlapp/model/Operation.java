package tn.nakhlapp.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record Operation(
        int id,
        LocalDate date,
        LocalTime time,
        int clientId,
        int productId,
        int cageId,
        String grossWeight,
        double cageCount,
        String unitPrice,
        double coefficient
) {
}
