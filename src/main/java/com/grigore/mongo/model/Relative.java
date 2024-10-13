package com.grigore.mongo.model;

public class Relative {
    private String relativePersonId;
    private String relativeType;

    public Relative() {
    }

    public Relative(String relativePersonId, String relativeType) {
        this.relativePersonId = relativePersonId;
        this.relativeType = relativeType;
    }

    public String getRelativePersonId() {
        return relativePersonId;
    }

    public void setRelativePersonId(String relativePersonId) {
        this.relativePersonId = relativePersonId;
    }

    public String getRelativeType() {
        return relativeType;
    }

    public void setRelativeType(String relativeType) {
        this.relativeType = relativeType;
    }
}
