package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.model.Person;
import com.grigore.mongo.repository.PersonsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.Comparator;
import java.util.List;

@Service
public class PersonService {

private final PersonsRepository personsRepository;
   //@Autowired
   // private MongoTemplate mongoTemplate;
    @Autowired
    public PersonService(PersonsRepository personsRepository) {

        this.personsRepository = personsRepository;
    }

    public List<Person> findAllPersons() {
       return personsRepository.findAll();
    }

    public Person addPerson(Person person){
        if((person.getFirstName()!= null&&!person.getFirstName().isEmpty())&&
                (person.getLastName()!=null&&!person.getLastName().isEmpty())&&
                (person.getDateOfBirth()!= null)&&
                (person.getGender()!= null)
        )
        {
            personsRepository.save(person);
            System.out.println("One person was saved " + person.getFirstName());
            return person;
        }
        else{
            throw new IllegalArgumentException("Person fields cannot be null.");
        }
        }

    public Person findPersonById(String id) {
        return personsRepository.findPersonById(id).orElseThrow(
                ()-> new UserNotFoundException("User by id " + id + " not found")
        );}
    public Person updatePerson(Person person){
        if((person.getFirstName()!= null&&!person.getFirstName().isEmpty())&&
                (person.getLastName()!=null&&!person.getLastName().isEmpty())&&
                (person.getDateOfBirth()!= null)&&
                (person.getGender()!= null)
        ){
            return personsRepository.save(person);
        }
        else{
            throw new IllegalArgumentException("Person fields cannot be null.");
        }

    }

    public void deletePerson(String id){
        personsRepository.deleteById(id);
    }

    public List<Person> searchByMonth(Integer month) {
        //List<Person> newList= new ArrayList<>();
        List<Person> newList = personsRepository.findAll().stream().
         filter(person -> {
                    return  person.getDateOfBirth().getMonth().equals(Month.of(month));}).
                sorted(Comparator.comparingInt(o -> o.getDateOfBirth().getDayOfMonth())).toList();
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
}
