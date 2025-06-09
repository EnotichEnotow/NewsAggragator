package org.example.Storage;

public enum Category {
    POLITICS,
    ECONOMICS,
    SPORTS,
    SCIENCE,
    ENTERTAINMENT,
    OTHER;
    public static Category fromString(String str) {
        if (str == null) return OTHER;
        switch (str.toLowerCase()) {
            case "политика": return POLITICS;
            case "экономика": return ECONOMICS;
            case "спорт":    return SPORTS;
            case "наука":    return SCIENCE;
            case "развлечения": return ENTERTAINMENT;
            default: return OTHER;
        }
    }
}