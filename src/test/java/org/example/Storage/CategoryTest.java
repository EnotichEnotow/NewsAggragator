package org.example.Storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test void fromString_ruVariants() {
        assertEquals(Category.POLITICS,    Category.fromString("политика"));
        assertEquals(Category.ECONOMICS,   Category.fromString("экономика"));
        assertEquals(Category.SPORTS,      Category.fromString("спорт"));
        assertEquals(Category.SCIENCE,     Category.fromString("наука"));
        assertEquals(Category.ENTERTAINMENT,Category.fromString("развлечения"));
        assertEquals(Category.OTHER,       Category.fromString("что-то неизвестное"));
    }

    @Test void fromString_nullIsOther() {
        assertEquals(Category.OTHER, Category.fromString(null));
    }
}