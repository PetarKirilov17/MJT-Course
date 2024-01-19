package queryParams;

public enum MealType {
    BREAKFAST("Breakfast"),
    DINNER("Dinner"),
    LUNCH("Lunch"),
    SNACK("Snack"),
    TEATIME("Teatime");
    private final String value;
    MealType(String value) {
        this.value = value;
    }
    public String toString() {
        return value;
    }
}
