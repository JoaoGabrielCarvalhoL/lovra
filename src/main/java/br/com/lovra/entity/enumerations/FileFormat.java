package br.com.lovra.entity.enumerations;

public enum FileFormat {

	 STL(1, "STL"),
	 THREE_MF(2, "3MF");

    private final Integer id;
    private final String description;

    FileFormat(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public Integer getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public static FileFormat fromId(Integer id) {
        for (FileFormat format : FileFormat.values()) {
            if (format.getId().equals(id)) {
                return format;
            }
        }

        throw new IllegalArgumentException("FileFormat not found. Id: " + id);
    }
}
