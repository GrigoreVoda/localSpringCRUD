package com.grigore.mongo.immich;

/**
 * Mirrors (a subset of) Immich's PersonUpdateDto. Fields are nullable/omittable
 * on purpose - the frontend only sends what it actually wants to change, and
 * Immich's PUT /people/{id} treats this as a full update of the fields present.
 */
public class ImmichPersonUpdate {
    private String name;
    private String birthDate;
    private Boolean isHidden;
    private Boolean isFavorite;
    private String color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
