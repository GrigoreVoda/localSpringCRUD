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
}
