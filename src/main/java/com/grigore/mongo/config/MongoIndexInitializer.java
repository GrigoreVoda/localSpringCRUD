package com.grigore.mongo.config;

import com.grigore.mongo.model.Person;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.stereotype.Component;

/**
 * Person text search (GET /person/search/{text}) runs a MongoDB $text query,
 * which requires a text index to exist on the collection or every search
 * fails with "text index required for $text query". Ensure it exists on
 * startup instead of requiring it to be created by hand - ensureIndex() is a
 * no-op if a matching index is already there.
 *
 * Failures here are logged, not rethrown: this must not stop the app from
 * starting just because MongoDB isn't reachable yet (it wasn't a hard
 * dependency of startup before this class existed either).
 */
@Component
public class MongoIndexInitializer {

    private static final Logger logger = LoggerFactory.getLogger(MongoIndexInitializer.class);

    private final MongoTemplate mongoTemplate;

    public MongoIndexInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void ensurePersonTextIndex() {
        TextIndexDefinition textIndex = TextIndexDefinition.builder()
                .onField("firstName")
                .onField("lastName")
                .onField("maidenName")
                .onField("email")
                .onField("comments")
                .onField("phone")
                .onField("address.city")
                .onField("address.street")
                .build();
        try {
            mongoTemplate.indexOps(Person.class).ensureIndex(textIndex);
        } catch (Exception e) {
            logger.warn("Could not ensure the persons text index on startup - MongoDB may not be reachable yet. "
                    + "Person text search will keep failing with 'text index required' until this succeeds; "
                    + "restart the app once MongoDB is up.", e);
        }
    }
}
