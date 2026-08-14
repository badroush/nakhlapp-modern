package tn.nakhlapp.model;

public record Societe(
        int id,
        String nameAr,
        String addressAr,
        String phone,
        String nameFr,
        String addressFr,
        String taxId,
        String gsm,
        String email
) {
}
