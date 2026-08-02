package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.immich.ImmichService;
import com.grigore.mongo.immich.ImmichUnavailableException;
import com.grigore.mongo.model.Person;
import com.grigore.mongo.model.PersonPhoto;
import com.grigore.mongo.repository.PersonPhotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonPhotoServiceTest {

    @Mock
    private PersonPhotoRepository personPhotoRepository;
    @Mock
    private PersonService personService;
    @Mock
    private ImmichService immichService;

    private PersonPhotoService photoService;

    @BeforeEach
    void setUp() {
        photoService = new PersonPhotoService(personPhotoRepository, personService, immichService);
    }

    @Test
    void savePhotoCreatesNewEntryWhenNoneExists() {
        when(personService.findPersonById("p1")).thenReturn(new Person());
        when(personPhotoRepository.findByPersonId("p1")).thenReturn(Optional.empty());
        when(personPhotoRepository.save(any(PersonPhoto.class))).thenAnswer(inv -> inv.getArgument(0));
        MultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "fake-bytes".getBytes());

        PersonPhoto saved = photoService.savePhoto("p1", file);

        assertEquals("p1", saved.getPersonId());
        assertEquals("image/jpeg", saved.getContentType());
        assertArrayEquals("fake-bytes".getBytes(), saved.getData().getData());
    }

    @Test
    void savePhotoReplacesExistingEntryInsteadOfDuplicating() {
        PersonPhoto existing = new PersonPhoto();
        existing.setId("photo1");
        existing.setPersonId("p1");
        when(personService.findPersonById("p1")).thenReturn(new Person());
        when(personPhotoRepository.findByPersonId("p1")).thenReturn(Optional.of(existing));
        when(personPhotoRepository.save(any(PersonPhoto.class))).thenAnswer(inv -> inv.getArgument(0));
        MultipartFile file = new MockMultipartFile("file", "new.png", "image/png", "new-bytes".getBytes());

        PersonPhoto saved = photoService.savePhoto("p1", file);

        assertEquals("photo1", saved.getId()); // same document, updated in place
        assertEquals("image/png", saved.getContentType());
    }

    @Test
    void savePhotoRejectsNonImageContentType() {
        when(personService.findPersonById("p1")).thenReturn(new Person());
        MultipartFile file = new MockMultipartFile("file", "doc.pdf", "application/pdf", "bytes".getBytes());

        assertThrows(IllegalArgumentException.class, () -> photoService.savePhoto("p1", file));
        verify(personPhotoRepository, never()).save(any());
    }

    @Test
    void savePhotoThrowsWhenPersonDoesNotExist() {
        when(personService.findPersonById("ghost")).thenThrow(new UserNotFoundException("not found"));
        MultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "bytes".getBytes());

        assertThrows(UserNotFoundException.class, () -> photoService.savePhoto("ghost", file));
    }

    @Test
    void getPhotoThrowsWhenNoneSaved() {
        when(personPhotoRepository.findByPersonId("p1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> photoService.getPhoto("p1"));
    }

    @Test
    void importFromImmichSavesThumbnailWhenLinked() {
        Person person = new Person();
        person.setImmichPersonId("immich-1");
        when(personService.findPersonById("p1")).thenReturn(person);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        when(immichService.getThumbnail("immich-1"))
                .thenReturn(new ResponseEntity<>("thumb-bytes".getBytes(), headers, 200));
        when(personPhotoRepository.findByPersonId("p1")).thenReturn(Optional.empty());
        when(personPhotoRepository.save(any(PersonPhoto.class))).thenAnswer(inv -> inv.getArgument(0));

        PersonPhoto saved = photoService.importFromImmich("p1");

        assertEquals("p1", saved.getPersonId());
        assertArrayEquals("thumb-bytes".getBytes(), saved.getData().getData());
    }

    @Test
    void importFromImmichThrowsWhenPersonNotLinked() {
        when(personService.findPersonById("p1")).thenReturn(new Person());

        assertThrows(IllegalArgumentException.class, () -> photoService.importFromImmich("p1"));
        verify(personPhotoRepository, never()).save(any());
    }

    @Test
    void importFromImmichPropagatesImmichUnavailable() {
        Person person = new Person();
        person.setImmichPersonId("immich-1");
        when(personService.findPersonById("p1")).thenReturn(person);
        when(immichService.getThumbnail("immich-1")).thenThrow(new ImmichUnavailableException("down"));

        assertThrows(ImmichUnavailableException.class, () -> photoService.importFromImmich("p1"));
    }

    @Test
    void deletePhotoDelegatesToRepository() {
        photoService.deletePhoto("p1");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(personPhotoRepository).deleteByPersonId(captor.capture());
        assertEquals("p1", captor.getValue());
    }
}
