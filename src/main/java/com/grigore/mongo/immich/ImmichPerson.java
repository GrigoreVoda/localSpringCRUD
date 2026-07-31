package com.grigore.mongo.immich;

/**
 * Mirrors Immich's PersonResponseDto (server/src/dtos/person.dto.ts). Only
 * the fields this app actually uses are included.
 *
 * Getters/setters are named getIsHidden/getIsFavorite (not the bare-boolean
 * isHidden()/isFavorite() convention) on purpose: Jackson derives the JSON
 * property name from a "getX" method by stripping "get" and decapitalizing,
 * so getIsHidden() maps to "isHidden" - matching Immich's actual field name
 * exactly, with no @JsonProperty annotation needed.
 */
public class ImmichPerson {
    private String id;
    private String name;
    private String birthDate;
    private String thumbnailPath;
    private boolean isHidden;
    private boolean isFavorite;
    private String color;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
