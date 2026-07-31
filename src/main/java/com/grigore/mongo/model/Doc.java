package com.grigore.mongo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Document(collection = "docs")
public class Doc {
    @Id
    private String id;
    @NotBlank(message = "Document type is required")
    private String docType;
    @NotBlank(message = "Document number is required")
    private String docNumber;
    @NotNull(message = "Expire date is required")
    private LocalDate expireDate;
    @Transient
    private int daysUntilExpire;

    public Doc(String id, String docType, String docNumber, LocalDate expireDate, int daysUntilExpire) {
        this.id = id;
        this.docType = docType;
        this.docNumber = docNumber;
        this.expireDate = expireDate;
        this.daysUntilExpire = daysUntilExpire;
    }
    public Doc(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public int getDaysUntilExpire() {
        return (int) ChronoUnit.DAYS.between(LocalDate.now(),expireDate);
    }

    //public void setDaysUntilExpire(int daysUntilExpire) {this.daysUntilExpire = daysUntilExpire;}
}
