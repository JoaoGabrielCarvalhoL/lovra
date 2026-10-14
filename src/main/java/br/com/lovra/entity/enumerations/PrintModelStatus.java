package br.com.lovra.entity.enumerations;

public enum PrintModelStatus {

    PENDING_VALIDATION(1, "Pending Validation"),
    VALIDATED(2, "Validated"),
    REJECTED(3, "Rejected");

    private final Integer id;
    private final String description;

    PrintModelStatus(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public Integer getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public static PrintModelStatus fromId(Integer id) {
        for (PrintModelStatus status : PrintModelStatus.values()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }

        throw new IllegalArgumentException("PrintModelStatus not found. Id: " + id);
    }
}