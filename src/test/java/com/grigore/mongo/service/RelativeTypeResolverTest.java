package com.grigore.mongo.service;

import com.grigore.mongo.model.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RelativeTypeResolverTest {

    @ParameterizedTest
    @CsvSource({
            "father, MALE, son",
            "father, FEMALE, daughter",
            "mother, MALE, son",
            "mother, FEMALE, daughter",
            "parent, MALE, son",
            "son, MALE, father",
            "son, FEMALE, mother",
            "daughter, MALE, father",
            "daughter, FEMALE, mother",
            "brother, MALE, brother",
            "brother, FEMALE, sister",
            "sister, MALE, brother",
            "sister, FEMALE, sister",
            "husband, MALE, husband",
            "husband, FEMALE, wife",
            "wife, MALE, husband",
            "wife, FEMALE, wife",
    })
    void resolvesGenderAwareInverse(String type, Gender ownerGender, String expected) {
        assertEquals(expected, RelativeTypeResolver.inverseOf(type, ownerGender));
    }

    @Test
    void fallsBackToGenderNeutralLabelWhenGenderIsNull() {
        assertEquals("parent", RelativeTypeResolver.inverseOf("son", null));
        assertEquals("child", RelativeTypeResolver.inverseOf("father", null));
        assertEquals("sibling", RelativeTypeResolver.inverseOf("brother", null));
        assertEquals("spouse", RelativeTypeResolver.inverseOf("wife", null));
    }

    @Test
    void unknownFreeTextLabelsAreSymmetric() {
        assertEquals("godparent", RelativeTypeResolver.inverseOf("godparent", Gender.MALE));
        assertEquals("friend", RelativeTypeResolver.inverseOf("friend", Gender.FEMALE));
    }

    @Test
    void isCaseInsensitive() {
        assertEquals("daughter", RelativeTypeResolver.inverseOf("FATHER", Gender.FEMALE));
    }
}
