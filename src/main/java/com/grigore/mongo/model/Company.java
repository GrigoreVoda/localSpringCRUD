package com.grigore.mongo.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "companies")

public class Company {
    @Id
    private String id;
    @NotBlank(message = "Company name is required")
    private String name;
    private String street;
    private String streetNumber;
    private String city;
    private String postCode;
    private String openHours;//00-24 zile
    private List<DeliveryPoint> deliveryPoint;
    private List<String> contacts;
    private List<String> workers;
    private String comments;

    public Company() {
    }



    public Company(String id, String name, String street, String streetNumber, String city, String postCode, String openHours, List<String> contacts, List<String> workers, List<DeliveryPoint> deliveryPoint, String comments) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.postCode = postCode;
        this.openHours = openHours;
        this.contacts = contacts;
        this.workers = workers;
        this.comments = comments;
        this.deliveryPoint = deliveryPoint;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public List<String> getWorkers() {
        return workers;
    }

    public void setWorkers(List<String> workers) {
        this.workers = workers;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<DeliveryPoint> getDeliveryPoint() {
        return deliveryPoint;
    }

    public void setDeliveryPoints(List<DeliveryPoint> deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }
}
