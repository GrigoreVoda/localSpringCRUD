package com.grigore.mongo.repository;

import com.grigore.mongo.model.Person;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PersonsRepository extends MongoRepository<Person, String> {

    Optional<Person> findPersonById(String id);
    List<Person> findBy(TextCriteria criteria);



    //List<Person> findByDateOfBirthMonth(Integer month);


}
