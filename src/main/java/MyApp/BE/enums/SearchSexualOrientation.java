package MyApp.BE.enums;

public enum SearchSexualOrientation {

    HETEROSEXUAL("heterosexuální"),
    HOMOSEXUAL("homosexuální"),
    BISEXUAL("bisexuální"),
    PANSEXUAL("pansexuální");

    private final String searchSexualType;

    SearchSexualOrientation(String description) {
        this.searchSexualType = description;
    }

    public String getDescription() {
        return searchSexualType;
    }
}
