package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.immich.ImmichService;
import com.grigore.mongo.model.Person;
import com.grigore.mongo.model.PersonPhoto;
import com.grigore.mongo.repository.PersonPhotoRepository;
import org.bson.types.Binary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

@Service
public class PersonPhotoService {

    private final PersonPhotoRepository personPhotoRepository;
    private final PersonService personService;
    private final ImmichService immichService;

    public PersonPhotoService(PersonPhotoRepository personPhotoRepository, PersonService personService,
                               ImmichService immichService) {
        this.personPhotoRepository = personPhotoRepository;
        this.personService = personService;
        this.immichService = immichService;
    }

    public PersonPhoto getPhoto(String personId) {
        return personPhotoRepository.findByPersonId(personId).orElseThrow(
                () -> new UserNotFoundException("No photo saved for person " + personId));
    }

    public PersonPhoto savePhoto(String personId, MultipartFile file) {
        personService.findPersonById(personId); // 404s if the person doesn't exist

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Photo file is empty.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Photo must be an image file.");
        }
        try {
            return savePhotoBytes(personId, contentType, file.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read the uploaded photo.");
        }
    }

    // One-time copy, not a live link: once imported, the photo lives in our
    // own storage exactly like an uploaded one, so nothing here depends on
    // Immich being reachable again afterward.
    public PersonPhoto importFromImmich(String personId) {
        Person person = personService.findPersonById(personId);
        String immichPersonId = person.getImmichPersonId();
        if (immichPersonId == null || immichPersonId.isBlank()) {
            throw new IllegalArgumentException("Person is not linked to an Immich person.");
        }

        ResponseEntity<byte[]> response = immichService.getThumbnail(immichPersonId);
        byte[] bytes = response.getBody();
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Immich returned no thumbnail for this person.");
        }
        String contentType = response.getHeaders().getContentType() != null
                ? response.getHeaders().getContentType().toString()
                : "image/jpeg";
        return savePhotoBytes(personId, contentType, bytes);
    }

    public void deletePhoto(String personId) {
        personPhotoRepository.deleteByPersonId(personId);
    }

    private PersonPhoto savePhotoBytes(String personId, String contentType, byte[] bytes) {
        PersonPhoto photo = personPhotoRepository.findByPersonId(personId).orElseGet(PersonPhoto::new);
        photo.setPersonId(personId);
        photo.setContentType(contentType);
        photo.setUploadedAt(Instant.now());
        photo.setData(new Binary(bytes));
        return personPhotoRepository.save(photo);
    }
}
