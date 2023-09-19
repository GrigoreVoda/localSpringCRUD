//package com.grigore.mongo.model;
//
//import jakarta.persistence.*;
//import org.springframework.format.annotation.DateTimeFormat;
//
//import java.io.Serializable;
//import java.time.LocalDate;
//import java.time.Period;
//
//@Deprecated
//    @Entity
//    @Table(name="person")
//    public class SqlPerson implements Serializable {
//
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        @Column(name = "person_id")
//        private Long id;
//        @Column(name ="first_name")
//        private String firstName;
//        @Column(name ="last_name")
//        private String lastName;
//        @DateTimeFormat(pattern = "dd-MM-YYYY")
//        @Column(name ="date_of_birth")
//        private LocalDate dateOfBirth;
//        @Column(name ="phone")
//        private String phone;
//        @Column(name ="email")
//        private String email;
//        @Column(name ="gender")
//        private String gender;
//        @Transient
//        private Integer age;
//
//        public Integer getAge() {
//            return Period.between(dateOfBirth, LocalDate.now()).getYears();
//        }
//
//        public void setAge(Integer age) {
//            this.age = age;
//        }
//
//        public SqlPerson() {
//        }
//
//        public SqlPerson(Long id, String firstName, String lastName, LocalDate dateOfBirth, String phone, String email, String gender) {
//            this.id = id;
//            this.firstName = firstName;
//            this.lastName = lastName;
//            this.dateOfBirth = dateOfBirth;
//            this.phone = phone;
//            this.email = email;
//            this.gender = gender;
//
//        }
//
//
//
//        public Long getId() {
//            return id;
//        }
//
//        public String getFirstName() {
//            return firstName;
//        }
//
//        public String getLastName() {
//            return lastName;
//        }
//
//        public LocalDate getDateOfBirth() {
//            return dateOfBirth;
//        }
//
//        public String getPhone() {
//            return phone;
//        }
//
//        public String getEmail() {
//            return email;
//        }
//
//        public String getGender() {
//            return gender;
//        }
//
//        public void setId(Long id) {
//            this.id = id;
//        }
//
//        public void setFirstName(String firstName) {
//            this.firstName = firstName;
//        }
//
//        public void setLastName(String lastName) {
//            this.lastName = lastName;
//        }
//
//        public void setDateOfBirth(LocalDate dateOfBirth) {
//            this.dateOfBirth = dateOfBirth;
//        }
//
//        public void setPhone(String phone) {
//            this.phone = phone;
//        }
//
//        public void setEmail(String email) {
//            this.email = email;
//        }
//
//        public void setGender(String gender) {
//            this.gender = gender;
//        }
//
//        @Override
//        public String toString() {
//            return "Person{" +
//                    "id=" + id +
//                    ", firstName='" + firstName + '\'' +
//                    ", lastName='" + lastName + '\'' +
//                    ", dateOfBirth=" + dateOfBirth +
//                    ", phone='" + phone + '\'' +
//                    ", email='" + email + '\'' +
//                    ", gender='" + gender + '\'' +
//                    '}';
//        }
//
//    }
//
