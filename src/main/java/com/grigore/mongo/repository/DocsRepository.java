package com.grigore.mongo.repository;

import com.grigore.mongo.model.Doc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DocsRepository extends MongoRepository<Doc, String> {
    Optional<Doc> findDocsById(String id);
}
