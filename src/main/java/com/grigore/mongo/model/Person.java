package com.grigore.mongo.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String maidenName;
    // Deliberately optional: not everyone's exact birth date is known,
    // especially for older relatives kept mainly for genealogical records.
    // Age/zodiac/month-search all degrade gracefully (null/skip) when unset.
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    // The *Known flags let a LocalDate hold a best-guess value (e.g. day 1)
    // while marking which parts are actually unknown, so calculations that
    // need precision (zodiac, age, birth-month search) can skip a person
    // instead of silently trusting a guessed day/month/year. Default true so
    // existing records without these fields are treated as fully known.
    private Boolean dateOfBirthDayKnown = true;
    private Boolean dateOfBirthMonthKnown = true;
    private Boolean dateOfBirthYearKnown = true;
    private Boolean dateOfDeathDayKnown = true;
    private Boolean dateOfDeathMonthKnown = true;
    private Boolean dateOfDeathYearKnown = true;
    private Boolean isAlive;

    private List<String> phone;
    @Email(message = "Email must be a valid address")
    private String email;
    @NotNull(message = "Gender is required")
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
    private List<Relative> relatives;
    // Excluded from normal browsing/search in the UI - e.g. contacts kept only
    // for genealogical record-keeping. Not a security/access boundary.
    private boolean hidden;


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
                  List<String> eventsID,
                  List<Relative> relatives) {
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
        this.relatives = relatives;
    }

    // Old documents predating these flags have them absent, not explicitly
    // false - treat that (and any other null) as "known" rather than
    // silently hiding data that was always fully known.
    private static boolean isKnown(Boolean flag) {
        return !Boolean.FALSE.equals(flag);
    }

    public Integer getAge() {
        if (dateOfBirth == null || !isKnown(dateOfBirthYearKnown)) {
            return null;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public String getZodiac() {
        if (dateOfBirth == null || !isKnown(dateOfBirthMonthKnown) || !isKnown(dateOfBirthDayKnown)) {
            return null;
        }
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

    public Boolean getDateOfBirthDayKnown() {
        return dateOfBirthDayKnown;
    }

    public void setDateOfBirthDayKnown(Boolean dateOfBirthDayKnown) {
        this.dateOfBirthDayKnown = dateOfBirthDayKnown;
    }

    public Boolean getDateOfBirthMonthKnown() {
        return dateOfBirthMonthKnown;
    }

    public void setDateOfBirthMonthKnown(Boolean dateOfBirthMonthKnown) {
        this.dateOfBirthMonthKnown = dateOfBirthMonthKnown;
    }

    public Boolean getDateOfBirthYearKnown() {
        return dateOfBirthYearKnown;
    }

    public void setDateOfBirthYearKnown(Boolean dateOfBirthYearKnown) {
        this.dateOfBirthYearKnown = dateOfBirthYearKnown;
    }

    public Boolean getDateOfDeathDayKnown() {
        return dateOfDeathDayKnown;
    }

    public void setDateOfDeathDayKnown(Boolean dateOfDeathDayKnown) {
        this.dateOfDeathDayKnown = dateOfDeathDayKnown;
    }

    public Boolean getDateOfDeathMonthKnown() {
        return dateOfDeathMonthKnown;
    }

    public void setDateOfDeathMonthKnown(Boolean dateOfDeathMonthKnown) {
        this.dateOfDeathMonthKnown = dateOfDeathMonthKnown;
    }

    public Boolean getDateOfDeathYearKnown() {
        return dateOfDeathYearKnown;
    }

    public void setDateOfDeathYearKnown(Boolean dateOfDeathYearKnown) {
        this.dateOfDeathYearKnown = dateOfDeathYearKnown;
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

    public List<Relative> getRelatives() {
        return relatives;
    }

    public void setRelatives(List<Relative> relatives) {
        this.relatives = relatives;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Person Details:\n");
        sb.append("----------------------------\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("First Name: ").append(firstName).append("\n");
        sb.append("Last Name: ").append(lastName).append("\n");
        sb.append("Maiden Name: ").append(maidenName != null ? maidenName : "N/A").append("\n");
        sb.append("Date of Birth: ").append(dateOfBirth != null ? dateOfBirth : "N/A").append("\n");
        sb.append("Date of Death: ").append(dateOfDeath != null ? dateOfDeath : "N/A").append("\n");
        sb.append("Is Alive: ").append(isAlive != null ? isAlive : "N/A").append("\n");
        sb.append("Age: ").append(age != null ? age : "N/A").append("\n");
        sb.append("Zodiac Sign: ").append(zodiac != null ? zodiac : "N/A").append("\n");
        sb.append("Gender: ").append(gender != null ? gender : "N/A").append("\n");

        sb.append("Phones: ");
        if (phone != null && !phone.isEmpty()) {
            sb.append(String.join(", ", phone));
        } else {
            sb.append("N/A");
        }
        sb.append("\n");

        sb.append("Email: ").append(email != null ? email : "N/A").append("\n");

        sb.append("Addresses: ");
        if (address != null && !address.isEmpty()) {
            for (Address addr : address) {
                sb.append("\n  - ").append(addr.toString());
            }
        } else {
            sb.append("N/A");
        }
        sb.append("\n");

        sb.append("Cars: ");
        if (carsList != null && !carsList.isEmpty()) {
            for (Cars car : carsList) {
                sb.append("\n  - ").append(car.toString());
            }
        } else {
            sb.append("N/A");
        }
        sb.append("\n");

        sb.append("Comments: ").append(comments != null ? comments : "N/A").append("\n");

        sb.append("Events ID: ");
        if (eventsID != null && !eventsID.isEmpty()) {
            sb.append(String.join(", ", eventsID));
        } else {
            sb.append("N/A");
        }
        sb.append("\n");

        sb.append("Relatives: ");
        if (relatives != null && !relatives.isEmpty()) {
            for (Relative relative : relatives) {
                sb.append("\n  - ").append(relative.toString());
            }
        } else {
            sb.append("N/A");
        }
        sb.append("\n");
        sb.append("----------------------------\n");

        return sb.toString();
    }

   public void addEvent(String savedString) {this.getEventsID().add(savedString);}
}
