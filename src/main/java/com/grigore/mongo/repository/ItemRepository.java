package com.grigore.mongo.repository;

import com.grigore.mongo.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, String> {
}
