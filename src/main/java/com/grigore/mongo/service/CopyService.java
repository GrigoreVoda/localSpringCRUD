//package com.grigore.mongo.service;
//
//import com.grigore.mongo.model.Gender;
//import com.grigore.mongo.model.Person;
//import com.grigore.mongo.model.SqlPerson;
//import com.grigore.mongo.repository.PersonsRepository;
//import com.grigore.mongo.repository.SqlRepo;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Deprecated
//@Service
//public class CopyService {
//    private final PersonsRepository personsRepository;
//    private final SqlRepo sqlRepo;
//
//    public CopyService(PersonsRepository personsRepository, SqlRepo sqlRepo) {
//        this.personsRepository = personsRepository;
//        this.sqlRepo = sqlRepo;
//    }
//
//    public void copyData(){
//        List<SqlPerson> persons = sqlRepo.findAll();
//        for (SqlPerson sqlPerson : persons){
//            Person mongoPerosn = new Person();
//            mongoPerosn.setFirstName(sqlPerson.getFirstName());
//            mongoPerosn.setLastName(sqlPerson.getLastName());
//            mongoPerosn.setDateOfBirth(sqlPerson.getDateOfBirth());
//            if (sqlPerson.getGender().equalsIgnoreCase("Male")){
//                mongoPerosn.setGender(Gender.MALE);
//            }
//            else {mongoPerosn.setGender(Gender.FEMALE);}
//            personsRepository.save(mongoPerosn);
//
//        }
//    }
//}
