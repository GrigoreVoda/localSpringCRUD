package com.grigore.mongo.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PersonTest {

    @Test
    void ageAndZodiacAreNullWhenDateOfBirthIsUnknown() {
        Person p = new Person();
        p.setFirstName("Unknown");
        p.setLastName("Birthdate");
        p.setGender(Gender.MALE);
        // dateOfBirth intentionally left unset

        assertNull(p.getAge());
        assertNull(p.getZodiac());
    }

    @Test
    void ageAndZodiacAreComputedWhenDateOfBirthIsKnown() {
        Person p = new Person();
        p.setDateOfBirth(LocalDate.now().minusYears(30));

        assertEquals(30, p.getAge());
        assertNotNull(p.getZodiac());
    }

    @Test
    void ageIsNullWhenOnlyBirthYearIsUnknown() {
        Person p = new Person();
        p.setDateOfBirth(LocalDate.of(1990, 6, 15));
        p.setDateOfBirthYearKnown(false);

        assertNull(p.getAge());
        assertNotNull(p.getZodiac());
    }

    @Test
    void zodiacIsNullWhenBirthMonthOrDayIsUnknown() {
        Person monthUnknown = new Person();
        monthUnknown.setDateOfBirth(LocalDate.of(1990, 6, 15));
        monthUnknown.setDateOfBirthMonthKnown(false);
        assertNull(monthUnknown.getZodiac());

        Person dayUnknown = new Person();
        dayUnknown.setDateOfBirth(LocalDate.of(1990, 6, 15));
        dayUnknown.setDateOfBirthDayKnown(false);
        assertNull(dayUnknown.getZodiac());
    }

    @Test
    void ageAndZodiacTreatMissingFlagAsKnown() {
        Person p = new Person();
        p.setDateOfBirth(LocalDate.now().minusYears(30));
        // Flags left unset (null), simulating a document saved before these
        // fields existed - should behave as if fully known.
        p.setDateOfBirthYearKnown(null);
        p.setDateOfBirthMonthKnown(null);
        p.setDateOfBirthDayKnown(null);

        assertEquals(30, p.getAge());
        assertNotNull(p.getZodiac());
    }
}
