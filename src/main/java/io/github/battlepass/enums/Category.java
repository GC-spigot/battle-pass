package io.github.battlepass.enums;

public enum Category {

    WEEKLY("week"),
    DAILY("daily");

    private final String categoryId;

    Category(String categoryId) {
        this.categoryId = categoryId;
    }

    public String id() {
        return this.categoryId;
    }

    public String id(int week) {
        return this.categoryId.concat("-").concat(String.valueOf(week));
    }

    public static int stripWeek(String categoryId) {
        return categoryId.contains("week") ? Integer.parseInt(categoryId.replaceAll("[^0-9]+", "")) : 0;
    }
}
