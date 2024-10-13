package com.grigore.mongo.repository;

import com.grigore.mongo.model.Location;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LocationRepository extends MongoRepository<Location, String> {
}
