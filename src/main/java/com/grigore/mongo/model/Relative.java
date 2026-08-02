package com.grigore.mongo.model;

public class Relative {
    private String relativePersonId;
    private String relativeType;
    private RelationOrigin origin = RelationOrigin.BIOLOGICAL;

    public Relative() {
    }

    public Relative(String relativePersonId, String relativeType) {
        this.relativePersonId = relativePersonId;
        this.relativeType = relativeType;
    }

    public Relative(String relativePersonId, String relativeType, RelationOrigin origin) {
        this.relativePersonId = relativePersonId;
        this.relativeType = relativeType;
        this.origin = origin == null ? RelationOrigin.BIOLOGICAL : origin;
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

    public RelationOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(RelationOrigin origin) {
        this.origin = origin;
    }
}
