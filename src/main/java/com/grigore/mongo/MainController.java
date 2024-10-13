package com.grigore.mongo;


import com.grigore.mongo.model.Company;
import com.grigore.mongo.model.Doc;
import com.grigore.mongo.model.Eveniment;
import com.grigore.mongo.model.Person;
import com.grigore.mongo.service.CompanyService;

import com.grigore.mongo.service.DocService;
import com.grigore.mongo.service.EventService;
import com.grigore.mongo.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping()
public class MainController {

    private final PersonService personService;
    private final DocService docsService;
    //private final CopyService copyService;/
    private final CompanyService companyService;
    private final EventService eventService;

    public MainController(PersonService personService, DocService docsService, CompanyService companyService, EventService eventService) {

        this.personService = personService;
        this.docsService = docsService;
        this.companyService = companyService;
        this.eventService = eventService;
    }

    @GetMapping("/person")
    public ResponseEntity<List<Person>> getAllPersons(){
        List<Person> allPersons = personService.findAllPersons();
        return new ResponseEntity<>(allPersons, HttpStatus.OK);
    }
    @CrossOrigin
    @GetMapping("/docs")
    public ResponseEntity<List<Doc>> getAllDocs(){
        List<Doc> allDocs = docsService.findAllDocs();
        return new ResponseEntity<>(allDocs, HttpStatus.OK);
    }
    @CrossOrigin
    @GetMapping("/company")
    public ResponseEntity<List<Company>> getAllCompanies(){
        List<Company> allCompanies = companyService.findAllCompanies();
        return new ResponseEntity<>(allCompanies, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/person/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable("id") String id){
       Person newPerson = personService.findPersonById(id);
       return new ResponseEntity<>(newPerson,HttpStatus.OK);
    }
    @CrossOrigin
    @GetMapping("/person/name")
    public ResponseEntity<Boolean> getPersonByName(
            @RequestParam("firstName") String firstName, @RequestParam ("lastName") java.lang.String lastName){
         boolean isMacth = personService.findPersonByFirstNameAndLastName(firstName,lastName);
         return new ResponseEntity<Boolean>(isMacth,HttpStatus.OK);
    }
    @CrossOrigin
    @GetMapping("person/search/{text}")
    public ResponseEntity<List<Person>> searchAnyText(@PathVariable("text")String text){
        List<Person> personList = personService.findText(text);
        return new ResponseEntity<>(personList,HttpStatus.OK);
    }
    @CrossOrigin
    @GetMapping("/docs/{id}")
    public ResponseEntity<Doc> getDocById(@PathVariable("id") String id){
        Doc newDoc = docsService.findDocById(id);
        return new ResponseEntity<>(newDoc,HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/person/byMonth/{month}")
    public ResponseEntity<List<Person>> getByMonth(@PathVariable("month") Integer month){
        List<Person> personsSearchByMonth =personService.searchByMonth(month);
        return new ResponseEntity<>(personsSearchByMonth, HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("/person")
    public ResponseEntity<Person> addPerson(@RequestBody Person person){
        Person newPerson =  personService.addPerson(person);
        System.out.println(person);
        return new ResponseEntity<>(newPerson, HttpStatus.CREATED);
    }

    @CrossOrigin
    @PostMapping("/docs")
    public ResponseEntity<Doc> addDoc(@RequestBody Doc doc){
        Doc newDoc = docsService.addDoc(doc);
        return new ResponseEntity<>(newDoc, HttpStatus.CREATED);
    }
    @CrossOrigin
    @PostMapping("company")
    public ResponseEntity<Company> addCompany(@RequestBody Company company){
        Company newCompany = companyService.addCompany(company);
        return new ResponseEntity<>(newCompany, HttpStatus.CREATED);
    }

    @CrossOrigin
    @PutMapping("/person")
    public  ResponseEntity<Person> updatePerson(@RequestBody Person person){
        Person updatePerson = personService.updatePerson(person);
        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    @CrossOrigin
    @PutMapping("/docs")
    public ResponseEntity<Doc> updateDoc(@RequestBody Doc doc){
        Doc updateDoc = docsService.updateDoc(doc);
        return new ResponseEntity<>(doc, HttpStatus.OK);
    }
    @CrossOrigin
    @PutMapping("/company")
    public ResponseEntity<Company> updateCompany(@RequestBody Company company){
        Company newCompany = companyService.updateCompany(company);
        return new ResponseEntity<>(company,HttpStatus.OK);
    }
    @CrossOrigin
    @PutMapping("/events")
    public ResponseEntity<Eveniment> updateEveniment(@RequestBody Eveniment eveniment){
        Eveniment newEveniment = eventService.updateEveniment(eveniment);
        return new ResponseEntity<>(eveniment, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/events/byMonth/{month}")
    public ResponseEntity<List<Eveniment>> getEventByMonth(@PathVariable("month") Integer month){
        List<Eveniment> eventsByMonth =eventService.searchByMonth(month);
        return new ResponseEntity<>(eventsByMonth, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/docs/delete/{id}")
    public void deleteDoc (@PathVariable("id") String id ){
        docsService.deleteDoc(id);
        System.out.println("deleted in MainController" + id);

    }
    @CrossOrigin
    @GetMapping("/events")
    public ResponseEntity<List<Eveniment>> findAllEvents() {
        List<Eveniment> eveniments = eventService.findAllEvents();
        return new ResponseEntity<>(eveniments, HttpStatus.OK);
    }
    @CrossOrigin
    @GetMapping("/company/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable("id") String id){
        Company newCompany = companyService.findCompanyById(id);
        return new ResponseEntity<>(newCompany,HttpStatus.OK);
    }
    @CrossOrigin
    @GetMapping("/events/{eventId}")
    public ResponseEntity<Eveniment> findEventById(@PathVariable("eventId") String stringId) {

        Eveniment newEveniment = eventService.findEvenimentById(stringId);
        return new ResponseEntity<>(newEveniment,HttpStatus.OK);
    }
    @CrossOrigin
    @PostMapping("/events")
    public ResponseEntity<Eveniment> addEvent(@RequestBody Eveniment eveniment) {
        Eveniment savedEveniment = eventService.addEvent(eveniment);
        return new ResponseEntity<>(savedEveniment, HttpStatus.CREATED);
    }
    @CrossOrigin
    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> removeEvent(@PathVariable("eventId") String stringId) {
        eventService.removeEvent(stringId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @CrossOrigin
    @DeleteMapping("/person/{personId}")
    public ResponseEntity<Void> removePerson(@PathVariable("personId") String stringId) {
        personService.removePerson(stringId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    // @Deprecated
    //@CrossOrigin
    //@GetMapping("/copy")
    //public void copyData(){
      //  copyService.copyData();}
}
