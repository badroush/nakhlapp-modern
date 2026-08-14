package tn.nakhlapp.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record Reglement(
        int id,
        LocalDate date,
        LocalTime time,
        int clientId,
        String amount,
        String type,
        String method,
        String reference
) {
}
