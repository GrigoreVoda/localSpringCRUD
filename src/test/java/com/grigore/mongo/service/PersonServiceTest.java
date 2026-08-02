package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.model.Gender;
import com.grigore.mongo.model.Person;
import com.grigore.mongo.model.RelationOrigin;
import com.grigore.mongo.model.Relative;
import com.grigore.mongo.repository.PersonsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonsRepository personsRepository;

    private PersonService personService;

    @BeforeEach
    void setUp() {
        personService = new PersonService(personsRepository);
        // save() just returns whatever it's given, like the real Mongo repository does.
        lenient().when(personsRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(personsRepository.saveAll(anyIterable())).thenAnswer(inv -> inv.getArgument(0));
    }

    private Person person(String id, String firstName, Gender gender) {
        Person p = new Person();
        p.setId(id);
        p.setFirstName(firstName);
        p.setLastName("Test");
        p.setDateOfBirth(LocalDate.of(1990, 1, 1));
        p.setGender(gender);
        return p;
    }

    @Test
    void addingRelativeCreatesInverseOnTheOtherPerson() {
        Person alice = person("alice", "Alice", Gender.FEMALE);
        Person bob = person("bob", "Bob", Gender.MALE);
        alice.setRelatives(List.of(new Relative("bob", "father")));

        when(personsRepository.findAllById(anyIterable())).thenReturn(List.of(bob));

        personService.addPerson(alice);

        ArgumentCaptor<List<Person>> captor = ArgumentCaptor.forClass(List.class);
        verify(personsRepository).saveAll(captor.capture());
        Person savedBob = captor.getValue().get(0);
        assertEquals(1, savedBob.getRelatives().size());
        assertEquals("alice", savedBob.getRelatives().get(0).getRelativePersonId());
        // Alice is female, so from Bob's side ("father" inverted) she's his daughter.
        assertEquals("daughter", savedBob.getRelatives().get(0).getRelativeType());
    }

    @Test
    void addingRelativeThatDoesNotExistThrows() {
        Person alice = person("alice", "Alice", Gender.FEMALE);
        alice.setRelatives(List.of(new Relative("ghost", "father")));

        when(personsRepository.findAllById(anyIterable())).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> personService.addPerson(alice));
    }

    @Test
    void removingRelativeOnUpdateRemovesInverseToo() {
        Person bob = person("bob", "Bob", Gender.MALE);
        bob.setRelatives(new ArrayList<>(List.of(new Relative("alice", "daughter"))));

        Person aliceBefore = person("alice", "Alice", Gender.FEMALE);
        aliceBefore.setRelatives(List.of(new Relative("bob", "father")));
        when(personsRepository.findPersonById("alice")).thenReturn(Optional.of(aliceBefore));

        Person aliceAfter = person("alice", "Alice", Gender.FEMALE);
        aliceAfter.setRelatives(List.of()); // relative removed in the edit

        when(personsRepository.findAllById(anyIterable())).thenReturn(List.of(bob));

        personService.updatePerson(aliceAfter);

        ArgumentCaptor<List<Person>> captor = ArgumentCaptor.forClass(List.class);
        verify(personsRepository).saveAll(captor.capture());
        assertTrue(captor.getValue().get(0).getRelatives().isEmpty());
    }

    @Test
    void changingRelativeTypeUpdatesInverseLabel() {
        Person bob = person("bob", "Bob", Gender.MALE);
        bob.setRelatives(new ArrayList<>(List.of(new Relative("alice", "sister"))));

        Person aliceBefore = person("alice", "Alice", Gender.FEMALE);
        aliceBefore.setRelatives(List.of(new Relative("bob", "brother")));
        when(personsRepository.findPersonById("alice")).thenReturn(Optional.of(aliceBefore));

        Person aliceAfter = person("alice", "Alice", Gender.FEMALE);
        aliceAfter.setRelatives(List.of(new Relative("bob", "husband"))); // corrected: they're spouses

        when(personsRepository.findAllById(anyIterable())).thenReturn(List.of(bob));

        personService.updatePerson(aliceAfter);

        ArgumentCaptor<List<Person>> captor = ArgumentCaptor.forClass(List.class);
        verify(personsRepository).saveAll(captor.capture());
        List<Relative> bobRelatives = captor.getValue().get(0).getRelatives();
        assertEquals(1, bobRelatives.size());
        assertEquals("wife", bobRelatives.get(0).getRelativeType());
    }

    @Test
    void deletingPersonRemovesDanglingRelativeReferencesFromOthers() {
        Person alice = person("alice", "Alice", Gender.FEMALE);
        alice.setRelatives(new ArrayList<>(List.of(new Relative("bob", "husband"))));

        when(personsRepository.findPersonById("bob")).thenReturn(Optional.of(person("bob", "Bob", Gender.MALE)));
        when(personsRepository.findAll()).thenReturn(List.of(alice));

        personService.removePerson("bob");

        ArgumentCaptor<List<Person>> captor = ArgumentCaptor.forClass(List.class);
        verify(personsRepository).saveAll(captor.capture());
        assertTrue(captor.getValue().get(0).getRelatives().isEmpty());
        verify(personsRepository).deleteById("bob");
    }

    @Test
    void adoptiveOriginCarriesOverToTheInverseRelation() {
        Person alice = person("alice", "Alice", Gender.FEMALE);
        Person bob = person("bob", "Bob", Gender.MALE);
        alice.setRelatives(List.of(new Relative("bob", "father", RelationOrigin.ADOPTIVE)));

        when(personsRepository.findAllById(anyIterable())).thenReturn(List.of(bob));

        personService.addPerson(alice);

        ArgumentCaptor<List<Person>> captor = ArgumentCaptor.forClass(List.class);
        verify(personsRepository).saveAll(captor.capture());
        Relative inverse = captor.getValue().get(0).getRelatives().get(0);
        assertEquals("daughter", inverse.getRelativeType());
        assertEquals(RelationOrigin.ADOPTIVE, inverse.getOrigin());
    }

    @Test
    void defaultOriginIsBiological() {
        Relative r = new Relative("bob", "father");
        assertEquals(RelationOrigin.BIOLOGICAL, r.getOrigin());
    }

    @Test
    void changingOriginOnlyResyncsTheInverseEntry() {
        Person bob = person("bob", "Bob", Gender.MALE);
        bob.setRelatives(new ArrayList<>(List.of(new Relative("alice", "daughter", RelationOrigin.BIOLOGICAL))));

        Person aliceBefore = person("alice", "Alice", Gender.FEMALE);
        aliceBefore.setRelatives(List.of(new Relative("bob", "father", RelationOrigin.BIOLOGICAL)));
        when(personsRepository.findPersonById("alice")).thenReturn(Optional.of(aliceBefore));

        Person aliceAfter = person("alice", "Alice", Gender.FEMALE);
        aliceAfter.setRelatives(List.of(new Relative("bob", "father", RelationOrigin.ADOPTIVE))); // corrected

        when(personsRepository.findAllById(anyIterable())).thenReturn(List.of(bob));

        personService.updatePerson(aliceAfter);

        ArgumentCaptor<List<Person>> captor = ArgumentCaptor.forClass(List.class);
        verify(personsRepository).saveAll(captor.capture());
        List<Relative> bobRelatives = captor.getValue().get(0).getRelatives();
        assertEquals(1, bobRelatives.size());
        assertEquals(RelationOrigin.ADOPTIVE, bobRelatives.get(0).getOrigin());
    }

    @Test
    void findPersonByIdThrowsWhenMissing() {
        when(personsRepository.findPersonById("missing")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> personService.findPersonById("missing"));
    }
}
