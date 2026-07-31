package com.grigore.mongo.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Document(collection = "events")
public class Eveniment {
    @Id
    private String id;
    @NotBlank(message = "Event name is required")
    private String eventName;
    @NotNull(message = "Event date is required")
    private LocalDate eventDate;
    private String description;
    @Transient
    private Integer age;

    private List<String> personsId;

    public Eveniment() {
    }

    public Eveniment(String id, String eventName, LocalDate eventDate, String description, Integer age, List<String> personsId) {
        this.id = id;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.description = description;
        this.age = age;
        this.personsId = personsId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAge() {
        return Period.between(eventDate, LocalDate.now()).getYears();
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPersonsId() {
        return personsId;
    }

    public void setPersonsId(List<String> personsId) {
        this.personsId = personsId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Eveniment Details:\n");
        sb.append("----------------------------\n");
        sb.append("ID: ").append(id != null ? id : "N/A").append("\n");
        sb.append("Event Name: ").append(eventName != null ? eventName : "N/A").append("\n");
        sb.append("Event Date: ").append(eventDate != null ? eventDate : "N/A").append("\n");
        sb.append("Description: ").append(description != null ? description : "N/A").append("\n");
        sb.append("Age: ").append(getAge() != null ? getAge() + " years" : "N/A").append("\n");
        sb.append("Persons IDs: ").append(personsId != null && !personsId.isEmpty() ? String.join(", ", personsId) : "None").append("\n");
        sb.append("----------------------------\n");
        return sb.toString();
    }
}