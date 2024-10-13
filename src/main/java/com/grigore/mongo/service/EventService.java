package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.model.Eveniment;

import com.grigore.mongo.model.Person;
import com.grigore.mongo.repository.EventsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Month;
import java.util.*;


@Service
public class EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private final EventsRepository eventsRepository;
    private final PersonService personService;

    @Autowired
    public EventService(EventsRepository eventRepository, PersonService personService) {
        this.eventsRepository = eventRepository;
        this.personService = personService;
    }
    boolean isEmptyOrNull(Collection< ? > collection) {
        return (collection == null || collection.isEmpty());
    }
    public List<Eveniment> findAllEvents() {
        logger.info("Finding all events");
        return eventsRepository.findAll();
    }

    public Eveniment findEvenimentById(String eventId) {
        //logger.info("Searching for event by ID " + eventId);

        Eveniment eveniment= eventsRepository.findEvenimentById(eventId).orElseThrow(
                ()-> new UserNotFoundException("Entity by id " + eventId + " not found"));
        logger.info("Eveniment showed: " + eventId + " " + eveniment.getEventName());
        return eveniment;
    }

    public Eveniment addEvent(Eveniment eveniment) {
        // Save the event
        Eveniment savedEveniment = eventsRepository.save(eveniment);
        logger.info("Eveniment saved" + eveniment.getEventName());
        addEventAtPerson(eveniment.getPersonsId(),eveniment);


        // Update each person's list of events

            // Check if every person in the event has a valid ID and exists in the database


            return savedEveniment;
            //  return eventsRepository.save(event);



        }

   //adding eventId (updating persons eveniment array)at the persons that are mentionated in this eveniment.persons
    public void addEventAtPerson(List<String> personsId, Eveniment eveniment) {
        if(isEmptyOrNull(personsId)) { return; }
        // else {
             for (String person : personsId) {
                 Person existingPerson = personService.findPersonById(person);
                 if (existingPerson == null) {throw new IllegalArgumentException("Person with ID " + person + " does not exist.");}
                 if (existingPerson.getEventsID() == null) {
                         existingPerson.setEventsID(new ArrayList<>());
                         existingPerson.getEventsID().add(eveniment.getId());
                         personService.updatePerson(existingPerson);
                     } else {
                         existingPerson.addEvent(eveniment.getId()); // Assuming you have an addEvent method in the Person class

                         personService.updatePerson(existingPerson);
                     }
                 //}
             }
    }
                                                //personsId
public void removeEventsFromPerson(List<String> listOfPersonsIdToRemoveThisEvent, String eventId){
        for(String personId: listOfPersonsIdToRemoveThisEvent){
           Person personToUpdate = personService.findPersonById(personId);

            if (personToUpdate == null) {throw new IllegalArgumentException("PersonID  does not exist.");}
            List<String> eventsID = personToUpdate.getEventsID();
            if(eventsID != null && eventsID.contains(eventId)){
                personToUpdate.getEventsID().remove(eventId);
                System.out.println("Removed eventID: " + eventId + " at " + personToUpdate.getFirstName() + " " + personToUpdate.getLastName());
            }

            personService.updatePerson(personToUpdate);
        }
}
    public void removeEvent(String stringId) {
        Eveniment eventToDelete = findEvenimentById(stringId);
        removeEventsFromPerson(eventToDelete.getPersonsId(),eventToDelete.getId());
        eventsRepository.deleteById(stringId);
        System.out.println("Deleted event: " + eventToDelete.getEventName());
    }
//check if the new Eveniment has more or less Persons and update the Person.events field

    public Eveniment updateEveniment(Eveniment eveniment) {
        List<String> originalPersonsId = new ArrayList<>();
        logger.info("Eveniment before update" + findEvenimentById(eveniment.getId()));
        Optional<Eveniment> optionalEveniment = eventsRepository.findEvenimentById(eveniment.getId()); //PersonId[] from DB
        List<String> newPersonsId =eveniment.getPersonsId(); //PersonId[] from updated obj
        if (optionalEveniment.isPresent()) {
        originalPersonsId = optionalEveniment.get().getPersonsId();
        }

        // Check for null or empty lists
        if (isEmptyOrNull(originalPersonsId) && isEmptyOrNull(newPersonsId))
                  {System.out.println("Both lists are null");//do nothing
         //DB list is empty and updated lis has some persons
        } else if (isEmptyOrNull(originalPersonsId)&&!isEmptyOrNull(newPersonsId)) {
            System.out.println("Original list is null");
            // Handle the case where originalList is null and updatedList is not null
            addEventAtPerson(eveniment.getPersonsId(), eveniment); //add event at person from new list
           // Updated list is empty and DB list is not
        } else if (isEmptyOrNull(newPersonsId)&&!isEmptyOrNull(originalPersonsId)) {
            System.out.println("Edited list is null");
            // Handle the case where editedList is null
            //must delete this eventID from persons that are in original list
            removeEventsFromPerson(originalPersonsId,eveniment.getId());
        }  else {
            // Find elements that are new (present in editedList but not in originalList)
            //must be added to persons
            List<String> newElements = new ArrayList<>(newPersonsId);
            newElements.removeAll(originalPersonsId);

            // Find just elements that were removed (present in originalList but not in editedList)
            //must be removed from persons
            List<String> removedElements = new ArrayList<>(originalPersonsId);
            removedElements.removeAll(newPersonsId);
            //add this event to persons
            if(!isEmptyOrNull(newElements)){
                addEventAtPerson(newElements,eveniment);
            }
            //remove this event from persons
            if(!isEmptyOrNull(removedElements)){
                removeEventsFromPerson(removedElements, eveniment.getId());
            }
            System.out.println("New Elements: " + newElements);
            System.out.println("Removed Elements: " + removedElements);
        }
        logger.info("Eveniment after update: " + eveniment);
        return eventsRepository.save(eveniment);
    }

    public List<Eveniment> searchByMonth(Integer month) {
        List<Eveniment> newList = eventsRepository.findAll().stream().
                filter(eveniment -> {return eveniment.getEventDate().getMonth().equals(Month.of(month));}).
                sorted(Comparator.comparingInt(o -> o.getEventDate().getDayOfMonth())).toList();
        logger.info("Searched events for month: " + month);
        return newList;
    }
}

