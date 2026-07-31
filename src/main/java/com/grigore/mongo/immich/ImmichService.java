package com.grigore.mongo.immich;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImmichService {

    // Safety cap so a misbehaving hasNextPage flag can't loop forever.
    private static final int MAX_PAGES = 20;
    private static final int PAGE_SIZE = 1000;

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public ImmichService(RestTemplate restTemplate,
                          @Value("${immich.base-url}") String baseUrl,
                          @Value("${immich.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public List<ImmichPerson> listPeople() {
        List<ImmichPerson> all = new ArrayList<>();
        int page = 1;
        while (page <= MAX_PAGES) {
            String url = baseUrl + "/api/people?withHidden=true&page=" + page + "&size=" + PAGE_SIZE;
            ImmichPeopleResponse body = exchange(url, HttpMethod.GET, null, ImmichPeopleResponse.class);
            if (body == null || body.getPeople() == null) {
                break;
            }
            all.addAll(body.getPeople());
            if (!body.getHasNextPage()) {
                break;
            }
            page++;
        }
        return all;
    }

    public ImmichPerson getPerson(String id) {
        return exchange(baseUrl + "/api/people/" + id, HttpMethod.GET, null, ImmichPerson.class);
    }

    public ImmichPerson updatePerson(String id, ImmichPersonUpdate update) {
        return exchange(baseUrl + "/api/people/" + id, HttpMethod.PUT, update, ImmichPerson.class);
    }

    public ResponseEntity<byte[]> getThumbnail(String id) {
        String url = baseUrl + "/api/people/" + id + "/thumbnail";
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(authHeaders()), byte[].class);
            return ResponseEntity.status(response.getStatusCode())
                    .contentType(response.getHeaders().getContentType())
                    .body(response.getBody());
        } catch (RestClientException e) {
            throw new ImmichUnavailableException("Could not fetch thumbnail from Immich: " + e.getMessage());
        }
    }

    private <T> T exchange(String url, HttpMethod method, Object body, Class<T> responseType) {
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, method, new HttpEntity<>(body, authHeaders()), responseType);
            return response.getBody();
        } catch (RestClientException e) {
            throw new ImmichUnavailableException("Could not reach Immich at " + baseUrl + ": " + e.getMessage());
        }
    }

    private HttpHeaders authHeaders() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ImmichUnavailableException("Immich integration is not configured - set IMMICH_API_KEY.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        return headers;
    }
}
