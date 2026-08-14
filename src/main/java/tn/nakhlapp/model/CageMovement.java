package tn.nakhlapp.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record CageMovement(
        int id,
        LocalDate date,
        LocalTime time,
        Integer clientId,
        int cageId,
        String quantity,
        MovementType type
) {
    public enum MovementType {
        RETURN("retourcage"),
        OUT("sortiecage"),
        STOCK("stock_cage");

        private final String table;

        MovementType(String table) {
            this.table = table;
        }

        public String table() {
            return table;
        }
    }
}
