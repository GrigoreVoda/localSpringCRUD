package com.grigore.mongo.immich;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImmichServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private ImmichService serviceWithKey() {
        return new ImmichService(restTemplate, "http://immich.local", "test-api-key");
    }

    @Test
    void listPeopleStopsWhenHasNextPageIsFalse() {
        ImmichService service = serviceWithKey();
        ImmichPeopleResponse onlyPage = new ImmichPeopleResponse();
        ImmichPerson person = new ImmichPerson();
        person.setId("p1");
        person.setName("Alice");
        onlyPage.setPeople(List.of(person));
        onlyPage.setHasNextPage(false);

        when(restTemplate.exchange(contains("page=1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(ImmichPeopleResponse.class)))
                .thenReturn(ResponseEntity.ok(onlyPage));

        List<ImmichPerson> result = service.listPeople();

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getName());
    }

    @Test
    void listPeopleFollowsPaginationUntilLastPage() {
        ImmichService service = serviceWithKey();

        ImmichPeopleResponse page1 = new ImmichPeopleResponse();
        ImmichPerson p1 = new ImmichPerson();
        p1.setId("p1");
        page1.setPeople(List.of(p1));
        page1.setHasNextPage(true);

        ImmichPeopleResponse page2 = new ImmichPeopleResponse();
        ImmichPerson p2 = new ImmichPerson();
        p2.setId("p2");
        page2.setPeople(List.of(p2));
        page2.setHasNextPage(false);

        when(restTemplate.exchange(contains("page=1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(ImmichPeopleResponse.class)))
                .thenReturn(ResponseEntity.ok(page1));
        when(restTemplate.exchange(contains("page=2"), eq(HttpMethod.GET), any(HttpEntity.class), eq(ImmichPeopleResponse.class)))
                .thenReturn(ResponseEntity.ok(page2));

        List<ImmichPerson> result = service.listPeople();

        assertEquals(2, result.size());
        assertEquals(List.of("p1", "p2"), result.stream().map(ImmichPerson::getId).toList());
    }

    @Test
    void missingApiKeyThrowsImmichUnavailableInsteadOfCallingImmich() {
        ImmichService service = new ImmichService(restTemplate, "http://immich.local", "");

        ImmichUnavailableException ex = assertThrows(ImmichUnavailableException.class, service::listPeople);
        assertTrue(ex.getMessage().contains("IMMICH_API_KEY"));
    }

    @Test
    void unreachableImmichWrapsIntoImmichUnavailableException() {
        ImmichService service = serviceWithKey();
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(ImmichPeopleResponse.class)))
                .thenThrow(new ResourceAccessException("Connection refused"));

        assertThrows(ImmichUnavailableException.class, service::listPeople);
    }

    @Test
    void updatePersonSendsPutWithApiKeyHeader() {
        ImmichService service = serviceWithKey();
        ImmichPersonUpdate update = new ImmichPersonUpdate();
        update.setBirthDate("1990-01-01");
        ImmichPerson updated = new ImmichPerson();
        updated.setId("p1");
        updated.setBirthDate("1990-01-01");

        when(restTemplate.exchange(eq("http://immich.local/api/people/p1"), eq(HttpMethod.PUT), any(HttpEntity.class), eq(ImmichPerson.class)))
                .thenReturn(ResponseEntity.ok(updated));

        ImmichPerson result = service.updatePerson("p1", update);

        assertEquals("1990-01-01", result.getBirthDate());
    }
}
