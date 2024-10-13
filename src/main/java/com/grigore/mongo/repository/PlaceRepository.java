package com.grigore.mongo.repository;

import com.grigore.mongo.model.Place;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlaceRepository extends MongoRepository<Place, String> {
}
