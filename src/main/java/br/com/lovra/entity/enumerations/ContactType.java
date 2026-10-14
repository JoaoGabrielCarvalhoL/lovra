package br.com.lovra.entity.enumerations;

public enum ContactType {
	WHATSAPP(1, "WhatsApp"),
    PHONE(2, "Phone"),
    EMAIL(3, "Email");

    private final Integer id;
    private final String description;

    ContactType(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public Integer getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public static ContactType fromId(Integer id) {
        for (ContactType type : ContactType.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }

        throw new IllegalArgumentException("ContactType not found. Id: " + id);
    }
}
