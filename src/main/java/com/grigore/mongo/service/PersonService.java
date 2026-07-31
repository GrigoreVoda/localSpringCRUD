package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.model.Person;
import com.grigore.mongo.repository.PersonsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.Comparator;
import java.util.List;

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
        logger.info("Updating person: {}", findPersonById(person.getId()));
        personsRepository.save(person);
        logger.info("Updated person: {}", person);
        return person;

    }



    public List<Person> searchByMonth(Integer month) {
        //List<Person> newList= new ArrayList<>();
        List<Person> newList = personsRepository.findAll().stream().
         filter(person -> {
                    return  person.getDateOfBirth().getMonth().equals(Month.of(month));}).
                sorted(Comparator.comparingInt(o -> o.getDateOfBirth().getDayOfMonth())).toList();
        logger.info("Searched persons for month: " + month);
                      //(o1, o2) ->{return
                      //(o1.getDateOfBirth().getDayOfMonth()-o2.getDateOfBirth().getDayOfMonth());}
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
            personsRepository.deleteById(personId);}
    }

    public List<Person> findPersonsByIds(List<String> ids) {
        return personsRepository.findAllById(ids);
    }

    public List<Person> saveAll(List<Person> persons) {
        return personsRepository.saveAll(persons);
    }
}
