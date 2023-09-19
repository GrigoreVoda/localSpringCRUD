package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.model.Eveniment;

import com.grigore.mongo.model.Person;
import com.grigore.mongo.repository.EventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
public class EventService {
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
        return eventsRepository.findAll();
    }

    public Eveniment findEvenimentById(String eventId) {

        return eventsRepository.findEvenimentById(eventId).orElseThrow(
                ()-> new UserNotFoundException("Entity by id " + eventId + " not found"));
    }

    public Eveniment addEvent(Eveniment eveniment) {
        // Save the event
        Eveniment savedEveniment = eventsRepository.save(eveniment);
        addEventAtPerson(eveniment.getPersonsId(),eveniment);


        // Update each person's list of events

            // Check if every person in the event has a valid ID and exists in the database


            return savedEveniment;
            //  return eventsRepository.save(event);



        }

   //adding eventId (updating persons eveniment array)at the persons that are mentionated in this eveniment.persons
    private void addEventAtPerson(List<String> personsId, Eveniment eveniment) {//Eveniment eveniment{
        if(isEmptyOrNull(personsId)) {  }
         else {
             for (String person : personsId) {
                 Person existingPerson = personService.findPersonById(person);
                 if (existingPerson == null) {throw new IllegalArgumentException("Person with ID " + person + " does not exist.");}
                     if (existingPerson.getEventsID() == null) {
                         existingPerson.setEventsID(new ArrayList<>());
                         existingPerson.getEventsID().add(eveniment.getId());
                         personService.updatePerson(existingPerson);
                     } else {
                         existingPerson.addEvent(eveniment.getId()); // Assuming you have an addEvent method in the Person class}

                         personService.updatePerson(existingPerson);
                     }
                 }
             }
    }
                                                //personsId
public void removeEventsFromPerson(List<String> listOfPersonsIdToRemoveThisEvent, String eventId){
        for(String personId: listOfPersonsIdToRemoveThisEvent){
           Person personToUpdate = personService.findPersonById(personId);

            if (personToUpdate == null) {throw new IllegalArgumentException("PersonID " + personToUpdate.getId() + " does not exist.");}
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



       /* if (originalPersonsId==null||originalPersonsId.isEmpty()){
            if(!newPersonsId.isEmpty()){
                for (String newPerson: newPersonsId) {
                    addEventAtPerson(findEvenimentById(eveniment.getId()));
                }

            }
        }*/




        /*    // Create a copy of originalPersonsId to avoid modifying the original list
            List<String> originalCopy = new ArrayList<>(originalPersonsId); //copy of PersonId[] from DB

            // Use removeAll to remove elements that exist in both lists, leaving only new elements in originalCopy
            originalCopy.removeAll(newPersonsId);

            if (!originalCopy.isEmpty()) {
                for (String newEventId: originalCopy) {
                    addEventAtPerson(findEvenimentById(newEventId));
                }
            } else {
                System.out.println("There are no new elements in newPersonsId");
            }

            // The removedPersons list will contain the elements that will be removed from originalPersonsId.
            List<String> removedPersons = new ArrayList<>();

            for (String personId : originalPersonsId) {
                if (!newPersonsId.contains(personId)) {removedPersons.add(personId);}
            }
            //removes this eveniment at Person that was removed at update
            if(!removedPersons.isEmpty()){
                removeEventsFromPerson(removedPersons, eveniment.getId());
            }*/


        return eventsRepository.save(eveniment);
    }
}

