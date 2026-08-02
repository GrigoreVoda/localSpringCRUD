package com.grigore.mongo.model;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Stored in its own collection rather than as a field on Person, so fetching
 * or listing persons never drags binary image data along - the photo is only
 * loaded when /person/{id}/photo is actually requested.
 */
@Document(collection = "person_photos")
public class PersonPhoto {
    @Id
    private String id;
    private String personId;
    private String contentType;
    private Binary data;
    private Instant uploadedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Binary getData() {
        return data;
    }

    public void setData(Binary data) {
        this.data = data;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
