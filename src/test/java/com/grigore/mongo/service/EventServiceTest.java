package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.model.Eveniment;
import com.grigore.mongo.model.Gender;
import com.grigore.mongo.model.Person;
import com.grigore.mongo.repository.EventsRepository;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventsRepository eventsRepository;
    @Mock
    private PersonsRepository personsRepository;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        PersonService personService = new PersonService(personsRepository);
        eventService = new EventService(eventsRepository, personService);
        lenient().when(personsRepository.saveAll(anyIterable())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(eventsRepository.saveAll(anyIterable())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(eventsRepository.save(any(Eveniment.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Person person(String id) {
        Person p = new Person();
        p.setId(id);
        p.setFirstName("First");
        p.setLastName("Last");
        p.setDateOfBirth(LocalDate.of(1990, 1, 1));
        p.setGender(Gender.MALE);
        return p;
    }

    private Eveniment event(String id, List<String> personsId) {
        Eveniment e = new Eveniment();
        e.setId(id);
        e.setEventName("Party");
        e.setEventDate(LocalDate.of(2026, 5, 1));
        e.setPersonsId(personsId);
        return e;
    }

    @Test
    void addingEventAppendsEventIdToEachAttendeeAndSavesOnce() {
        Person alice = person("alice");
        Person bob = person("bob");
        Eveniment party = event("party1", List.of("alice", "bob"));

        when(personsRepository.findAllById(anyIterable())).thenReturn(List.of(alice, bob));

        eventService.addEventAtPerson(party.getPersonsId(), party);

        ArgumentCaptor<List<Person>> captor = ArgumentCaptor.forClass(List.class);
        verify(personsRepository).saveAll(captor.capture());
        assertTrue(captor.getValue().stream().allMatch(p -> p.getEventsID().contains("party1")));
    }

    @Test
    void addingEventWithUnknownAttendeeThrows() {
        Eveniment party = event("party1", List.of("ghost"));
        when(personsRepository.findAllById(anyIterable())).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> eventService.addEventAtPerson(party.getPersonsId(), party));
    }

    @Test
    void removingEventDoesNotThrowWhenAnAttendeeWasAlreadyDeleted() {
        // Regression test: an event can reference a person who no longer exists
        // (e.g. the person was deleted without going through event cleanup).
        // Deleting the event must still succeed instead of blowing up.
        Person alice = person("alice");
        alice.setEventsID(new ArrayList<>(List.of("party1")));
        Eveniment party = event("party1", List.of("alice", "deleted-person"));

        when(eventsRepository.findEvenimentById("party1")).thenReturn(Optional.of(party));
        // "deleted-person" no longer exists, so findAllById only returns alice.
        when(personsRepository.findAllById(anyIterable())).thenReturn(List.of(alice));

        eventService.removeEvent("party1");

        verify(eventsRepository).deleteById("party1");
        ArgumentCaptor<List<Person>> captor = ArgumentCaptor.forClass(List.class);
        verify(personsRepository).saveAll(captor.capture());
        assertFalse(captor.getValue().get(0).getEventsID().contains("party1"));
    }

    @Test
    void updatingEventToAddAndRemoveAttendeesSyncsBothSides() {
        Person alice = person("alice");
        alice.setEventsID(new ArrayList<>(List.of("party1")));
        Person bob = person("bob");
        bob.setEventsID(new ArrayList<>());

        Eveniment original = event("party1", new ArrayList<>(List.of("alice")));
        Eveniment updated = event("party1", List.of("bob")); // alice removed, bob added

        when(eventsRepository.findEvenimentById("party1")).thenReturn(Optional.of(original));
        when(personsRepository.findAllById(List.of("bob"))).thenReturn(List.of(bob));
        when(personsRepository.findAllById(List.of("alice"))).thenReturn(List.of(alice));

        eventService.updateEveniment(updated);

        verify(eventsRepository).save(updated);
        verify(personsRepository, org.mockito.Mockito.times(2)).saveAll(anyIterable());
    }

    @Test
    void removePersonFromAllEventsOnlyTouchesEventsContainingThem() {
        Eveniment withAlice = event("party1", new ArrayList<>(List.of("alice", "bob")));
        Eveniment withoutAlice = event("party2", new ArrayList<>(List.of("bob")));
        when(eventsRepository.findAll()).thenReturn(List.of(withAlice, withoutAlice));

        eventService.removePersonFromAllEvents("alice");

        ArgumentCaptor<List<Eveniment>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventsRepository).saveAll(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals("party1", captor.getValue().get(0).getId());
        assertFalse(captor.getValue().get(0).getPersonsId().contains("alice"));
    }

    @Test
    void removePersonFromAllEventsIsNoOpWhenNobodyReferencesThem() {
        when(eventsRepository.findAll()).thenReturn(List.of(event("party1", List.of("bob"))));

        eventService.removePersonFromAllEvents("alice");

        verify(eventsRepository, never()).saveAll(anyIterable());
    }

    @Test
    void findEvenimentByIdThrowsWhenMissing() {
        when(eventsRepository.findEvenimentById("missing")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> eventService.findEvenimentById("missing"));
    }
}
