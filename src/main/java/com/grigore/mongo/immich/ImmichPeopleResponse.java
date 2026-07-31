package com.grigore.mongo.immich;

import java.util.List;

/**
 * Mirrors Immich's PeopleResponseDto - the paginated envelope GET /people
 * returns.
 */
public class ImmichPeopleResponse {
    private int total;
    private int hidden;
    private List<ImmichPerson> people;
    private boolean hasNextPage;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    public List<ImmichPerson> getPeople() {
        return people;
    }

    public void setPeople(List<ImmichPerson> people) {
        this.people = people;
    }

    public boolean getHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
}
