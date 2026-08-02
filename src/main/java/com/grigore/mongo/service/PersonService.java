package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.model.Person;
import com.grigore.mongo.model.Relative;
import com.grigore.mongo.repository.PersonsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);
    private final PersonsRepository personsRepository;

    public PersonService(PersonsRepository personsRepository) {

        this.personsRepository = personsRepository;
    }

    public List<Person> findAllPersons() {
        logger.info("Accessed all persons data");
        return personsRepository.findAll();
    }

    public Person addPerson(Person person){
        personsRepository.save(person);
        syncRelatives(person, null);
        logger.info("Added person " + person.getFirstName() +" "+ person.getLastName());
        return person;

        }

    public Person findPersonById(String id) {
        //logger.info("Searching for person by ID " + id);
        Person person = personsRepository.findPersonById(id).orElseThrow(
                () -> new UserNotFoundException("User by id " + id + " not found")
        );

        logger.info("Person searched: " + id + " " + person.getFirstName() +" "+person.getLastName());

        return person;
    }
    private String getPersonFirstAndLastName(String id){
        Person person = personsRepository.findPersonById(id).orElseThrow(()->
        new UserNotFoundException("User by id " + id + " not found"));

        String FirstAndLastName = person.getFirstName()+ " " + person.getLastName();
        return FirstAndLastName;
    }
    public Person updatePerson(Person person){
        Person existing = findPersonById(person.getId());
        logger.info("Updating person: {}", existing);
        personsRepository.save(person);
        syncRelatives(person, existing.getRelatives());
        logger.info("Updated person: {}", person);
        return person;

    }



    public List<Person> searchByMonth(Integer month) {
        List<Person> newList = personsRepository.findAll().stream()
                .filter(person -> person.getDateOfBirth() != null && person.getDateOfBirth().getMonth().equals(Month.of(month)))
                .sorted(Comparator.comparingInt(o -> o.getDateOfBirth().getDayOfMonth()))
                .toList();
        logger.info("Searched persons for month: " + month);
        return newList;
    }

    public List<Person> findText(String text) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(text);
       // TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny("firstName", "lastName").matching(text);

         //mongoTemplate.find(Query.query(criteria), Person.class, "persons");
        return personsRepository.findBy(criteria);
    }

    public boolean findPersonByFirstNameAndLastName(String fisrtName, String lastName) {
        List<Person> allPersons = personsRepository.findAll();
        boolean matchPerson = allPersons.stream().anyMatch(
                (person)->(person.getLastName().equalsIgnoreCase(lastName)
                        &&person.getFirstName().equalsIgnoreCase(fisrtName)));
        return matchPerson;
    }

    public void removePerson(String personId) {
        if(personsRepository.findPersonById(personId).isPresent())
        {logger.info("Deleted person with ID " +personId+ " " + getPersonFirstAndLastName(personId));
            removePersonFromRelativesOfOthers(personId);
            personsRepository.deleteById(personId);}
    }

    public List<Person> findPersonsByIds(List<String> ids) {
        return personsRepository.findAllById(ids);
    }

    public List<Person> saveAll(List<Person> persons) {
        return personsRepository.saveAll(persons);
    }

    /**
     * Strips any relative entry pointing at {@code personId} from every other
     * person, so deleting someone doesn't leave dangling relative references
     * behind on the people who were linked to them.
     */
    public void removePersonFromRelativesOfOthers(String personId) {
        List<Person> affected = personsRepository.findAll().stream()
                .filter(p -> hasRelativeTo(p, personId))
                .toList();
        if (affected.isEmpty()) {
            return;
        }
        for (Person p : affected) {
            p.getRelatives().removeIf(r -> personId.equals(r.getRelativePersonId()));
        }
        personsRepository.saveAll(affected);
    }

    private boolean hasRelativeTo(Person person, String personId) {
        return person.getRelatives() != null
                && person.getRelatives().stream().anyMatch(r -> personId.equals(r.getRelativePersonId()));
    }

    /**
     * Keeps relative relations bidirectional: whenever a relative entry is added
     * to {@code person}, the inverse relation (e.g. "father" -> "son"/"daughter")
     * is added to the referenced person too, and removed when the entry is
     * removed. Diffs against {@code previousRelatives} (the relatives the person
     * had before this save) to know what changed.
     */
    private void syncRelatives(Person person, List<Relative> previousRelatives) {
        List<Relative> current = person.getRelatives() == null ? List.of() : person.getRelatives();
        List<Relative> previous = previousRelatives == null ? List.of() : previousRelatives;

        Set<String> previousKeys = previous.stream().map(this::relativeKey).collect(Collectors.toSet());
        Set<String> currentKeys = current.stream().map(this::relativeKey).collect(Collectors.toSet());

        List<Relative> added = current.stream().filter(r -> !previousKeys.contains(relativeKey(r))).toList();
        List<Relative> removed = previous.stream().filter(r -> !currentKeys.contains(relativeKey(r))).toList();

        if (added.isEmpty() && removed.isEmpty()) {
            return;
        }

        Set<String> relatedIds = new LinkedHashSet<>();
        added.forEach(r -> relatedIds.add(r.getRelativePersonId()));
        removed.forEach(r -> relatedIds.add(r.getRelativePersonId()));

        List<Person> relatedPersons = personsRepository.findAllById(relatedIds);
        if (relatedPersons.size() != relatedIds.size()) {
            throw new IllegalArgumentException("One or more relatives reference a person that does not exist.");
        }
        Map<String, Person> byId = relatedPersons.stream().collect(Collectors.toMap(Person::getId, Function.identity()));

        // Process removals before additions: when a relation's type changes (same
        // related person shows up in both lists), the stale inverse entry must be
        // cleared before the fresh one is written, or the removal step would wipe
        // out the entry the addition step just added.
        for (Relative r : removed) {
            Person related = byId.get(r.getRelativePersonId());
            if (related.getRelatives() != null) {
                related.getRelatives().removeIf(x -> person.getId().equals(x.getRelativePersonId()));
            }
        }
        for (Relative r : added) {
            Person related = byId.get(r.getRelativePersonId());
            String inverseType = RelativeTypeResolver.inverseOf(r.getRelativeType(), person.getGender());
            List<Relative> relatives = related.getRelatives() == null ? new ArrayList<>() : new ArrayList<>(related.getRelatives());
            relatives.removeIf(x -> person.getId().equals(x.getRelativePersonId()));
            // The origin (biological/adoptive/step) applies symmetrically to both
            // sides of the relation, so it's carried over unchanged to the inverse.
            relatives.add(new Relative(person.getId(), inverseType, r.getOrigin()));
            related.setRelatives(relatives);
        }

        personsRepository.saveAll(relatedPersons);
    }

    private String relativeKey(Relative r) {
        return r.getRelativePersonId() + "::" + r.getRelativeType() + "::" + r.getOrigin();
    }
}
