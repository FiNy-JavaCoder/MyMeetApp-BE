package MyApp.BE.enums;

public enum SearchTypeRelationShip {
    FORMAL_RELATIONSHIP("Vážný vztah"),
    FRIENDSHIP("Přátelství"),
    FLIRT("Flirt"),
    ROMANCE_RELATIONSHIP("Milenecký vztah");

    private final String searchRelationType;

    SearchTypeRelationShip(String description) {
        this.searchRelationType = description;
    }

    public String getDescription() {
        return searchRelationType;
    }
}