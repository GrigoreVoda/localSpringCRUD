package com.grigore.mongo.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;



@Document(collection = "persons")
public class Person {
    @Id
    private String id;

    private String firstName;

    private String lastName;

    private String maidenName;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private Boolean isAlive;

    private List<String> phone;
    private String email;
    private Gender gender;
    private List<Address> address;
    private List<Cars> carsList;
    private String comments;
    @Transient
    private Integer age;
    @Transient
    private String zodiac;

   //@DBRef(lazy = false)
    private List<String> eventsID;


    public Person(){}

    public Person(String id,
                  String firstName,
                  String lastName,
                  String maidenName,
                  LocalDate dateOfBirth,
                  LocalDate dateOfDeath,
                  Boolean isAlive,
                  List<String> phone,
                  String email,
                  Gender gender,
                  List<Address> address,
                  List<Cars> carsList,
                  String comments,
                  Integer age,
                  String zodiac,
                  List<String> eventsID) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.maidenName = maidenName;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;
        this.isAlive = isAlive;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.address = address;
        this.carsList = carsList;
        this.comments = comments;
        this.age = age;
        this.zodiac = zodiac;
        this.eventsID = eventsID;
    }

    public Integer getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public String getZodiac() {
        int month = dateOfBirth.getMonthValue();
        int day = dateOfBirth.getDayOfMonth();

        if ((month == 3 && day >= 21) || (month == 4 && day <= 20)) {
            return "Berbec";
        } else if ((month == 4 && day >= 21) || (month == 5 && day <= 20)) {
            return "Taur";
        } else if ((month == 5 && day >= 21) || (month == 6 && day <= 21)) {
            return "Gemeni";
        } else if ((month == 6 && day >= 22) || (month == 7 && day <= 22)) {
            return "Rac";
        } else if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) {
            return "Leu";
        } else if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) {
            return "Fecioara";
        } else if ((month == 9 && day >= 23) || (month == 10 && day <= 22)) {
            return "Balanta";
        } else if ((month == 10 && day >= 23) || (month == 11 && day <= 21)) {
            return "Scorpion";
        } else if ((month == 11 && day >= 22) || (month == 12 && day <= 20)) {
            return "Sagetator";
        } else if ((month == 12 && day >= 21) || (month == 1 && day <= 19)) {
            return "Capricorn";
        } else if ((month == 1 && day >= 20) || (month == 2 && day <= 18)) {
            return "Varsator";
        } else {
            return "Peste";
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMaidenName() {
        return maidenName;
    }

    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public Boolean getAlive() {
        return isAlive;
    }

    public void setAlive(Boolean alive) {
        isAlive = alive;
    }

    public List<String> getPhone() {
        return phone;
    }

    public void setPhone(List<String> phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public List<Cars> getCarsList() {
        return carsList;
    }

    public void setCarsList(List<Cars> carsList) {
        this.carsList = carsList;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setZodiac(String zodiac) {
        this.zodiac = zodiac;
    }

    public List<String> getEventsID() {
        return eventsID;
    }

    public void setEventsID(List<String> eventsID) {
        this.eventsID = eventsID;
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender=" + gender +
                '}';
    }

   public void addEvent(String savedString) {this.getEventsID().add(savedString);}
}
