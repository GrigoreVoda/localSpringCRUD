package com.grigore.mongo.controller;

import com.grigore.mongo.model.PersonPhoto;
import com.grigore.mongo.service.PersonPhotoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/person")
@CrossOrigin(origins = "*")
public class PersonPhotoController {

    private final PersonPhotoService personPhotoService;

    public PersonPhotoController(PersonPhotoService personPhotoService) {
        this.personPhotoService = personPhotoService;
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String id) {
        PersonPhoto photo = personPhotoService.getPhoto(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .body(photo.getData().getData());
    }

    @PutMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPhoto(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        personPhotoService.savePhoto(id, file);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable String id) {
        personPhotoService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }
}
