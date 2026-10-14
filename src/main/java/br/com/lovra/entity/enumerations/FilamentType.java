package br.com.lovra.entity.enumerations;

public enum FilamentType {

    PLA(1, "PLA - Polylactic Acid"),
    ABS(2, "ABS - Acrylonitrile Butadiene Styrene"),
    PETG(3, "PETG - Polyethylene Terephthalate Glycol"),
    TPU(4, "TPU - Thermoplastic Polyurethane"),
    ASA(5, "ASA - Acrylonitrile Styrene Acrylate"),
    NYLON(6, "Nylon - Polyamide"),
    HIPS(7, "HIPS - High Impact Polystyrene"),
    PVA(8, "PVA - Polyvinyl Alcohol");

    private final Integer id;
    private final String description;

    FilamentType(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public Integer getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public static FilamentType fromId(Integer id) {
        for (FilamentType type : FilamentType.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }

        throw new IllegalArgumentException("FilamentType not found. Id: " + id);
    }
}