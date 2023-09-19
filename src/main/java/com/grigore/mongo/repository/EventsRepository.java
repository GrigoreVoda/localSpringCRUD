package com.grigore.mongo.repository;

import com.grigore.mongo.model.Eveniment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EventsRepository extends MongoRepository<Eveniment, String> {
Optional<Eveniment> findEvenimentById(String evenimetId);
}
