package com.grigore.mongo.model;

import jakarta.validation.constraints.NotBlank;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@Document(collection = "items")
public class Item {
    @Id
    private String id;
    @NotBlank(message = "Item name is required")
    private String itemName;
    private String description;
    @DBRef
    private Place place; // reference to Location
    private LocalDate expireDate; // optional
    private String barcode; // optional for barcode
    private Binary photo; // optional for storing a photo of the item
    @Transient
    private int daysUntilExpire;

    public Item() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Place getLocation() {
        return place;
    }

    public void setLocation(Place place) {
        this.place = place;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Binary getPhoto() {
        return photo;
    }

    public void setPhoto(Binary photo) {
        this.photo = photo;
    }

    public int getDaysUntilExpire() {
        if(expireDate==null)
        {return 0;}
        else {
            return (int) ChronoUnit.DAYS.between(LocalDate.now(), expireDate);
        }
    }
}
