package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.model.PersonPhoto;
import com.grigore.mongo.repository.PersonPhotoRepository;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

@Service
public class PersonPhotoService {

    private final PersonPhotoRepository personPhotoRepository;
    private final PersonService personService;

    public PersonPhotoService(PersonPhotoRepository personPhotoRepository, PersonService personService) {
        this.personPhotoRepository = personPhotoRepository;
        this.personService = personService;
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

        PersonPhoto photo = personPhotoRepository.findByPersonId(personId).orElseGet(PersonPhoto::new);
        photo.setPersonId(personId);
        photo.setContentType(contentType);
        photo.setUploadedAt(Instant.now());
        try {
            photo.setData(new Binary(file.getBytes()));
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read the uploaded photo.");
        }
        return personPhotoRepository.save(photo);
    }

    public void deletePhoto(String personId) {
        personPhotoRepository.deleteByPersonId(personId);
    }
}
