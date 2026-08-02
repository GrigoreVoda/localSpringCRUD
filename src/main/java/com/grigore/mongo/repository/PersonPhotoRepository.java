package com.grigore.mongo.repository;

import com.grigore.mongo.model.PersonPhoto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PersonPhotoRepository extends MongoRepository<PersonPhoto, String> {
    Optional<PersonPhoto> findByPersonId(String personId);

    void deleteByPersonId(String personId);
}
